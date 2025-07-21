import api from './api';

export interface User {
  id: number;
  username: string;
  realname: string;
  email?: string;
  mobile?: string;
  status: number;
  enabled: boolean;
  createdTime?: string;
  updatedTime?: string;
}

export interface UserRequest {
  username: string;
  password?: string;
  realname: string;
  email?: string;
  mobile?: string;
}

export interface UserListResponse {
  code: number;
  message: string;
  data: {
    records: User[];
    total: number;
    size: number;
    current: number;
    pages: number;
  };
}

export interface UserResponse {
  code: number;
  message: string;
  data: User;
}

export const userService = {
  getUsers: (page: number = 1, perPage: number = 10, query?: string) => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('per_page', perPage.toString());
    if (query) {
      params.append('query', query);
    }
    return api.get<UserListResponse>(`/users?${params.toString()}`);
  },

  getUser: (id: number) => 
    api.get<UserResponse>(`/users/${id}`),

  createUser: (user: UserRequest) => 
    api.post<UserResponse>('/users', user),

  updateUser: (id: number, user: Partial<UserRequest>) => 
    api.put<UserResponse>(`/users/${id}`, user),

  deleteUser: (id: number) => 
    api.delete(`/users/${id}`),

  updateUserStatus: (id: number, enabled: boolean) => 
    api.patch(`/users/${id}/status`, { enabled }),

  resetPassword: (id: number) => 
    api.patch(`/users/${id}/reset-password`),
};
