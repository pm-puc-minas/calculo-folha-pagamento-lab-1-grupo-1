import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { apiFetch } from '@/lib/apiClient';

interface Employee {
  id: number;
  name: string;
  cpf: string;
  position: string;
  admissionDate: string;
}

interface PayrollRecord {
  id: number;
  employeeName: string;
  month: string;
  grossSalary: number;
  netSalary: number;
}

interface DashboardStats {
  totalEmployees: number;
  totalPayrolls: number;
  totalGrossSalary: number;
  totalNetSalary: number;
}

interface DashboardState {
  stats: DashboardStats;
  recentEmployees: Employee[];
  recentPayrolls: PayrollRecord[];
  isLoading: boolean;
  error: string | null;
}

const initialState: DashboardState = {
  stats: {
    totalEmployees: 0,
    totalPayrolls: 0,
    totalGrossSalary: 0,
    totalNetSalary: 0,
  },
  recentEmployees: [],
  recentPayrolls: [],
  isLoading: false,
  error: null,
};

// Async thunk for fetching dashboard data
export const fetchDashboardData = createAsyncThunk(
  'dashboard/fetchDashboardData',
  async () => {
    const response = await apiFetch('/api/dashboard');
    return response.json();
  }
);

const dashboardSlice = createSlice({
  name: 'dashboard',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    updateStats: (state, action: PayloadAction<Partial<DashboardStats>>) => {
      state.stats = { ...state.stats, ...action.payload };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDashboardData.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchDashboardData.fulfilled, (state, action) => {
        state.isLoading = false;
        const payload = action.payload || {};
        state.stats = {
          totalEmployees: payload.totalEmployees ?? 0,
          totalPayrolls: payload.totalPayrolls ?? 0,
          totalGrossSalary: payload.totalGrossSalary ?? 0,
          totalNetSalary: payload.totalNetSalary ?? 0,
        };
        state.recentEmployees = payload.recentEmployees ?? [];
        state.recentPayrolls = payload.recentPayrolls ?? [];
      })
      .addCase(fetchDashboardData.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to fetch dashboard data';
      });
  },
});

export const { clearError, updateStats } = dashboardSlice.actions;
export default dashboardSlice.reducer;
