import type { Comment, CreateCommentRequest, UpdateCommentRequest } from '@/types/comment.types';

const delay = (ms: number = 300) => new Promise(resolve => setTimeout(resolve, ms));

const mockUser = {
  id: 1,
  username: 'testuser',
  name: 'Test User',
  email: 'test@example.com',
  role: { id: 1, name: 'USER' },
};

const mockUser2 = {
  id: 2,
  username: 'johndoe',
  name: 'John Doe',
  email: 'john@example.com',
  role: { id: 1, name: 'USER' },
};

let mockComments: Comment[] = [
  {
    id: 1,
    content: 'This task is crucial for the upcoming release. We need to prioritize it.',
    createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    author: mockUser2,
    taskId: 2,
  },
  {
    id: 2,
    content: 'I\'ve started working on the authentication flow. Should have a PR ready by tomorrow.',
    createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
    author: mockUser,
    taskId: 2,
  },
  {
    id: 3,
    content: 'Found a bug in the login validation. Will fix it in this task.',
    createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
    author: mockUser,
    taskId: 2,
  },
  {
    id: 4,
    content: 'The setup looks good! Ready for review.',
    createdAt: new Date(Date.now() - 8 * 24 * 60 * 60 * 1000).toISOString(),
    author: mockUser,
    taskId: 1,
  },
  {
    id: 5,
    content: 'We should use Stripe for this. They have great documentation.',
    createdAt: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(),
    author: mockUser2,
    taskId: 4,
  },
];

let nextId = 6;

export const commentsApiMock = {
  getByTaskId: async (taskId: number): Promise<Comment[]> => {
    await delay();
    return mockComments
      .filter(c => c.taskId === taskId)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  },

  create: async (commentData: CreateCommentRequest): Promise<Comment> => {
    await delay();
    const newComment: Comment = {
      id: nextId++,
      content: commentData.content,
      createdAt: new Date().toISOString(),
      author: mockUser,
      taskId: commentData.taskId,
    };
    mockComments.push(newComment);
    return { ...newComment };
  },

  update: async (id: number, commentData: UpdateCommentRequest): Promise<Comment> => {
    await delay();
    const index = mockComments.findIndex(c => c.id === id);
    if (index === -1) {
      throw new Error('Comment not found');
    }
    mockComments[index] = {
      ...mockComments[index],
      content: commentData.content,
      updatedAt: new Date().toISOString(),
    };
    return { ...mockComments[index] };
  },

  delete: async (id: number): Promise<void> => {
    await delay();
    mockComments = mockComments.filter(c => c.id !== id);
  },
};

export default commentsApiMock;

