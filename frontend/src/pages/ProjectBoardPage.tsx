import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { useProject, useDevelopers } from '@/hooks/useProjects';
import { useTasks } from '@/hooks/useTasks';
import { useCurrentUser } from '@/hooks/useAuth';
import { UserRole } from '@/types/auth.types';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';
import CreateTaskModal from '@/components/features/CreateTaskModal';
import AssignUserModal from '@/components/features/AssignUserModal';
import { getStatusDisplayName } from '@/utils/taskHelpers';
import { TaskStatus } from '@/types/task.types';

export const ProjectBoardPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const projectId = id ? parseInt(id) : 0;
  
  const { project, isLoading: isLoadingProject } = useProject(projectId);
  const { tasks, isLoading: isLoadingTasks } = useTasks(projectId);
  const { developers, addDeveloper, removeDeveloper, isAdding, isRemoving } = useDevelopers(projectId);
  const { user } = useCurrentUser();
  const [isCreateTaskModalOpen, setIsCreateTaskModalOpen] = useState(false);
  const [isAssignUserModalOpen, setIsAssignUserModalOpen] = useState(false);
  
  const isPM = user?.role === UserRole.PROJECT_MANAGER;

  const handleTaskClick = (taskId: number) => {
    navigate(`/projects/${projectId}/tasks/${taskId}`);
  };

  const handleAddDeveloper = async (userId: number) => {
    try {
      await addDeveloper({ developerId: userId });
      setIsAssignUserModalOpen(false);
    } catch (error) {
      console.error('Failed to add developer:', error);
    }
  };

  const handleRemoveDeveloper = async (developerId: number) => {
    try {
      await removeDeveloper(developerId);
    } catch (error) {
      console.error('Failed to remove developer:', error);
    }
  };

  const tasksByStatus = {
    [TaskStatus.OPEN]: tasks.filter((task) => task.status === TaskStatus.OPEN),
    [TaskStatus.IN_PROGRESS]: tasks.filter((task) => task.status === TaskStatus.IN_PROGRESS),
    [TaskStatus.UNDER_REVIEW]: tasks.filter((task) => task.status === TaskStatus.UNDER_REVIEW),
    [TaskStatus.COMPLETED]: tasks.filter((task) => task.status === TaskStatus.COMPLETED),
  };

  const statusColumns = [
    { status: TaskStatus.OPEN, title: getStatusDisplayName(TaskStatus.OPEN), color: 'bg-gray-100' },
    { status: TaskStatus.IN_PROGRESS, title: getStatusDisplayName(TaskStatus.IN_PROGRESS), color: 'bg-blue-100' },
    { status: TaskStatus.UNDER_REVIEW, title: getStatusDisplayName(TaskStatus.UNDER_REVIEW), color: 'bg-yellow-100' },
    { status: TaskStatus.COMPLETED, title: getStatusDisplayName(TaskStatus.COMPLETED), color: 'bg-green-100' },
  ];

  if (isLoadingProject || isLoadingTasks) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin h-12 w-12 border-4 border-blue-600 border-t-transparent rounded-full mx-auto mb-4"></div>
            <p className="text-gray-600">Loading project board...</p>
          </div>
        </div>
      </Layout>
    );
  }

  if (!project) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-red-600">Project not found</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        <div className="mb-6">
          <div className="flex justify-between items-center mb-2">
            <h1 className="text-3xl font-bold text-gray-900">{project.name}</h1>
            <div className="flex gap-3">
              {isPM && (
                <Button 
                  variant="secondary" 
                  onClick={() => setIsAssignUserModalOpen(true)}
                  disabled={isAdding}
                >
                  Add Developer
                </Button>
              )}
              <Button onClick={() => setIsCreateTaskModalOpen(true)}>
                Create Task
              </Button>
            </div>
          </div>
          <p className="text-gray-600">{project.description}</p>
          
          {developers.length > 0 && (
            <div className="mt-4 bg-gray-50 rounded-lg">
               <div className="flex items-center justify-between mb-3">
                 <h3 className="text-sm font-medium text-gray-700">Project Team</h3>
                 {isPM && developers.some(d => d.id !== user?.id) && (
                   <span className="text-xs text-gray-500">Click × to remove</span>
                 )}
               </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                {developers.map((developer) => (
                  <div
                    key={developer.id}
                    className="flex items-center justify-between bg-white px-4 py-3 rounded-lg border border-gray-200 hover:border-gray-300 transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                        <span className="text-sm font-medium text-blue-600">
                          {developer.name.charAt(0).toUpperCase()}
                        </span>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-900">{developer.name}</p>
                        <p className="text-xs text-gray-500">{developer.email}</p>
                      </div>
                    </div>
                     {isPM && developer.id !== user?.id && (
                       <button
                         onClick={() => handleRemoveDeveloper(developer.id)}
                         className="text-gray-400 hover:text-red-500 transition-colors p-1"
                         disabled={isRemoving}
                         title="Remove developer"
                       >
                         <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                           <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                         </svg>
                       </button>
                     )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {statusColumns.map((column) => (
            <div key={column.status} className="flex flex-col">
              <div className={`${column.color} px-4 py-3 rounded-t-lg`}>
                <h2 className="font-semibold text-gray-900">
                  {column.title} ({tasksByStatus[column.status].length})
                </h2>
              </div>
              <div className="bg-gray-50 p-4 rounded-b-lg min-h-[400px] space-y-3">
                {tasksByStatus[column.status].length === 0 ? (
                  <p className="text-sm text-gray-500 text-center py-4">No tasks</p>
                ) : (
                  tasksByStatus[column.status].map((task) => (
                    <Card key={task.id} padding="sm" hover onClick={() => handleTaskClick(task.id)}>
                      <h3 className="font-medium text-gray-900 mb-1">{task.title}</h3>
                      <p className="text-sm text-gray-600 line-clamp-2 mb-2">
                        {task.description}
                      </p>
                      <div className="flex items-center justify-between">
                        <span
                          className={`px-2 py-1 text-xs font-medium rounded ${
                            task.priority === 'CRITICAL'
                              ? 'bg-red-100 text-red-800'
                              : task.priority === 'HIGH'
                              ? 'bg-orange-100 text-orange-800'
                              : task.priority === 'MEDIUM'
                              ? 'bg-yellow-100 text-yellow-800'
                              : 'bg-gray-100 text-gray-800'
                          }`}
                        >
                          {task.priority}
                        </span>
                        {task.assignee && (
                          <span className="text-xs text-gray-500">
                            {task.assignee.name}
                          </span>
                        )}
                      </div>
                    </Card>
                  ))
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      <CreateTaskModal
        isOpen={isCreateTaskModalOpen}
        onClose={() => setIsCreateTaskModalOpen(false)}
        projectId={projectId}
      />
      
      <AssignUserModal
        isOpen={isAssignUserModalOpen}
        onClose={() => setIsAssignUserModalOpen(false)}
        projectId={projectId}
        onAssign={handleAddDeveloper}
        isAssigning={isAdding}
      />
    </Layout>
  );
};

export default ProjectBoardPage;

