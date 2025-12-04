import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

export interface Employee {
  id?: number;
  name: string;
  cpf: string;
  position: string;
  department: string;
  admissionDate: string;
  baseSalary: number;
  dependents: number;
  hasHazardPay: boolean;
  insalubrity: 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH';
  transportVoucherValue: number;
  mealVoucherDaily: number;
  workDaysMonth: number;
  weeklyHours: number;
}

// Payload específico para criação/atualização alinhado ao backend (Entity Employee)
export interface CreateEmployeePayload {
  fullName: string;
  cpf: string;
  rg: string;
  position: string;
  admissionDate: string; // yyyy-MM-dd
  salary: number;
  weeklyHours: number;
  dependents?: number;
  transportVoucher?: boolean;
  mealVoucher?: boolean;
  mealVoucherValue?: number;
  dangerousWork?: boolean;
  dangerousPercentage?: number;
  unhealthyWork?: boolean;
  unhealthyLevel?: string;
}

interface EmployeeState {
  employees: Employee[];
  selectedEmployee: Employee | null;
  isLoading: boolean;
  error: string | null;
  searchTerm: string;
}

const initialState: EmployeeState = {
  employees: [],
  selectedEmployee: null,
  isLoading: false,
  error: null,
  searchTerm: '',
};

// Async thunks for employee operations
export const fetchEmployees = createAsyncThunk(
  'employee/fetchEmployees',
  async () => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch('/api/employees', {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch employees');
    }
    
    return response.json();
  }
);

export const createEmployee = createAsyncThunk(
  'employee/createEmployee',
  async (employeeData: CreateEmployeePayload) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    // Mapeia o payload do frontend para o schema esperado pelo backend (Entity Employee)
    const body = {
      fullName: employeeData.fullName,
      cpf: employeeData.cpf,
      rg: employeeData.rg,
      position: employeeData.position,
      admissionDate: employeeData.admissionDate,
      salary: employeeData.salary,
      weeklyHours: employeeData.weeklyHours,
      dependents: employeeData.dependents ?? 0,
      transportVoucher: employeeData.transportVoucher ?? false,
      mealVoucher: employeeData.mealVoucher ?? false,
      mealVoucherValue: employeeData.mealVoucherValue ?? 0,
      dangerousWork: employeeData.dangerousWork ?? false,
      dangerousPercentage: employeeData.dangerousPercentage ?? 0,
      unhealthyWork: employeeData.unhealthyWork ?? false,
      unhealthyLevel: employeeData.unhealthyLevel ?? 'NONE'
    };

    const response = await fetch('/api/employees', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(token ? { 'Authorization': `Bearer ${token}` } : {}) },
      body: JSON.stringify(body),
    });
    
    if (!response.ok) {
      throw new Error('Failed to create employee');
    }
    
    return response.json();
  }
);

export const updateEmployee = createAsyncThunk(
  'employee/updateEmployee',
  async ({ id, data }: { id: number; data: CreateEmployeePayload }) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const body = {
      fullName: data.fullName,
      cpf: data.cpf,
      rg: data.rg,
      position: data.position,
      admissionDate: data.admissionDate,
      salary: data.salary,
      weeklyHours: data.weeklyHours,
      dependents: data.dependents ?? 0,
      transportVoucher: data.transportVoucher ?? false,
      mealVoucher: data.mealVoucher ?? false,
      mealVoucherValue: data.mealVoucherValue ?? 0,
      dangerousWork: data.dangerousWork ?? false,
      dangerousPercentage: data.dangerousPercentage ?? 0,
      unhealthyWork: data.unhealthyWork ?? false,
      unhealthyLevel: data.unhealthyLevel ?? 'NONE'
    };

    const response = await fetch(`/api/employees/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...(token ? { 'Authorization': `Bearer ${token}` } : {}) },
      body: JSON.stringify(body),
    });
    
    if (!response.ok) {
      throw new Error('Failed to update employee');
    }
    
    return response.json();
  }
);

export const deleteEmployee = createAsyncThunk(
  'employee/deleteEmployee',
  async (id: number) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch(`/api/employees/${id}`, {
      method: 'DELETE',
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to delete employee');
    }
    
    return id;
  }
);

export const fetchEmployeeById = createAsyncThunk(
  'employee/fetchEmployeeById',
  async (id: number) => {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
    const response = await fetch(`/api/employees/${id}`, {
      headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch employee');
    }
    
    return response.json();
  }
);

const employeeSlice = createSlice({
  name: 'employee',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSearchTerm: (state, action: PayloadAction<string>) => {
      state.searchTerm = action.payload;
    },
    clearSelectedEmployee: (state) => {
      state.selectedEmployee = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch employees
      .addCase(fetchEmployees.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchEmployees.fulfilled, (state, action) => {
        state.isLoading = false;
        state.employees = action.payload;
      })
      .addCase(fetchEmployees.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to fetch employees';
      })
      // Create employee
      .addCase(createEmployee.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createEmployee.fulfilled, (state, action) => {
        state.isLoading = false;
        state.employees.push(action.payload);
      })
      .addCase(createEmployee.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Failed to create employee';
      })
      // Update employee
      .addCase(updateEmployee.fulfilled, (state, action) => {
        const index = state.employees.findIndex(emp => emp.id === action.payload.id);
        if (index !== -1) {
          state.employees[index] = action.payload;
        }
      })
      // Delete employee
      .addCase(deleteEmployee.fulfilled, (state, action) => {
        state.employees = state.employees.filter(emp => emp.id !== action.payload);
      })
      // Fetch employee by ID
      .addCase(fetchEmployeeById.fulfilled, (state, action) => {
        state.selectedEmployee = action.payload;
      });
  },
});

export const { clearError, setSearchTerm, clearSelectedEmployee } = employeeSlice.actions;
export default employeeSlice.reducer;
