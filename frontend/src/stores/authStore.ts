import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { type User } from '@/types/auth.types';
import { TOKEN_KEY, USER_KEY } from '@/utils/constants';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  setUser: (user: User) => void;
  initializeAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,

      login: (token: string, user: User) => {
        localStorage.setItem(TOKEN_KEY, token);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        set({ token, user, isAuthenticated: true });
      },

      logout: () => {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        set({ token: null, user: null, isAuthenticated: false });
      },

      setUser: (user: User) => {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        set({ user });
      },

      initializeAuth: () => {
        const token = localStorage.getItem(TOKEN_KEY);
        const userStr = localStorage.getItem(USER_KEY);
        
        if (token && userStr) {
          try {
            const user = JSON.parse(userStr) as User;
            set({ token, user, isAuthenticated: true });
          } catch (error) {
            console.error('Failed to parse user data:', error);
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
          }
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

export default useAuthStore;

