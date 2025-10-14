import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usersApi } from '@/api/users.api';
import type { UserUpdateRequest } from '@/types/auth.types';
import { QUERY_KEYS } from '@/utils/constants';

export const useUsers = () => {
  const {
    data: users = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: [QUERY_KEYS.USERS],
    queryFn: () => usersApi.getAll(),
  });

  const queryClient = useQueryClient();

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UserUpdateRequest }) =>
      usersApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.USERS] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => usersApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.USERS] });
    },
  });

  return {
    users,
    isLoading,
    error,
    refetch,
    updateUser: updateMutation.mutate,
    deleteUser: deleteMutation.mutate,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useUser = (id: number) => {
  const {
    data: user,
    isLoading,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.USER, id],
    queryFn: () => usersApi.getById(id),
    enabled: !!id,
  });

  return {
    user,
    isLoading,
    error,
  };
};

export const useUserByEmail = (email: string) => {
  const {
    data: user,
    isLoading,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.USER_BY_EMAIL, email],
    queryFn: () => usersApi.getByEmail(email),
    enabled: !!email,
  });

  return {
    user,
    isLoading,
    error,
  };
};

export default useUsers;

