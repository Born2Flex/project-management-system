import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL, API_TIMEOUT } from '@/utils/constants';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStorage = localStorage.getItem('auth-storage');
    if (authStorage) {
      try {
        const authData = JSON.parse(authStorage);
        const token = authData?.state?.token;
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
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status;
      const url = error.config?.url || '';
      
      if (status === 401) {
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
        
        console.error('Unauthorized access, redirecting to login');
        localStorage.removeItem('auth-storage');
        window.location.replace('/login');
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

