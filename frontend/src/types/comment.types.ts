import { type User } from './auth.types';

export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  updatedAt?: string;
  author: User;
  taskId: number;
}

export interface CreateCommentRequest {
  content: string;
  taskId: number;
}

export interface UpdateCommentRequest {
  content: string;
}

