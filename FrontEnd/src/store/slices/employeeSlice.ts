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
    const response = await fetch('/api/employees');
    
    if (!response.ok) {
      throw new Error('Failed to fetch employees');
    }
    
    return response.json();
  }
);

export const createEmployee = createAsyncThunk(
  'employee/createEmployee',
  async (employeeData: Employee) => {
    const response = await fetch('/api/employees', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(employeeData),
    });
    
    if (!response.ok) {
      throw new Error('Failed to create employee');
    }
    
    return response.json();
  }
);

export const updateEmployee = createAsyncThunk(
  'employee/updateEmployee',
  async ({ id, data }: { id: number; data: Employee }) => {
    const response = await fetch(`/api/employees/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
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
    const response = await fetch(`/api/employees/${id}`, {
      method: 'DELETE',
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
    const response = await fetch(`/api/employees/${id}`);
    
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