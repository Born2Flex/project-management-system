import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '@/api/tasks.api';
import { type CreateTaskRequest, type UpdateTaskRequest } from '@/types/task.types';
import { QUERY_KEYS } from '@/utils/constants';

export const useTasks = (projectId?: number) => {
  const queryClient = useQueryClient();

  const {
    data: tasks = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: projectId ? [QUERY_KEYS.TASKS, projectId] : [QUERY_KEYS.TASKS],
    queryFn: () => (projectId ? tasksApi.getByProjectId(projectId) : tasksApi.getAll()),
  });

  const createMutation = useMutation({
    mutationFn: (taskData: CreateTaskRequest) => tasksApi.create(taskData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateTaskRequest }) =>
      tasksApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => tasksApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
    },
  });

  return {
    tasks,
    isLoading,
    error,
    refetch,
    createTask: createMutation.mutate,
    updateTask: updateMutation.mutate,
    deleteTask: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useTask = (id: number) => {
  const {
    data: task,
    isLoading,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.TASK, id],
    queryFn: () => tasksApi.getById(id),
    enabled: !!id,
  });

  return {
    task,
    isLoading,
    error,
  };
};

export default useTasks;

