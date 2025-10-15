import { type User } from './auth.types';

export interface Comment {
  id: number;
  text: string;
  createdAt: string;
  updatedAt?: string;
  author: User;
  taskId: number;
}

export interface CreateCommentRequest {
  text: string;
  taskId: number;
}

export interface UpdateCommentRequest {
  text: string;
}

