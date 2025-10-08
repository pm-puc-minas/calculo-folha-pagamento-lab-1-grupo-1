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
  error: null,
  calculationInProgress: false,
};

// Async thunks for payroll operations
export const fetchPayrolls = createAsyncThunk(
  'payroll/fetchPayrolls',
  async () => {
    const response = await fetch('/api/folha-pagamento');
    
    if (!response.ok) {
      throw new Error('Failed to fetch payrolls');
    }
    
    return response.json();
  }
);

export const calculatePayroll = createAsyncThunk(
  'payroll/calculatePayroll',
  async (data: { employeeId: number; month: string }) => {
    const response = await fetch('/api/folha-pagamento/calcular', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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
    const response = await fetch(`/api/folha-pagamento/${id}`);
    
    if (!response.ok) {
      throw new Error('Failed to fetch payroll');
    }
    
    return response.json();
  }
);

export const fetchPayrollsByEmployee = createAsyncThunk(
  'payroll/fetchPayrollsByEmployee',
  async (employeeId: number) => {
    const response = await fetch(`/api/folha-pagamento/funcionario/${employeeId}`);
    
    if (!response.ok) {
      throw new Error('Failed to fetch employee payrolls');
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
      });
  },
});

export const { clearError, clearSelectedPayroll, setCalculationInProgress } = payrollSlice.actions;
export default payrollSlice.reducer;