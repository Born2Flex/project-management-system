import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { authApi } from '@/api/auth.api';
import { usersApi } from '@/api/users.api';
import { type LoginRequest, type RegisterRequest, type RefreshTokenRequest, UserRole } from '@/types/auth.types';
import { ROUTES } from '@/utils/constants';

interface ApiError extends Error {
  response?: {
    data?: unknown;
    status?: number;
  };
}

export const useAuth = () => {
  const navigate = useNavigate();
  const { setAuth, clearAuth, isAuthenticated } = useAuthStore();

  const loginMutation = useMutation({
    mutationFn: async (credentials: LoginRequest) => {
      const loginResponse = await authApi.login(credentials);
      
      setAuth(loginResponse.accessToken, loginResponse.refreshToken, {
        id: loginResponse.userId,
        username: '',
        name: '',
        email: credentials.email,
        role: UserRole.USER,
      });
      
      const user = await usersApi.getById(loginResponse.userId);
      
      return { ...loginResponse, user };
    },
    onSuccess: (data) => {
      setAuth(data.accessToken, data.refreshToken, data.user);
      navigate(ROUTES.PROJECTS);
    },
    onError: (error: ApiError) => {
      console.error('Login failed with error:', error);
      console.error('Error details:', {
        message: error?.message,
        response: error?.response?.data,
        status: error?.response?.status,
      });
    },
  });

  const registerMutation = useMutation({
    mutationFn: (userData: RegisterRequest) => {
      console.log('Attempting registration for:', userData.email);
      return authApi.register(userData);
    },
    onSuccess: (data, variables) => {
      console.log('Registration successful, received data:', data);
      console.log('Auto-logging in after registration...');
      loginMutation.mutate({
        email: variables.email,
        password: variables.password,
      });
    },
    onError: (error: ApiError) => {
      console.error('Error details:', {
        message: error?.message,
        response: error?.response?.data,
        status: error?.response?.status,
      });
    },
  });

  const refreshTokenMutation = useMutation({
    mutationFn: async (request: RefreshTokenRequest) => {
      const refreshResponse = await authApi.refresh(request);
      
      setAuth(refreshResponse.accessToken, refreshResponse.refreshToken, {
        id: refreshResponse.userId,
        username: '',
        name: '',
        email: '',
        role: UserRole.USER,
      });
      
      const user = await usersApi.getById(refreshResponse.userId);
      
      return { ...refreshResponse, user };
    },
    onSuccess: (data) => {
      setAuth(data.accessToken, data.refreshToken, data.user);
    },
  });

  const logoutMutation = useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      clearAuth();
      navigate(ROUTES.LOGIN);
    },
    onError: () => {
      clearAuth();
      navigate(ROUTES.LOGIN);
    },
  });

  return {
    login: loginMutation.mutate,
    register: registerMutation.mutate,
    refreshToken: refreshTokenMutation.mutate,
    logout: logoutMutation.mutate,
    isLoggingIn: loginMutation.isPending,
    isRegistering: registerMutation.isPending,
    isRefreshing: refreshTokenMutation.isPending,
    loginError: loginMutation.error,
    registerError: registerMutation.error,
    isAuthenticated,
  };
};

export const useCurrentUser = () => {
  const user = useAuthStore((state) => state.user);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  
  return { 
    user, 
    isLoading: false, 
    error: null,
    isAuthenticated,
  };
};

export default useAuth;

