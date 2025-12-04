import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

export interface PayrollCalculation {
  id?: number;
  employeeId: number;
  employeeName: string;
  month: string;
  hourlyRate: number;
  totalEarnings: number;
  totalDeductions: number;
  netSalary: number;
  hazardPayValue: number;
  insalubrityValue: number;
  mealVoucherValue: number;
  transportVoucherDiscount: number;
  inssDiscount: number;
  fgtsValue: number;
  irrfDiscount: number;
  inssBase: number;
  fgtsBase: number;
  irrfBase: number;
  calculatedAt: string;
  generatedBy?: {
    id: string;
    name: string;
    email: string;
    role: string;
  };
}

interface PayrollState {
  payrolls: PayrollCalculation[];
  selectedPayroll: PayrollCalculation | null;
  reportHistory: ReportHistoryEntry[];
  isLoading: boolean;
  reportLoading: boolean;
  error: string | null;
  calculationInProgress: boolean;
}

export interface ReportHistoryEntry {
  id: string;
  reportType: 'payroll' | 'employee' | 'summary';
  employeeName: string;
  referenceMonth: string;
  generatedAt: string;
  generatedBy: {
    id: string;
    name: string;
    email: string;
    role: string;
  };
  status: 'completed' | 'pending' | 'error';
}

const initialState: PayrollState = {
  payrolls: [],
  selectedPayroll: null,
  reportHistory: [],
  isLoading: false,
  reportLoading: false,
  error: null,
  calculationInProgress: false,
};

// Async thunks for payroll operations
export const fetchPayrolls = createAsyncThunk(
  'payroll/fetchPayrolls',
  async () => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch('/api/payroll', {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch payrolls');
    }
    
    return response.json();
  }
);

export const calculatePayroll = createAsyncThunk(
  'payroll/calculatePayroll',
  async (data: { employeeId: number; referenceMonth: string }) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch('/api/payroll/calculate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(token ? { 'Authorization': `Bearer ${token}` } : {}) },
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw new Error('Failed to calculate payroll');
    }
    
    return response.json();
  }
);

export const fetchPayrollById = createAsyncThunk(
  'payroll/fetchPayrollById',
  async (id: number) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch(`/api/payroll/${id}`, {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch payroll');
    }
    
    return response.json();
  }
);

export const fetchPayrollsByEmployee = createAsyncThunk(
  'payroll/fetchPayrollsByEmployee',
  async (employeeId: number) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch(`/api/payroll/employee/${employeeId}`, {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch employee payrolls');
    }
    
    return response.json();
  }
);

export const fetchReportHistory = createAsyncThunk(
  'payroll/fetchReportHistory',
  async () => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch('/api/reports/history', {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch report history');
    }
    
    return response.json();
  }
);

export const generateReport = createAsyncThunk(
  'payroll/generateReport',
  async (data: { month?: string; employee?: string; type?: string }) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch('/api/reports', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(token ? { 'Authorization': `Bearer ${token}` } : {}) },
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      const msg = await response.text();
      throw new Error(msg || 'Failed to generate report');
    }
    
    return response.json();
  }
);

const payrollSlice = createSlice({
  name: 'payroll',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearSelectedPayroll: (state) => {
      state.selectedPayroll = null;
    },
    setCalculationInProgress: (state, action: PayloadAction<boolean>) => {
      state.calculationInProgress = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch payrolls
      .addCase(fetchPayrolls.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPayrolls.fulfilled, (state, action) => {
        state.isLoading = false;
        state.payrolls = action.payload;
      })
      .addCase(fetchPayrolls.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to fetch payrolls';
      })
      // Calculate payroll
      .addCase(calculatePayroll.pending, (state) => {
        state.calculationInProgress = true;
        state.error = null;
      })
      .addCase(calculatePayroll.fulfilled, (state, action) => {
        state.calculationInProgress = false;
        state.payrolls.push(action.payload);
        state.selectedPayroll = action.payload;
      })
      .addCase(calculatePayroll.rejected, (state, action) => {
        state.calculationInProgress = false;
        state.error = action.error.message || 'Failed to calculate payroll';
      })
      // Fetch payroll by ID
      .addCase(fetchPayrollById.fulfilled, (state, action) => {
        state.selectedPayroll = action.payload;
      })
      // Fetch payrolls by employee
      .addCase(fetchPayrollsByEmployee.fulfilled, (state, action) => {
        // Update the payrolls array with employee-specific payrolls
        const employeePayrolls = action.payload;
        employeePayrolls.forEach((payroll: PayrollCalculation) => {
          const index = state.payrolls.findIndex(p => p.id === payroll.id);
          if (index === -1) {
            state.payrolls.push(payroll);
          }
        });
      })
      // Reports history
      .addCase(fetchReportHistory.pending, (state) => {
        state.reportLoading = true;
        state.error = null;
      })
      .addCase(fetchReportHistory.fulfilled, (state, action) => {
        state.reportLoading = false;
        state.reportHistory = action.payload;
      })
      .addCase(fetchReportHistory.rejected, (state, action) => {
        state.reportLoading = false;
        state.error = action.error.message || 'Failed to fetch report history';
      })
      // Generate report
      .addCase(generateReport.pending, (state) => {
        state.reportLoading = true;
        state.error = null;
      })
      .addCase(generateReport.fulfilled, (state) => {
        state.reportLoading = false;
      })
      .addCase(generateReport.rejected, (state, action) => {
        state.reportLoading = false;
        state.error = action.error.message || 'Failed to generate report';
      });
  },
});

export const { clearError, clearSelectedPayroll, setCalculationInProgress } = payrollSlice.actions;
export default payrollSlice.reducer;
