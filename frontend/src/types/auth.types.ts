export interface User {
  id: number;
  username: string;
  name: string;
  email: string;
  role: UserRole;
}

export enum UserRole {
  USER = 'USER',
  PROJECT_MANAGER = 'PROJECT_MANAGER',
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  accessToken: string;
  refreshToken: string;
}

export interface RegisterRequest {
  username: string;
  name: string;
  email: string;
  password: string;
  role?: UserRole;
}

export interface RegisterResponse {
  id: number;
  username: string;
  name: string;
  email: string;
  role: UserRole;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface UserUpdateRequest {
  username?: string;
  name?: string;
  email?: string;
  role?: UserRole;
}
