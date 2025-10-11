import { apiClient } from './client';
import { type Project, type CreateProjectRequest, type UpdateProjectRequest } from '@/types/project.types';

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
};

export default projectsApi;

