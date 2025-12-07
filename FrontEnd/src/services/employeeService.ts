import { ReportHistoryEntry } from "@/components/Reports/ReportHistory";

const API_URL = '/api/employees';

const authHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};

export const employeeService = {
  search: async (query: string): Promise<any[]> => {
    const url = query ? `${API_URL}?search=${encodeURIComponent(query)}` : API_URL;
    const response = await fetch(url, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Não foi possível buscar funcionários.');
    const data = await response.json();
    return data; 
  }
};
