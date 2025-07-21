import api from './api';

export interface Department {
  id: number;
  code: string;
  name: string;
  parentCode?: string;
  description?: string;
  children?: Department[];
  createdTime?: string;
  updatedTime?: string;
}

export interface DepartmentRequest {
  code: string;
  name: string;
  parentCode?: string;
  description?: string;
}

export interface DepartmentListResponse {
  code: number;
  message: string;
  data: {
    records: Department[];
    total: number;
    size: number;
    current: number;
    pages: number;
  };
}

export interface DepartmentResponse {
  code: number;
  message: string;
  data: Department;
}

export interface DepartmentTreeResponse {
  code: number;
  message: string;
  data: Department[];
}

export const departmentService = {
  getDepartments: (page: number = 1, perPage: number = 10, search?: string) => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('per_page', perPage.toString());
    if (search) {
      params.append('search', search);
    }
    return api.get<DepartmentListResponse>(`/departments?${params.toString()}`);
  },

  getDepartmentTree: () => 
    api.get<DepartmentTreeResponse>('/departments/tree'),

  getDepartment: (id: number) => 
    api.get<DepartmentResponse>(`/departments/${id}`),

  createDepartment: (department: DepartmentRequest) => 
    api.post<DepartmentResponse>('/departments', department),

  updateDepartment: (id: number, department: Partial<DepartmentRequest>) => 
    api.put<DepartmentResponse>(`/departments/${id}`, department),

  deleteDepartment: (id: number) => 
    api.delete(`/departments/${id}`),
};
