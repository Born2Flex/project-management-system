import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/utils/constants';
import { isValidEmail, isRequired } from '@/utils/validation';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import Card from '@/components/common/Card';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const { login, isLoggingIn, loginError } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});

  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.PROJECTS);
    }
  }, [isAuthenticated, navigate]);

  const validate = (): boolean => {
    const newErrors: { email?: string; password?: string } = {};

    if (!isRequired(email)) {
      newErrors.email = 'Email is required';
    } else if (!isValidEmail(email)) {
      newErrors.email = 'Invalid email format';
    }

    if (!isRequired(password)) {
      newErrors.password = 'Password is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      login({ email, password });
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-md" padding="lg">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome Back</h1>
          <p className="text-gray-600">Sign in to your Progma account</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            type="email"
            label="Email"
            placeholder="your@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={errors.email}
            fullWidth
          />

          <Input
            type="password"
            label="Password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={errors.password}
            fullWidth
          />

          {loginError && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-600">
                Invalid email or password. Please try again.
              </p>
            </div>
          )}

          <Button type="submit" fullWidth isLoading={isLoggingIn}>
            Sign In
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Don't have an account?{' '}
            <Link to={ROUTES.REGISTER} className="text-blue-600 hover:text-blue-700 font-medium">
              Sign up
            </Link>
          </p>
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;

