import { apiClient } from './client';
import { type Project, type CreateProjectRequest, type UpdateProjectRequest, type AddDeveloperRequest } from '@/types/project.types';
import type { User } from '@/types/auth.types';

export const projectsApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await apiClient.get<Project[]>('/projects');
    return response.data;
  },

  getById: async (id: number): Promise<Project> => {
    const response = await apiClient.get<Project>(`/projects/${id}`);
    return response.data;
  },

  create: async (projectData: CreateProjectRequest): Promise<Project> => {
    const response = await apiClient.post<Project>('/projects', projectData);
    return response.data;
  },

  update: async (id: number, projectData: UpdateProjectRequest): Promise<Project> => {
    const response = await apiClient.put<Project>(`/projects/${id}`, projectData);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/projects/${id}`);
  },

  getDevelopers: async (projectId: number): Promise<User[]> => {
    const response = await apiClient.get<User[]>(`/projects/${projectId}/developers`);
    return response.data;
  },

  addDeveloper: async (projectId: number, request: AddDeveloperRequest): Promise<void> => {
    await apiClient.post(`/projects/${projectId}/developers`, request);
  },

  removeDeveloper: async (projectId: number, developerId: number): Promise<void> => {
    await apiClient.delete(`/projects/${projectId}/developers/${developerId}`);
  },
};

export default projectsApi;

