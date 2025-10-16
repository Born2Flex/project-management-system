import React, { useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router';
import { useProject, useDevelopers, useProjects } from '@/hooks/useProjects';
import { useTasks } from '@/hooks/useTasks';
import { useCurrentUser } from '@/hooks/useAuth';
import { UserRole } from '@/types/auth.types';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';
import DeleteConfirmationModal from '@/components/common/DeleteConfirmationModal';
import CreateTaskModal from '@/components/features/CreateTaskModal';
import EditProjectModal from '@/components/features/EditProjectModal';
import AssignUserModal from '@/components/features/AssignUserModal';
import { getStatusDisplayName } from '@/utils/taskHelpers';
import { TaskStatus, type Task } from '@/types/task.types';
import { 
  DndContext, 
  DragOverlay,
  useDraggable,
  useDroppable,
  closestCorners,
  PointerSensor,
  useSensor,
  useSensors
} from '@dnd-kit/core';
import type { DragStartEvent, DragEndEvent } from '@dnd-kit/core';
import { CSS } from '@dnd-kit/utilities';

const DraggableTask: React.FC<{ task: Task; onClick: (taskId: number) => void }> = ({ task, onClick }) => {
  const { attributes, listeners, setNodeRef, transform, isDragging } = useDraggable({
    id: task.id.toString(),
  });

  const style = {
    transform: CSS.Translate.toString(transform),
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style}>
      <Card padding="sm" className="hover:shadow-md transition-shadow cursor-pointer" onClick={() => onClick(task.id)}>
        <div 
          {...listeners} 
          {...attributes}
          className="cursor-grab active:cursor-grabbing"
          style={{ touchAction: 'none' }}
        >
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
        </div>
      </Card>
    </div>
  );
};

const DroppableColumn: React.FC<{ 
  status: TaskStatus; 
  title: string; 
  color: string; 
  tasks: Task[]; 
  onTaskClick: (taskId: number) => void;
}> = ({ status, title, color, tasks, onTaskClick }) => {
  const { setNodeRef, isOver } = useDroppable({
    id: status,
  });

  return (
    <div key={status} className="flex flex-col">
      <div className={`${color} px-4 py-3 rounded-t-lg`}>
        <h2 className="font-semibold text-gray-900">
          {title} ({tasks.length})
        </h2>
      </div>
      <div 
        ref={setNodeRef}
        className={`bg-gray-50 p-4 rounded-b-lg min-h-[400px] space-y-3 transition-colors ${
          isOver ? 'bg-blue-50 border-2 border-dashed border-blue-300' : ''
        }`}
      >
        {tasks.length === 0 ? (
          <p className="text-sm text-gray-500 text-center py-4">No tasks</p>
        ) : (
          tasks.map((task) => (
            <DraggableTask key={task.id} task={task} onClick={onTaskClick} />
          ))
        )}
      </div>
    </div>
  );
};

