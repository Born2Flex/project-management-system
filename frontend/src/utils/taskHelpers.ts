import { TaskStatus } from '@/types/task.types';

export const getStatusDisplayName = (status: TaskStatus): string => {
  switch (status) {
    case TaskStatus.OPEN:
      return 'To Do';
    case TaskStatus.IN_PROGRESS:
      return 'In Progress';
    case TaskStatus.UNDER_REVIEW:
      return 'Under Review';
    case TaskStatus.COMPLETED:
      return 'Completed';
    default:
      return status;
  }
};

