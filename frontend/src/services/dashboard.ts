import api from './api';

export interface DashboardStats {
  userCount: number;
  departmentCount: number;
  activeUserCount: number;
}

export const getDashboardStats = async (): Promise<DashboardStats> => {
  const response = await api.get('/dashboard/stats');
  return response.data.data;
};
