import { apiClient } from './client';
import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse, RefreshTokenRequest } from '@/types/auth.types';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/sign-in', credentials);
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/auth/sign-up', {
      username: userData.username,
      name: userData.name,
      email: userData.email,
      password: userData.password,
      role: userData.role || 'DEVELOPER',
    });
    return response.data;
  },

  refresh: async (request: RefreshTokenRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/refresh', request);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.delete('/auth/logout');
  },
};

export default authApi;