export const ProjectBoardPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const projectId = id ? parseInt(id) : 0;
  
  const { project, isLoading: isLoadingProject } = useProject(projectId);
  const { tasks, isLoading: isLoadingTasks, updateTaskMutation } = useTasks(projectId);
  const { developers, addDeveloper, removeDeveloper, isAdding, isRemoving } = useDevelopers(projectId);
  const { deleteProject, isDeleting: isDeletingProject } = useProjects();
  const { user } = useCurrentUser();
  const [isCreateTaskModalOpen, setIsCreateTaskModalOpen] = useState(false);
  const [isEditProjectModalOpen, setIsEditProjectModalOpen] = useState(false);
  const [isAssignUserModalOpen, setIsAssignUserModalOpen] = useState(false);
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const [optimisticTasks, setOptimisticTasks] = useState<Task[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const lastUpdateRef = useRef<number>(0);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    })
  );

  const displayTasks = optimisticTasks.length > 0 ? optimisticTasks : tasks;
  
  const isPM = user?.role === UserRole.PROJECT_MANAGER;

  const handleTaskClick = (taskId: number) => {
    if (isDragging) return;
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

  const handleDeleteProject = async () => {
    try {
      await deleteProject(projectId);
      navigate('/projects');
    } catch (error) {
      console.error('Failed to delete project:', error);
    }
  };

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    const task = displayTasks.find(t => t.id.toString() === active.id);
    setActiveTask(task || null);
    setIsDragging(true);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    
    setActiveTask(null);
    setIsDragging(false);
    
    if (!over) {
      setOptimisticTasks([]);
      return;
    }

    const taskId = active.id.toString();
    const newStatus = over.id as TaskStatus;
    
    const task = tasks.find(t => t.id.toString() === taskId);
    
    if (!task || task.status === newStatus || updateTaskMutation.isPending) {
      setOptimisticTasks([]);
      return;
    }

    const now = Date.now();
    if (now - lastUpdateRef.current < 1000) {
      setOptimisticTasks([]);
      return;
    }
    lastUpdateRef.current = now;

    const updatedTasks = tasks.map(t => 
      t.id.toString() === taskId ? { ...t, status: newStatus } : t
    );
    
    setOptimisticTasks(updatedTasks);
    
    updateTaskMutation.mutate(
      { taskId: parseInt(taskId), data: { status: newStatus } },
      {
        onSuccess: () => {
          setOptimisticTasks([]);
        },
        onError: () => {
          setOptimisticTasks([]);
        }
      }
    );
  };

  const tasksByStatus = {
    [TaskStatus.OPEN]: displayTasks.filter((task) => task.status === TaskStatus.OPEN),
    [TaskStatus.IN_PROGRESS]: displayTasks.filter((task) => task.status === TaskStatus.IN_PROGRESS),
    [TaskStatus.UNDER_REVIEW]: displayTasks.filter((task) => task.status === TaskStatus.UNDER_REVIEW),
    [TaskStatus.COMPLETED]: displayTasks.filter((task) => task.status === TaskStatus.COMPLETED),
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
      <DndContext
        sensors={sensors}
        collisionDetection={closestCorners}
        onDragStart={handleDragStart}
        onDragEnd={handleDragEnd}
      >
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
                {isPM && (
                  <>
                    <Button 
                      variant="secondary" 
                      onClick={() => setIsEditProjectModalOpen(true)}
                    >
                      Edit Project
                    </Button>
                    <Button 
                      variant="outline" 
                      onClick={() => setShowDeleteConfirm(true)}
                      disabled={isDeletingProject}
                      className="border-red-600 text-red-600 hover:bg-red-600 hover:text-white focus:ring-red-500"
                    >
                      Delete Project
                    </Button>
                  </>
                )}
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
              <DroppableColumn
                key={column.status}
                status={column.status}
                title={column.title}
                color={column.color}
                tasks={tasksByStatus[column.status]}
                onTaskClick={handleTaskClick}
              />
            ))}
          </div>
        </div>

        <DragOverlay>
          {activeTask ? (
            <div className="bg-white rounded-lg shadow-lg p-4 border border-gray-200 opacity-90">
              <h3 className="font-medium text-gray-900 mb-1">{activeTask.title}</h3>
              <p className="text-sm text-gray-600 line-clamp-2 mb-2">
                {activeTask.description}
              </p>
              <div className="flex items-center justify-between">
                <span
                  className={`px-2 py-1 text-xs font-medium rounded ${
                    activeTask.priority === 'CRITICAL'
                      ? 'bg-red-100 text-red-800'
                      : activeTask.priority === 'HIGH'
                      ? 'bg-orange-100 text-orange-800'
                      : activeTask.priority === 'MEDIUM'
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-gray-100 text-gray-800'
                  }`}
                >
                  {activeTask.priority}
                </span>
                {activeTask.assignee && (
                  <span className="text-xs text-gray-500">
                    {activeTask.assignee.name}
                  </span>
                )}
              </div>
            </div>
          ) : null}
        </DragOverlay>

        <CreateTaskModal
          isOpen={isCreateTaskModalOpen}
          onClose={() => setIsCreateTaskModalOpen(false)}
          projectId={projectId}
        />

        {project && (
          <EditProjectModal
            isOpen={isEditProjectModalOpen}
            onClose={() => setIsEditProjectModalOpen(false)}
            project={project}
          />
        )}
        
        <AssignUserModal
          isOpen={isAssignUserModalOpen}
          onClose={() => setIsAssignUserModalOpen(false)}
          projectId={projectId}
          onAssign={handleAddDeveloper}
          isAssigning={isAdding}
        />

        <DeleteConfirmationModal
          isOpen={showDeleteConfirm}
          onClose={() => setShowDeleteConfirm(false)}
          onConfirm={handleDeleteProject}
          title="Delete Project"
          message={`Are you sure you want to delete "${project?.name}"? This action cannot be undone and will delete all tasks and comments in this project.`}
          confirmText="Delete Project"
          isLoading={isDeletingProject}
        />
      </DndContext>
    </Layout>
  );
};

export default ProjectBoardPage;

