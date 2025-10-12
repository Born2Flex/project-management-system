import type { Task, CreateTaskRequest, UpdateTaskRequest } from '@/types/task.types';
import { TaskStatus, TaskPriority } from '@/types/task.types';
import { ProjectStatus } from '@/types/project.types';

const delay = (ms: number = 300) => new Promise(resolve => setTimeout(resolve, ms));

// Mock tasks data
let mockTasks: Task[] = [
  {
    id: 1,
    title: 'Setup project structure',
    description: 'Initialize React project with TypeScript and configure build tools',
    status: TaskStatus.COMPLETED,
    priority: TaskPriority.HIGH,
    createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    project: { id: 1, name: 'E-commerce Platform', description: 'Building a modern e-commerce platform', status: ProjectStatus.ACTIVE },
    assignee: { id: 1, username: 'testuser', name: 'Test User', email: 'test@example.com', role: { id: 1, name: 'USER' } },
  },
  {
    id: 2,
    title: 'Implement authentication',
    description: 'Add JWT-based authentication with login and registration',
    status: TaskStatus.IN_PROGRESS,
    priority: TaskPriority.CRITICAL,
    dueDateTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    project: { id: 1, name: 'E-commerce Platform', description: 'Building a modern e-commerce platform', status: ProjectStatus.ACTIVE },
    assignee: { id: 1, username: 'testuser', name: 'Test User', email: 'test@example.com', role: { id: 1, name: 'USER' } },
  },
  {
    id: 3,
    title: 'Design product catalog',
    description: 'Create responsive product listing and detail pages',
    status: TaskStatus.OPEN,
    priority: TaskPriority.MEDIUM,
    dueDateTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    project: { id: 1, name: 'E-commerce Platform', description: 'Building a modern e-commerce platform', status: ProjectStatus.ACTIVE },
  },
  {
    id: 4,
    title: 'Setup payment gateway',
    description: 'Integrate Stripe payment processing',
    status: TaskStatus.OPEN,
    priority: TaskPriority.HIGH,
    createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
    project: { id: 1, name: 'E-commerce Platform', description: 'Building a modern e-commerce platform', status: ProjectStatus.ACTIVE },
  },
  {
    id: 5,
    title: 'API documentation',
    description: 'Document all REST endpoints with Swagger',
    status: TaskStatus.UNDER_REVIEW,
    priority: TaskPriority.LOW,
    createdAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString(),
    project: { id: 2, name: 'Mobile App Backend', description: 'REST API for mobile application', status: ProjectStatus.ACTIVE },
    assignee: { id: 1, username: 'testuser', name: 'Test User', email: 'test@example.com', role: { id: 1, name: 'USER' } },
  },
];

let nextId = 6;

export const tasksApiMock = {
  getAll: async (): Promise<Task[]> => {
    await delay();
    return [...mockTasks];
  },

  getByProjectId: async (projectId: number): Promise<Task[]> => {
    await delay();
    return mockTasks.filter(task => task.project.id === projectId);
  },

  getById: async (id: number): Promise<Task> => {
    await delay();
    const task = mockTasks.find(t => t.id === id);
    if (!task) {
      throw new Error('Task not found');
    }
    return { ...task };
  },

  create: async (taskData: CreateTaskRequest): Promise<Task> => {
    await delay();
    const newTask: Task = {
      id: nextId++,
      title: taskData.title,
      description: taskData.description,
      status: taskData.status,
      priority: taskData.priority,
      dueDateTime: taskData.dueDateTime,
      createdAt: new Date().toISOString(),
      project: { id: taskData.projectId, name: 'Project ' + taskData.projectId, description: '', status: ProjectStatus.ACTIVE },
      assignee: taskData.assigneeId ? { id: taskData.assigneeId, username: 'user', name: 'User', email: 'user@example.com', role: { id: 1, name: 'USER' } } : undefined,
    };
    mockTasks.push(newTask);
    return { ...newTask };
  },

  update: async (id: number, taskData: UpdateTaskRequest): Promise<Task> => {
    await delay();
    const index = mockTasks.findIndex(t => t.id === id);
    if (index === -1) {
      throw new Error('Task not found');
    }
    mockTasks[index] = {
      ...mockTasks[index],
      ...taskData,
      updatedAt: new Date().toISOString(),
    };
    return { ...mockTasks[index] };
  },

  delete: async (id: number): Promise<void> => {
    await delay();
    mockTasks = mockTasks.filter(t => t.id !== id);
  },
};

export default tasksApiMock;

