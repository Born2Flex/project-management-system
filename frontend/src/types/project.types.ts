export enum ProjectStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
}

export interface Project {
  id: number;
  name: string;
  description: string;
  status: ProjectStatus;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  status: ProjectStatus;
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  status?: ProjectStatus;
}

