import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL, API_TIMEOUT } from '@/utils/constants';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

const refreshClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value: any) => void;
  reject: (error: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  
  failedQueue = [];
};

const refreshToken = async (): Promise<string | null> => {
  const authStorage = localStorage.getItem('auth-storage');
  if (!authStorage) {
    throw new Error('No auth storage found');
  }
  
  const authData = JSON.parse(authStorage);
  const refreshTokenValue = authData?.refreshToken || authData?.state?.refreshToken;
  
  if (!refreshTokenValue) {
    throw new Error('No refresh token found');
  }
  
  const response = await refreshClient.post('/auth/refresh', { refreshToken: refreshTokenValue });
  const { accessToken, refreshToken: newRefreshToken } = response.data;
  
  const newAuthData = {
    ...authData,
    token: accessToken,
    refreshToken: newRefreshToken,
  };
  
  if (authData.state) {
    newAuthData.state = {
      ...authData.state,
      token: accessToken,
      refreshToken: newRefreshToken,
    };
  }
  
  localStorage.setItem('auth-storage', JSON.stringify(newAuthData));
  return accessToken;
};

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStorage = localStorage.getItem('auth-storage');
    if (authStorage) {
      try {
        const authData = JSON.parse(authStorage);
        const token = authData?.token || authData?.state?.token;
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } catch (error) {
        console.error('Failed to parse auth storage:', error);
      }
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    
    if (error.response) {
      const status = error.response.status;
      const url = error.config?.url || '';
      
      if (status === 401 && !originalRequest._retry) {
        if (window.location.pathname.includes('/login') || 
            window.location.pathname.includes('/register')) {
          console.error('Authentication failed on login/register page');
          return Promise.reject(error);
        }
        
        if (url.includes('/refresh')) {
          console.error('Refresh token expired or invalid, redirecting to login');
          localStorage.removeItem('auth-storage');
          window.location.replace('/login');
          return Promise.reject(error);
        }
        
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          }).then(token => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return apiClient(originalRequest);
          }).catch(err => {
            return Promise.reject(err);
          });
        }
        
        originalRequest._retry = true;
        isRefreshing = true;
        
        try {
          console.log('Attempting to refresh token...');
          const newToken = await refreshToken();
          console.log('Token refreshed successfully');
          
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
          }
          
          processQueue(null, newToken);
          return apiClient(originalRequest);
          
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          processQueue(refreshError, null);
          
          localStorage.removeItem('auth-storage');
          window.location.replace('/login');
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }
      
      console.error('API Error:', error.response.data);
    } else if (error.request) {
      console.error('Network Error:', error.message);
    } else {
      console.error('Error:', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;

