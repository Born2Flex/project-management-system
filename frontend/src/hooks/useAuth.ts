import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { authApi } from '@/api/auth.api';
import { type LoginRequest, type RegisterRequest } from '@/types/auth.types';
import { ROUTES } from '@/utils/constants';

export const useAuth = () => {
  const navigate = useNavigate();
  const { login: loginStore, logout: logoutStore } = useAuthStore();

  const loginMutation = useMutation({
    mutationFn: (credentials: LoginRequest) => authApi.login(credentials),
    onSuccess: (data) => {
      loginStore(data.token, data.user);
      navigate(ROUTES.PROJECTS);
    },
    onError: (error) => {
      console.error('Login failed:', error);
    },
  });

  const registerMutation = useMutation({
    mutationFn: (userData: RegisterRequest) => authApi.register(userData),
    onSuccess: (data) => {
      loginStore(data.token, data.user);
      navigate(ROUTES.PROJECTS);
    },
    onError: (error) => {
      console.error('Registration failed:', error);
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
    logout: logoutMutation.mutate,
    isLoggingIn: loginMutation.isPending,
    isRegistering: registerMutation.isPending,
    loginError: loginMutation.error,
    registerError: registerMutation.error,
  };
};

export default useAuth;

