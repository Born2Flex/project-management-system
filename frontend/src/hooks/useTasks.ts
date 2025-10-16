import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '@/api/tasks.api';
import { type CreateTaskRequest, type UpdateTaskRequest, type AssignTaskRequest, type Task } from '@/types/task.types';
import { QUERY_KEYS } from '@/utils/constants';

export const useTasks = (projectId: number) => {
  const queryClient = useQueryClient();

  const {
    data: tasks = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: [QUERY_KEYS.TASKS, projectId],
    queryFn: () => tasksApi.getByProjectId(projectId),
    enabled: !!projectId,
  });

  const createMutation = useMutation({
    mutationFn: (taskData: CreateTaskRequest) => tasksApi.create(projectId, taskData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS, projectId] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ taskId, data }: { taskId: number; data: UpdateTaskRequest }) =>
      tasksApi.update(projectId, taskId, data),
    onSuccess: (updatedTask) => {
      queryClient.setQueryData([QUERY_KEYS.TASKS, projectId], (oldTasks: Task[] | undefined) => {
        if (!oldTasks) return oldTasks;
        return oldTasks.map(task => 
          task.id === updatedTask.id ? { ...task, ...updatedTask } : task
        );
      });
      queryClient.setQueryData([QUERY_KEYS.TASK, projectId, updatedTask.id], updatedTask);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (taskId: number) => tasksApi.delete(projectId, taskId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS, projectId] });
    },
  });

  const assignMutation = useMutation({
    mutationFn: ({ taskId, request }: { taskId: number; request: AssignTaskRequest }) =>
      tasksApi.assign(projectId, taskId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS, projectId] });
    },
  });

  const unassignMutation = useMutation({
    mutationFn: (taskId: number) => tasksApi.unassign(projectId, taskId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS, projectId] });
    },
  });

  return {
    tasks,
    isLoading,
    error,
    refetch,
    createTask: createMutation.mutate,
    updateTask: updateMutation.mutate,
    updateTaskMutation: updateMutation,
    deleteTask: deleteMutation.mutate,
    assignTask: assignMutation.mutate,
    unassignTask: unassignMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
    isAssigning: assignMutation.isPending,
    isUnassigning: unassignMutation.isPending,
  };
};

export const useTask = (projectId: number, taskId: number) => {
  const {
    data: task,
    isLoading,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.TASK, projectId, taskId],
    queryFn: () => tasksApi.getById(projectId, taskId),
    enabled: !!projectId && !!taskId,
  });

  return {
    task,
    isLoading,
    error,
  };
};


export default useTasks;

