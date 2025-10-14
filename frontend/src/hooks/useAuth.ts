import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { authApi } from '@/api/auth.api';
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
  const { login: loginStore, logout: logoutStore } = useAuthStore();

  const loginMutation = useMutation({
    mutationFn: (credentials: LoginRequest) => {
      console.log('Attempting login with:', credentials.email);
      return authApi.login(credentials);
    },
    onSuccess: (data, variables) => {
      console.log('Login successful, received data:', data);
      // Create user object from response
      const user = {
        id: data.userId,
        username: '', // Will be fetched if needed
        name: '', // Will be fetched if needed
        email: variables.email,
        role: UserRole.USER, // Default role
      };
      console.log('Storing user in auth store:', user);
      loginStore(data.accessToken, data.refreshToken, user);
      console.log('Navigating to projects page');
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
      // After registration, automatically log in
      // Note: Backend doesn't return tokens on registration, so we need to log in
      console.log('Auto-logging in after registration...');
      loginMutation.mutate({
        email: variables.email,
        password: variables.password,
      });
    },
    onError: (error: ApiError) => {
      console.error('Registration failed with error:', error);
      console.error('Error details:', {
        message: error?.message,
        response: error?.response?.data,
        status: error?.response?.status,
      });
    },
  });

  const refreshTokenMutation = useMutation({
    mutationFn: (request: RefreshTokenRequest) => authApi.refresh(request),
    onSuccess: (data) => {
      // Update tokens in store
      const user = {
        id: data.userId,
        username: '',
        name: '',
        email: '',
        role: UserRole.USER,
      };
      loginStore(data.accessToken, data.refreshToken, user);
    },
  });

  const logoutMutation = useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      logoutStore();
      navigate(ROUTES.LOGIN);
    },
    onError: () => {
      logoutStore();
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
  };
};

export default useAuth;

