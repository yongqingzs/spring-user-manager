import api from './api';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  code: number;
  message: string;
  data: string; // JWT token
}

export const authService = {
  login: (credentials: LoginRequest) => 
    api.post<AuthResponse>('/auth/login', credentials),
  
  logout: () => 
    api.post('/auth/logout'),
};
