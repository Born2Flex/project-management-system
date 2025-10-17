import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useProjects } from '@/hooks/useProjects';
import { useCurrentUser } from '@/hooks/useAuth';
import { UserRole } from '@/types/auth.types';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';
import DeleteConfirmationModal from '@/components/common/DeleteConfirmationModal';
import CreateProjectModal from '@/components/features/CreateProjectModal';
import EditProjectModal from '@/components/features/EditProjectModal';

export const ProjectsPage: React.FC = () => {
  const navigate = useNavigate();
  const { projects, isLoading, error, deleteProject, isDeleting } = useProjects();
  const { user } = useCurrentUser();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<number | null>(null);
  const [projectToEdit, setProjectToEdit] = useState<number | null>(null);
  
  const isPM = user?.role === UserRole.PROJECT_MANAGER;

  const handleProjectClick = (projectId: number) => {
    navigate(`/projects/${projectId}`);
  };

  const handleDeleteProject = async (projectId: number) => {
    try {
      await deleteProject(projectId);
      setProjectToDelete(null);
    } catch (error) {
      console.error('Failed to delete project:', error);
    }
  };

  const handleEditProject = (projectId: number) => {
    setProjectToEdit(projectId);
  };

  if (isLoading) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin h-12 w-12 border-4 border-blue-600 border-t-transparent rounded-full mx-auto mb-4"></div>
            <p className="text-gray-600">Loading projects...</p>
          </div>
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-red-600">Failed to load projects. Please try again.</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Projects</h1>
          {isPM && (
            <Button onClick={() => setIsCreateModalOpen(true)}>
              Create Project
            </Button>
          )}
        </div>

        {projects.length === 0 ? (
          <Card padding="lg">
            <div className="text-center py-12">
              <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
              </svg>
              <h3 className="text-lg font-medium text-gray-900 mb-2">No projects yet</h3>
              <p className="text-gray-600 mb-6">Get started by creating your first project</p>
              {isPM && (
                <Button onClick={() => setIsCreateModalOpen(true)}>
                  Create Your First Project
                </Button>
              )}
            </div>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {projects.map((project) => (
              <Card
                key={project.id}
                hover
                onClick={() => handleProjectClick(project.id)}
              >
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-xl font-semibold text-gray-900 overflow-hidden text-ellipsis">{project.name}</h3>
                  <div className="flex items-center gap-2">
                    <span
                      className={`px-2 py-1 text-xs font-medium rounded-full ${
                        project.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}
                    >
                      {project.status}
                    </span>
                    {isPM && (
                      <div className="flex gap-1">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            handleEditProject(project.id);
                          }}
                          className="text-gray-400 hover:text-blue-500 transition-colors p-1"
                          title="Edit project"
                        >
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            setProjectToDelete(project.id);
                          }}
                          className="text-gray-400 hover:text-red-500 transition-colors p-1"
                          disabled={isDeleting}
                          title="Delete project"
                        >
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </div>
                    )}
                  </div>
                </div>
                <p className="text-gray-600 text-sm line-clamp-2">{project.description}</p>
              </Card>
            ))}
          </div>
        )}
      </div>

      <CreateProjectModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />

      {projectToEdit && (
        <EditProjectModal
          isOpen={!!projectToEdit}
          onClose={() => setProjectToEdit(null)}
          project={projects.find(p => p.id === projectToEdit)!}
        />
      )}

      <DeleteConfirmationModal
        isOpen={!!projectToDelete}
        onClose={() => setProjectToDelete(null)}
        onConfirm={() => projectToDelete && handleDeleteProject(projectToDelete)}
        title="Delete Project"
        message={`Are you sure you want to delete "${projects.find(p => p.id === projectToDelete)?.name}"? This action cannot be undone and will delete all tasks and comments in this project.`}
        confirmText="Delete Project"
        isLoading={isDeleting}
      />
    </Layout>
  );
};

export default ProjectsPage;

