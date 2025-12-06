import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

interface SalaryDistribution {
  range: string;
  count: number;
}

interface RecentEmployee {
  id: number;
  fullName: string;
  position: string;
  salary: number;
  admissionDate: string;
}

interface DashboardData {
  currentUser: string | null;
  totalEmployees: number;
  lastPayrollDate: string | null;
  pendingCalculations: number;
  salaryDistribution: SalaryDistribution[];
  recentEmployees: RecentEmployee[];
  totalPayrolls: number;
  totalSalaries: number;
}

interface DashboardState {
  data: DashboardData | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: DashboardState = {
  data: null,
  isLoading: false,
  error: null,
};

// Async thunk for fetching dashboard data
export const fetchDashboardData = createAsyncThunk(
  'dashboard/fetchDashboardData',
  async () => {
    const token = localStorage.getItem('accessToken');
    const response = await fetch('/api/dashboard', {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch dashboard data');
    }
    
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
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDashboardData.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchDashboardData.fulfilled, (state, action) => {
        state.isLoading = false;
        state.data = action.payload;
      })
      .addCase(fetchDashboardData.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to fetch dashboard data';
      });
  },
});

export const { clearError } = dashboardSlice.actions;
export default dashboardSlice.reducer;
