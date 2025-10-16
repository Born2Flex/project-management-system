import React, { useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router';
import { useTask } from '@/hooks/useTasks';
import { useComments } from '@/hooks/useComments';
import { useTasks } from '@/hooks/useTasks';
import { useProject } from '@/hooks/useProjects';
import { useDevelopers } from '@/hooks/useProjects';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';
import { formatDate, formatRelativeTime } from '@/utils/formatters';
import { getStatusDisplayName } from '@/utils/taskHelpers';
import { TaskStatus, TaskPriority, type AssignTaskRequest } from '@/types/task.types';

export const TaskDetailPage: React.FC = () => {
  const { projectId: projectIdParam, id } = useParams<{ projectId: string; id: string }>();
  const navigate = useNavigate();
  const projectId = projectIdParam ? parseInt(projectIdParam) : 0;
  const taskId = id ? parseInt(id) : 0;

  const { task, isLoading: isLoadingTask } = useTask(projectId, taskId);
  const { project, isLoading: isLoadingProject } = useProject(projectId);
  const { comments, isLoading: isLoadingComments, createComment, isCreating } = useComments(projectId, taskId);
  const { updateTaskMutation, assignTask, unassignTask, isAssigning, isUnassigning } = useTasks(projectId);
  const { developers, isLoading: isLoadingDevelopers } = useDevelopers(projectId);

  const [newComment, setNewComment] = useState('');
  const [isEditingStatus, setIsEditingStatus] = useState(false);
  const [isEditingAssignee, setIsEditingAssignee] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const lastUpdateRef = useRef<number>(0);

  const handleAddComment = () => {
    if (newComment.trim()) {
      createComment({ text: newComment, taskId });
      setNewComment('');
    }
  };

  const handleStatusChange = (newStatus: TaskStatus) => {
    if (!task || task.status === newStatus || updateTaskMutation.isPending) {
      setIsEditingStatus(false);
      return;
    }

    const now = Date.now();
    if (now - lastUpdateRef.current < 1000) {
      setIsEditingStatus(false);
      return;
    }
    lastUpdateRef.current = now;

    setIsEditingStatus(false);
    
    updateTaskMutation.mutate({ taskId: task.id, data: { status: newStatus } });
  };

  const handleAssignTask = (userId: number) => {
    if (!task || isAssigning || isUnassigning) return;

    const assignRequest: AssignTaskRequest = { assigneeId: userId };
    
    assignTask(
      { taskId: task.id, request: assignRequest },
      {
        onSuccess: () => {
          setIsEditingAssignee(false);
          setSelectedUserId(null);
        },
        onError: () => {
          setIsEditingAssignee(false);
          setSelectedUserId(null);
        }
      }
    );
  };

  const handleUnassignTask = () => {
    if (!task || isAssigning || isUnassigning) return;

    unassignTask(task.id, {
      onSuccess: () => {
        setIsEditingAssignee(false);
        setSelectedUserId(null);
      },
      onError: () => {
        setIsEditingAssignee(false);
        setSelectedUserId(null);
      }
    });
  };

  const handleCancelAssignment = () => {
    setIsEditingAssignee(false);
    setSelectedUserId(null);
  };

  const getPriorityColor = (priority: TaskPriority) => {
    switch (priority) {
      case TaskPriority.CRITICAL:
        return 'bg-red-100 text-red-800 border-red-200';
      case TaskPriority.HIGH:
        return 'bg-orange-100 text-orange-800 border-orange-200';
      case TaskPriority.MEDIUM:
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case TaskPriority.LOW:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStatusColor = (status: TaskStatus) => {
    switch (status) {
      case TaskStatus.OPEN:
        return 'bg-gray-100 text-gray-800 border-gray-200';
      case TaskStatus.IN_PROGRESS:
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case TaskStatus.UNDER_REVIEW:
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case TaskStatus.COMPLETED:
        return 'bg-green-100 text-green-800 border-green-200';
    }
  };

  if (isLoadingTask || isLoadingProject) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin h-12 w-12 border-4 border-blue-600 border-t-transparent rounded-full mx-auto mb-4"></div>
            <p className="text-gray-600">Loading task...</p>
          </div>
        </div>
      </Layout>
    );
  }

  const displayTask = task;

  if (!displayTask) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Task not found</p>
          <Button onClick={() => navigate(-1)}>Go Back</Button>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-5xl mx-auto">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4 transition-colors"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Back
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <div className="flex items-start justify-between mb-4">
                <h1 className="text-3xl font-bold text-gray-900">{displayTask.title}</h1>
                <div className="flex gap-2">
                  <span className={`px-3 py-1 text-sm font-medium rounded-full border ${getPriorityColor(displayTask.priority)}`}>
                    {displayTask.priority}
                  </span>
                </div>
              </div>

              <button
                onClick={() => navigate(`/projects/${projectId}`)}
                className="text-sm text-blue-600 hover:text-blue-700 hover:underline mb-4"
              >
                {project?.name || 'Loading...'}
              </button>

              <div className="mb-6">
                <h2 className="text-sm font-semibold text-gray-700 mb-2">Description</h2>
                <p className="text-gray-700 whitespace-pre-wrap">{displayTask.description}</p>
              </div>
            </Card>

            <Card>
              <h2 className="text-xl font-bold text-gray-900 mb-4">
                Comments ({comments.length})
              </h2>

              <div className="mb-6">
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Add a comment..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                  rows={3}
                />
                <div className="flex justify-end mt-2">
                  <Button
                    onClick={handleAddComment}
                    disabled={!newComment.trim()}
                    isLoading={isCreating}
                    size="sm"
                  >
                    Add Comment
                  </Button>
                </div>
              </div>

              {isLoadingComments ? (
                <div className="text-center py-8">
                  <div className="animate-spin h-8 w-8 border-4 border-blue-600 border-t-transparent rounded-full mx-auto"></div>
                </div>
              ) : comments.length === 0 ? (
                <p className="text-center text-gray-500 py-8">No comments yet</p>
              ) : (
                <div className="space-y-4">
                  {comments.map((comment) => (
                    <div key={comment.id} className="border-b border-gray-200 pb-4 last:border-0">
                      <div className="flex items-start gap-3">
                        {/* Avatar */}
                        <div className="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-semibold flex-shrink-0">
                          {comment.author.name.charAt(0).toUpperCase()}
                        </div>

                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-semibold text-gray-900">{comment.author.name}</span>
                            <span className="text-sm text-gray-500">
                              {formatRelativeTime(comment.createdAt)}
                            </span>
                            {comment.updatedAt && (
                              <span className="text-xs text-gray-400">(edited)</span>
                            )}
                          </div>
                          <p className="text-gray-700 whitespace-pre-wrap">{comment.text}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>

          <div className="space-y-4">
             <Card>
               <h3 className="text-sm font-semibold text-gray-700 mb-3">Status</h3>
               {updateTaskMutation.isPending ? (
                 <div className="flex items-center justify-center py-3">
                   <div className="flex items-center gap-2 text-sm text-gray-600">
                     <div className="animate-spin h-4 w-4 border-2 border-blue-600 border-t-transparent rounded-full"></div>
                     Updating status...
                   </div>
                 </div>
               ) : isEditingStatus ? (
                 <div className="space-y-2">
                   {Object.values(TaskStatus).map((status) => (
                     <button
                       key={status}
                       onClick={() => handleStatusChange(status)}
                       disabled={updateTaskMutation.isPending}
                       className={`w-full px-3 py-2 text-sm font-medium rounded-lg border text-left transition-colors disabled:opacity-50 disabled:cursor-not-allowed ${
                         displayTask.status === status
                           ? getStatusColor(status)
                           : 'bg-white hover:bg-gray-50 border-gray-300'
                       }`}
                     >
                       {getStatusDisplayName(status)}
                     </button>
                   ))}
                   <button
                     onClick={() => setIsEditingStatus(false)}
                     disabled={updateTaskMutation.isPending}
                     className="w-full px-3 py-2 text-sm text-gray-600 hover:text-gray-900 disabled:opacity-50 disabled:cursor-not-allowed"
                   >
                     Cancel
                   </button>
                 </div>
               ) : (
                 <button
                   onClick={() => setIsEditingStatus(true)}
                   disabled={updateTaskMutation.isPending}
                   className={`w-full px-3 py-2 text-sm font-medium rounded-lg border ${getStatusColor(displayTask.status)} hover:opacity-80 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed`}
                 >
                   {getStatusDisplayName(displayTask.status)}
                 </button>
               )}
             </Card>

            <Card>
              <h3 className="text-sm font-semibold text-gray-700 mb-3">Details</h3>
              <div className="space-y-3">
                <div>
                  <div className="flex items-center justify-between mb-1">
                    <p className="text-xs text-gray-500">Assignee</p>
                    {!isEditingAssignee && (
                      <button
                        onClick={() => setIsEditingAssignee(true)}
                        className="text-xs text-blue-600 hover:text-blue-700 hover:underline"
                      >
                        {displayTask.assignee ? 'Change' : 'Assign'}
                      </button>
                    )}
                  </div>
                  
                  {isEditingAssignee ? (
                    <div className="space-y-3">
                      {isLoadingDevelopers ? (
                        <div className="text-center py-2">
                          <div className="animate-spin h-4 w-4 border-2 border-blue-600 border-t-transparent rounded-full mx-auto"></div>
                        </div>
                      ) : (
                        <div className="space-y-2">
                          {developers.length === 0 ? (
                            <p className="text-sm text-gray-500 py-2">No developers available for assignment</p>
                          ) : (
                            <select
                              value={selectedUserId || ''}
                              onChange={(e) => setSelectedUserId(e.target.value ? parseInt(e.target.value) : null)}
                              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                              <option value="">Select a developer...</option>
                              {developers.map((developer) => (
                                <option key={developer.id} value={developer.id}>
                                  {developer.name} ({developer.email})
                                </option>
                              ))}
                            </select>
                          )}
                          
                          <div className="flex gap-2">
                            {selectedUserId && developers.length > 0 && (
                              <Button
                                onClick={() => handleAssignTask(selectedUserId)}
                                size="sm"
                                isLoading={isAssigning}
                                disabled={isAssigning || isUnassigning}
                              >
                                Assign
                              </Button>
                            )}
                            
                            {displayTask.assignee && (
                              <Button
                                onClick={handleUnassignTask}
                                size="sm"
                                variant="secondary"
                                isLoading={isUnassigning}
                                disabled={isAssigning || isUnassigning}
                              >
                                Unassign
                              </Button>
                            )}
                            
                            <Button
                              onClick={handleCancelAssignment}
                              size="sm"
                              variant="secondary"
                              disabled={isAssigning || isUnassigning}
                            >
                              Cancel
                            </Button>
                          </div>
                        </div>
                      )}
                    </div>
                  ) : (
                    <>
                      {displayTask.assignee ? (
                        <div className="flex items-center gap-2">
                          <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-sm font-semibold">
                            {displayTask.assignee.name.charAt(0).toUpperCase()}
                          </div>
                          <div>
                            <p className="text-sm font-medium text-gray-900">{displayTask.assignee.name}</p>
                            <p className="text-xs text-gray-500">{displayTask.assignee.email}</p>
                          </div>
                        </div>
                      ) : (
                        <p className="text-sm text-gray-500">Unassigned</p>
                      )}
                    </>
                  )}
                </div>

                <div>
                  <p className="text-xs text-gray-500 mb-1">Created</p>
                  <p className="text-sm text-gray-900">{formatDate(displayTask.createdAt)}</p>
                  <p className="text-xs text-gray-500">{formatRelativeTime(displayTask.createdAt)}</p>
                </div>

                {displayTask.dueDateTime && (
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Due Date</p>
                    <p className="text-sm text-gray-900">{formatDate(displayTask.dueDateTime)}</p>
                    <p className="text-xs text-gray-500">{formatRelativeTime(displayTask.dueDateTime)}</p>
                  </div>
                )}

                {displayTask.updatedAt && (
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Last Updated</p>
                    <p className="text-sm text-gray-900">{formatDate(displayTask.updatedAt)}</p>
                  </div>
                )}

                <div>
                  <p className="text-xs text-gray-500 mb-1">Priority</p>
                  <span className={`inline-block px-2 py-1 text-xs font-medium rounded border ${getPriorityColor(displayTask.priority)}`}>
                    {displayTask.priority}
                  </span>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default TaskDetailPage;

