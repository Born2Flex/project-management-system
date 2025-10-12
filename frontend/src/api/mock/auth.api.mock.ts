import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '@/types/auth.types';

// Mock delay to simulate network request
const delay = (ms: number = 500) => new Promise(resolve => setTimeout(resolve, ms));

// Mock user data
const mockUser = {
  id: 1,
  username: 'testuser',
  name: 'Test User',
  email: 'test@example.com',
  role: {
    id: 1,
    name: 'USER',
  },
};

const mockToken = 'mock-jwt-token-' + Date.now();

export const authApiMock = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    await delay();
    
    // Simple validation - accept any email/password for demo
    if (!credentials.email || !credentials.password) {
      throw new Error('Email and password are required');
    }

    return {
      token: mockToken,
      user: {
        ...mockUser,
        email: credentials.email,
      },
    };
  },

  register: async (userData: RegisterRequest): Promise<RegisterResponse> => {
    await delay();
    
    // Simple validation
    if (!userData.email || !userData.password || !userData.name || !userData.username) {
      throw new Error('All fields are required');
    }

    if (userData.password !== userData.confirmPassword) {
      throw new Error('Passwords do not match');
    }

    return {
      token: mockToken,
      user: {
        ...mockUser,
        username: userData.username,
        name: userData.name,
        email: userData.email,
      },
    };
  },

  logout: async (): Promise<void> => {
    await delay(200);
    // Mock logout - just simulate delay
  },
};

export default authApiMock;

