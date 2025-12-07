import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Employee } from '@/types/employee';

type ApiEmployee = any;

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

const toApiPayload = (emp: Employee): any => {
  return {
    id: emp.id,
    name: emp.name,
    cpf: emp.cpf,
    position: emp.position,
    admissionDate: emp.admissionDate,
    baseSalary: emp.grossSalary,
    grossSalary: emp.grossSalary,
    dependents: emp.dependents,
    weeklyHours: emp.hoursPerDay * emp.daysPerWeek,
    transportVoucherValue: emp.transportVoucherValue,
    mealVoucherDaily: emp.mealVoucherDaily,
    pensionAlimony: emp.pensionAlimony,
    hasHazardPay: emp.isDangerous,
    insalubrity: emp.unhealthyLevel?.toUpperCase(),
    workDaysMonth: emp.workDaysInMonth,
    
    // New fields
    hasHealthPlan: emp.hasHealthPlan,
    healthPlanValue: emp.healthPlanValue,
    hasDentalPlan: emp.hasDentalPlan,
    dentalPlanValue: emp.dentalPlanValue,
    hasGym: emp.hasGym,
    gymValue: emp.gymValue,
    hasTimeBank: emp.hasTimeBank,
    timeBankHours: emp.timeBankHours,
    hasOvertime: emp.hasOvertime,
    overtimeHours: emp.overtimeHours,
  };
};

const fromApi = (api: ApiEmployee): Employee => {
  return {
    id: api.id,
    name: api.name || api.fullName,
    cpf: api.cpf,
    position: api.position,
    admissionDate: api.admissionDate,
    grossSalary: api.grossSalary ?? api.baseSalary ?? 0,
    hoursPerDay: api.weeklyHours ? Math.round(api.weeklyHours / 5) : 8,
    daysPerWeek: api.workDaysMonth ? Math.round((api.weeklyHours || 40) / (api.workDaysMonth / 4)) : 5,
    dependents: api.dependents ?? 0,
    transportVoucherValue: api.transportVoucherValue ?? 0,
    mealVoucherDaily: api.mealVoucherDaily ?? 0,
    workDaysInMonth: api.workDaysMonth ?? 22,
    isDangerous: api.hasHazardPay ?? api.dangerousWork ?? false,
    unhealthyLevel: (api.insalubrity || api.unhealthyLevel || 'none').toLowerCase(),
    pensionAlimony: api.pensionAlimony ?? 0,
    
    // New fields
    hasHealthPlan: api.hasHealthPlan ?? false,
    healthPlanValue: api.healthPlanValue ?? 0,
    hasDentalPlan: api.hasDentalPlan ?? false,
    dentalPlanValue: api.dentalPlanValue ?? 0,
    hasGym: api.hasGym ?? false,
    gymValue: api.gymValue ?? 0,
    hasTimeBank: api.hasTimeBank ?? false,
    timeBankHours: api.timeBankHours ?? 0,
    hasOvertime: api.hasOvertime ?? false,
    overtimeHours: api.overtimeHours ?? 0,
  };
};

const authHeader = () => {
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const fetchEmployees = createAsyncThunk('employee/fetchEmployees', async () => {
  const response = await fetch('/api/employees', { headers: authHeader() });
  if (!response.ok) {
    throw new Error('Failed to fetch employees');
  }
  const data = await response.json();
  return (Array.isArray(data) ? data : []).map(fromApi);
});

export const createEmployee = createAsyncThunk(
  'employee/createEmployee',
  async (employeeData: Employee, { rejectWithValue }) => {
    try {
      const payload = toApiPayload(employeeData);
      const response = await fetch('/api/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        return rejectWithValue(text || 'Failed to create employee');
      }

      const data = await response.json();
      return fromApi(data);
    } catch (err: any) {
      return rejectWithValue(err.message || 'Failed to create employee');
    }
  }
);

export const updateEmployee = createAsyncThunk(
  'employee/updateEmployee',
  async ({ id, data }: { id: number; data: Employee }) => {
    const response = await fetch(`/api/employees/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...authHeader() },
      body: JSON.stringify(toApiPayload({ ...data, id } as Employee)),
    });
    if (!response.ok) {
      throw new Error('Failed to update employee');
    }
    const updated = await response.json();
    return fromApi(updated);
  }
);

export const deleteEmployee = createAsyncThunk('employee/deleteEmployee', async (id: number) => {
  const response = await fetch(`/api/employees/${id}`, {
    method: 'DELETE',
    headers: authHeader(),
  });
  if (!response.ok) {
    throw new Error('Failed to delete employee');
  }
  return id;
});

export const fetchEmployeeById = createAsyncThunk('employee/fetchEmployeeById', async (id: number) => {
  const response = await fetch(`/api/employees/${id}`, { headers: authHeader() });
  if (!response.ok) {
    throw new Error('Failed to fetch employee');
  }
  const data = await response.json();
  return fromApi(data);
});

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
      .addCase(updateEmployee.fulfilled, (state, action) => {
        const index = state.employees.findIndex((emp) => emp.id === action.payload.id);
        if (index !== -1) {
          state.employees[index] = action.payload;
        }
      })
      .addCase(deleteEmployee.fulfilled, (state, action) => {
        state.employees = state.employees.filter((emp) => emp.id !== action.payload);
      })
      .addCase(fetchEmployeeById.fulfilled, (state, action) => {
        state.selectedEmployee = action.payload;
      });
  },
});

export const { clearError, setSearchTerm, clearSelectedEmployee } = employeeSlice.actions;
export default employeeSlice.reducer;
