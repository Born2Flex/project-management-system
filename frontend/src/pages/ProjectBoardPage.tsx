import React from 'react';
import { useParams } from 'react-router';
import { useProject } from '@/hooks/useProjects';
import { useTasks } from '@/hooks/useTasks';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';
import { TaskStatus } from '@/types/task.types';

export const ProjectBoardPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const projectId = id ? parseInt(id) : 0;
  
  const { project, isLoading: isLoadingProject } = useProject(projectId);
  const { tasks, isLoading: isLoadingTasks } = useTasks(projectId);

  const tasksByStatus = {
    [TaskStatus.OPEN]: tasks.filter((task) => task.status === TaskStatus.OPEN),
    [TaskStatus.IN_PROGRESS]: tasks.filter((task) => task.status === TaskStatus.IN_PROGRESS),
    [TaskStatus.UNDER_REVIEW]: tasks.filter((task) => task.status === TaskStatus.UNDER_REVIEW),
    [TaskStatus.COMPLETED]: tasks.filter((task) => task.status === TaskStatus.COMPLETED),
  };

  const statusColumns = [
    { status: TaskStatus.OPEN, title: 'To Do', color: 'bg-gray-100' },
    { status: TaskStatus.IN_PROGRESS, title: 'In Progress', color: 'bg-blue-100' },
    { status: TaskStatus.UNDER_REVIEW, title: 'Under Review', color: 'bg-yellow-100' },
    { status: TaskStatus.COMPLETED, title: 'Completed', color: 'bg-green-100' },
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
            <Button>
              Create Task
            </Button>
          </div>
          <p className="text-gray-600">{project.description}</p>
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
                    <Card key={task.id} padding="sm" hover>
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
    </Layout>
  );
};

export default ProjectBoardPage;

