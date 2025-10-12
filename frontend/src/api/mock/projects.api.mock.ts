import type { Project, CreateProjectRequest, UpdateProjectRequest } from '@/types/project.types';
import { ProjectStatus } from '@/types/project.types';

const delay = (ms: number = 300) => new Promise(resolve => setTimeout(resolve, ms));

// Mock projects data
let mockProjects: Project[] = [
  {
    id: 1,
    name: 'E-commerce Platform',
    description: 'Building a modern e-commerce platform with React and Spring Boot',
    status: ProjectStatus.ACTIVE,
  },
  {
    id: 2,
    name: 'Mobile App Backend',
    description: 'REST API for mobile application',
    status: ProjectStatus.ACTIVE,
  },
  {
    id: 3,
    name: 'Data Analytics Dashboard',
    description: 'Real-time analytics dashboard for business metrics',
    status: ProjectStatus.COMPLETED,
  },
];

let nextId = 4;

export const projectsApiMock = {
  getAll: async (): Promise<Project[]> => {
    await delay();
    return [...mockProjects];
  },

  getById: async (id: number): Promise<Project> => {
    await delay();
    const project = mockProjects.find(p => p.id === id);
    if (!project) {
      throw new Error('Project not found');
    }
    return { ...project };
  },

  create: async (projectData: CreateProjectRequest): Promise<Project> => {
    await delay();
    const newProject: Project = {
      id: nextId++,
      ...projectData,
    };
    mockProjects.push(newProject);
    return { ...newProject };
  },

  update: async (id: number, projectData: UpdateProjectRequest): Promise<Project> => {
    await delay();
    const index = mockProjects.findIndex(p => p.id === id);
    if (index === -1) {
      throw new Error('Project not found');
    }
    mockProjects[index] = {
      ...mockProjects[index],
      ...projectData,
    };
    return { ...mockProjects[index] };
  },

  delete: async (id: number): Promise<void> => {
    await delay();
    mockProjects = mockProjects.filter(p => p.id !== id);
  },
};

export default projectsApiMock;

