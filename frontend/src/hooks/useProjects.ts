import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '@/api/projects.api';
import { type CreateProjectRequest, type UpdateProjectRequest, type AddDeveloperRequest } from '@/types/project.types';
import { QUERY_KEYS } from '@/utils/constants';

export const useProjects = () => {
  const queryClient = useQueryClient();

  const {
    data: projects = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: [QUERY_KEYS.PROJECTS],
    queryFn: () => projectsApi.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: (projectData: CreateProjectRequest) => projectsApi.create(projectData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.PROJECTS] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProjectRequest }) =>
      projectsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.PROJECTS] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => projectsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.PROJECTS] });
    },
  });

  return {
    projects,
    isLoading,
    error,
    refetch,
    createProject: createMutation.mutate,
    updateProject: updateMutation.mutate,
    deleteProject: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useProject = (id: number) => {
  const {
    data: project,
    isLoading,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.PROJECT, id],
    queryFn: () => projectsApi.getById(id),
    enabled: !!id,
  });

  return {
    project,
    isLoading,
    error,
  };
};

export const useDevelopers = (projectId: number) => {
  const {
    data: developers = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: [QUERY_KEYS.PROJECT_DEVELOPERS, projectId],
    queryFn: () => projectsApi.getDevelopers(projectId),
    enabled: !!projectId,
  });

  const queryClient = useQueryClient();

  const addDeveloperMutation = useMutation({
    mutationFn: (request: AddDeveloperRequest) => projectsApi.addDeveloper(projectId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.PROJECT_DEVELOPERS, projectId] });
    },
  });

  const removeDeveloperMutation = useMutation({
    mutationFn: (developerId: number) => projectsApi.removeDeveloper(projectId, developerId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.PROJECT_DEVELOPERS, projectId] });
    },
  });

  return {
    developers,
    isLoading,
    error,
    refetch,
    addDeveloper: addDeveloperMutation.mutate,
    removeDeveloper: removeDeveloperMutation.mutate,
    isAdding: addDeveloperMutation.isPending,
    isRemoving: removeDeveloperMutation.isPending,
  };
};

export default useProjects;

