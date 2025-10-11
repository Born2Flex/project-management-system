import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { ProtectedRoute } from './ProtectedRoute';
import { ROUTES } from '@/utils/constants';

import LoginPage from '@/pages/LoginPage';
import RegisterPage from '@/pages/RegisterPage';
import ProjectsPage from '@/pages/ProjectsPage';
import ProjectBoardPage from '@/pages/ProjectBoardPage';
import NotFoundPage from '@/pages/NotFoundPage';

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        <Route path={ROUTES.REGISTER} element={<RegisterPage />} />

        <Route
          path={ROUTES.HOME}
          element={
            <ProtectedRoute>
              <Navigate to={ROUTES.PROJECTS} replace />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.PROJECTS}
          element={
            <ProtectedRoute>
              <ProjectsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path={ROUTES.PROJECT_BOARD}
          element={
            <ProtectedRoute>
              <ProjectBoardPage />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;

