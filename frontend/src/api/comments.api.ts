import { apiClient } from './client';
import { type Comment, type CreateCommentRequest, type UpdateCommentRequest } from '@/types/comment.types';
import { USE_MOCK_API } from '@/utils/constants';
import { commentsApiMock } from './mock';

const commentsApiReal = {
  getByTaskId: async (taskId: number): Promise<Comment[]> => {
    const response = await apiClient.get<Comment[]>(`/tasks/${taskId}/comments`);
    return response.data;
  },

  create: async (commentData: CreateCommentRequest): Promise<Comment> => {
    const response = await apiClient.post<Comment>('/comments', commentData);
    return response.data;
  },

  update: async (id: number, commentData: UpdateCommentRequest): Promise<Comment> => {
    const response = await apiClient.put<Comment>(`/comments/${id}`, commentData);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/comments/${id}`);
  },
};

export const commentsApi = USE_MOCK_API ? commentsApiMock : commentsApiReal;

export default commentsApi;

