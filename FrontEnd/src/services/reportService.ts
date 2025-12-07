import { ReportHistoryEntry } from "@/components/Reports/ReportHistory";

const API_URL = '/api/reports';

const authHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};

export interface GenerateReportRequest {
  employeeId: string;
  referenceMonth: string;
  reportType: string;
}

export const reportService = {
  getHistory: async (referenceMonth?: string): Promise<ReportHistoryEntry[]> => {
    const url = referenceMonth 
      ? `${API_URL}/history?referenceMonth=${referenceMonth}`
      : `${API_URL}/history`;
      
    const response = await fetch(url, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Não foi possível carregar o histórico de relatórios.');
    return response.json();
  },

  generate: async (data: GenerateReportRequest): Promise<ReportHistoryEntry> => {
    const response = await fetch(`${API_URL}`, {
      method: 'POST',
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Erro ao gerar relatório. Verifique os campos e tente novamente.');
    return response.json();
  },

  delete: async (id: string): Promise<void> => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      headers: {
        ...authHeader(),
      },
    });
    if (!response.ok) throw new Error('Não foi possível excluir o relatório.');
  },

  download: async (id: string): Promise<void> => {
    const response = await fetch(`${API_URL}/${id}/download`, {
      headers: {
        ...authHeader(),
      },
    });
    if (!response.ok) throw new Error('Não foi possível baixar o relatório.');
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `report-${id}.pdf`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  },
};
