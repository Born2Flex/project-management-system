import { apiClient } from './client';
import { type Task, type CreateTaskRequest, type UpdateTaskRequest, type AssignTaskRequest } from '@/types/task.types';

export const tasksApi = {
  getByProjectId: async (projectId: number): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(`/projects/${projectId}/tasks`);
    return response.data;
  },

  getById: async (projectId: number, taskId: number): Promise<Task> => {
    const response = await apiClient.get<Task>(`/projects/${projectId}/tasks/${taskId}`);
    return response.data;
  },

  create: async (projectId: number, taskData: CreateTaskRequest): Promise<Task> => {
    const response = await apiClient.post<Task>(`/projects/${projectId}/tasks`, taskData);
    return response.data;
  },

  update: async (projectId: number, taskId: number, taskData: UpdateTaskRequest): Promise<Task> => {
    const response = await apiClient.put<Task>(`/projects/${projectId}/tasks/${taskId}`, taskData);
    return response.data;
  },

  delete: async (projectId: number, taskId: number): Promise<void> => {
    await apiClient.delete(`/projects/${projectId}/tasks/${taskId}`);
  },

  assign: async (projectId: number, taskId: number, request: AssignTaskRequest): Promise<void> => {
    await apiClient.patch(`/projects/${projectId}/tasks/${taskId}/assign`, request);
  },

  unassign: async (projectId: number, taskId: number): Promise<void> => {
    await apiClient.patch(`/projects/${projectId}/tasks/${taskId}/unassign`);
  },
};

export default tasksApi;

