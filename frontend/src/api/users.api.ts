import { apiClient } from './client';
import type { User, UserUpdateRequest } from '@/types/auth.types';

export const usersApi = {
  getAll: async (): Promise<User[]> => {
    const response = await apiClient.get<User[]>('/users');
    return response.data;
  },

  getById: async (id: number): Promise<User> => {
    const response = await apiClient.get<User>(`/users/${id}`);
    return response.data;
  },

  getByEmail: async (email: string): Promise<User> => {
    const response = await apiClient.get<User>(`/users/by-email?email=${encodeURIComponent(email)}`);
    return response.data;
  },

  update: async (id: number, userData: UserUpdateRequest): Promise<User> => {
    const response = await apiClient.put<User>(`/users/${id}`, userData);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/users/${id}`);
  },
};

export default usersApi;

