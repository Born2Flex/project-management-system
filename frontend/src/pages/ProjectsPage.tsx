import React from 'react';
import { useNavigate } from 'react-router';
import { useProjects } from '@/hooks/useProjects';
import Layout from '@/components/layout/Layout';
import Card from '@/components/common/Card';
import Button from '@/components/common/Button';

export const ProjectsPage: React.FC = () => {
  const navigate = useNavigate();
  const { projects, isLoading, error } = useProjects();

  const handleProjectClick = (projectId: number) => {
    navigate(`/projects/${projectId}`);
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
          <Button>
            Create Project
          </Button>
        </div>

        {projects.length === 0 ? (
          <Card padding="lg">
            <div className="text-center py-12">
              <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
              </svg>
              <h3 className="text-lg font-medium text-gray-900 mb-2">No projects yet</h3>
              <p className="text-gray-600 mb-6">Get started by creating your first project</p>
              <Button>
                Create Your First Project
              </Button>
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
                  <h3 className="text-xl font-semibold text-gray-900">{project.name}</h3>
                  <span
                    className={`px-2 py-1 text-xs font-medium rounded-full ${
                      project.status === 'ACTIVE'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-gray-100 text-gray-800'
                    }`}
                  >
                    {project.status}
                  </span>
                </div>
                <p className="text-gray-600 text-sm line-clamp-2">{project.description}</p>
              </Card>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default ProjectsPage;

