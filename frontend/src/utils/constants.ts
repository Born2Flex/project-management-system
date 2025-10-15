export const API_BASE_URL = 'http://localhost:8080/project-management';
export const API_TIMEOUT = 30000;

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  PROJECTS: '/projects',
  PROJECT_BOARD: '/projects/:id',
  TASK_DETAIL: '/projects/:projectId/tasks/:id',
} as const;

export const QUERY_KEYS = {
  USER: 'user',
  USERS: 'users',
  USER_BY_EMAIL: 'userByEmail',
  PROJECTS: 'projects',
  PROJECT: 'project',
  PROJECT_DEVELOPERS: 'projectDevelopers',
  TASKS: 'tasks',
  TASK: 'task',
  COMMENTS: 'comments',
} as const;

