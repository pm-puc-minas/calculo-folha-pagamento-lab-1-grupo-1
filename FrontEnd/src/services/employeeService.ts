import { Employee } from '@/types/employee';

const API_URL = '/api/employees';

const authHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};

export const employeeService = {
  search: async (query: string): Promise<Employee[]> => {
    const url = query ? `${API_URL}?search=${encodeURIComponent(query)}` : API_URL;
    const response = await fetch(url, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Falha ao buscar funcionários');
    
    // Need to transform the data to match the Employee type if the API returns a DTO
    // Assuming the API returns a compatible structure or we just use what we need (id, name)
    const data = await response.json();
    return data; 
  }
};
