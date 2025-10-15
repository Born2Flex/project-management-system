import { apiClient } from './client';
import { type Comment, type CreateCommentRequest, type UpdateCommentRequest } from '@/types/comment.types';

export const commentsApi = {
  getByTaskId: async (projectId: number, taskId: number): Promise<Comment[]> => {
    const response = await apiClient.get<Comment[]>(`/projects/${projectId}/tasks/${taskId}/comments`);
    return response.data;
  },

  create: async (projectId: number, taskId: number, commentData: CreateCommentRequest): Promise<Comment> => {
    const response = await apiClient.post<Comment>(`/projects/${projectId}/tasks/${taskId}/comments`, commentData);
    return response.data;
  },

  update: async (projectId: number, taskId: number, id: number, commentData: UpdateCommentRequest): Promise<Comment> => {
    const response = await apiClient.put<Comment>(`/projects/${projectId}/tasks/${taskId}/comments/${id}`, commentData);
    return response.data;
  },

  delete: async (projectId: number, taskId: number, id: number): Promise<void> => {
    await apiClient.delete(`/projects/${projectId}/tasks/${taskId}/comments/${id}`);
  },
};

export default commentsApi;

