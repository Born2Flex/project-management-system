import { apiClient } from './client';
import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '@/types/auth.types';
import { USE_MOCK_API } from '@/utils/constants';
import { authApiMock } from './mock';

const authApiReal = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/sign-up', userData);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout');
  },
};

export const authApi = USE_MOCK_API ? authApiMock : authApiReal;

export default authApi;

