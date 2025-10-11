import React from 'react';
import { Link } from 'react-router';
import { ROUTES } from '@/utils/constants';
import Button from '@/components/common/Button';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="text-center">
        <h1 className="text-9xl font-bold text-blue-600 mb-4">404</h1>
        <h2 className="text-3xl font-semibold text-gray-900 mb-4">Page Not Found</h2>
        <p className="text-gray-600 mb-8">
          The page you're looking for doesn't exist or has been moved.
        </p>
        <Link to={ROUTES.HOME}>
          <Button>Go Back Home</Button>
        </Link>
      </div>
    </div>
  );
};

export default NotFoundPage;

