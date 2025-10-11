export const API_BASE_URL = 'http://localhost:8080/project-management';
export const API_TIMEOUT = 30000;

export const TOKEN_KEY = 'jira_clone_token';
export const USER_KEY = 'jira_clone_user';

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  PROJECTS: '/projects',
  PROJECT_BOARD: '/projects/:id',
} as const;

export const QUERY_KEYS = {
  USER: 'user',
  PROJECTS: 'projects',
  PROJECT: 'project',
  TASKS: 'tasks',
  TASK: 'task',
} as const;

