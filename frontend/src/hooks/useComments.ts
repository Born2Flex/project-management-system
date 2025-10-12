import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { commentsApi } from '@/api/comments.api';
import { type CreateCommentRequest, type UpdateCommentRequest } from '@/types/comment.types';
import { QUERY_KEYS } from '@/utils/constants';

export const useComments = (taskId: number) => {
  const queryClient = useQueryClient();

  const {
    data: comments = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: [QUERY_KEYS.COMMENTS, taskId],
    queryFn: () => commentsApi.getByTaskId(taskId),
    enabled: !!taskId,
  });

  const createMutation = useMutation({
    mutationFn: (commentData: CreateCommentRequest) => commentsApi.create(commentData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.COMMENTS, taskId] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCommentRequest }) =>
      commentsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.COMMENTS, taskId] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => commentsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.COMMENTS, taskId] });
    },
  });

  return {
    comments,
    isLoading,
    error,
    refetch,
    createComment: createMutation.mutate,
    updateComment: updateMutation.mutate,
    deleteComment: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export default useComments;

