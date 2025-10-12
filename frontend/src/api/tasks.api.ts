import { apiClient } from './client';
import { type Task, type CreateTaskRequest, type UpdateTaskRequest } from '@/types/task.types';
import { USE_MOCK_API } from '@/utils/constants';
import { tasksApiMock } from './mock';

const tasksApiReal = {
  getAll: async (): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>('/tasks');
    return response.data;
  },

  getByProjectId: async (projectId: number): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(`/projects/${projectId}/tasks`);
    return response.data;
  },

  getById: async (id: number): Promise<Task> => {
    const response = await apiClient.get<Task>(`/tasks/${id}`);
    return response.data;
  },

  create: async (taskData: CreateTaskRequest): Promise<Task> => {
    const response = await apiClient.post<Task>('/tasks', taskData);
    return response.data;
  },

  update: async (id: number, taskData: UpdateTaskRequest): Promise<Task> => {
    const response = await apiClient.put<Task>(`/tasks/${id}`, taskData);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/tasks/${id}`);
  },
};

export const tasksApi = USE_MOCK_API ? tasksApiMock : tasksApiReal;

export default tasksApi;

