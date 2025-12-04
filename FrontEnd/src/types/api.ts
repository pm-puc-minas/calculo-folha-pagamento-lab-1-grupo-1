export type UserRole = 'USER' | 'ADMIN';

export interface ApiUser {
  id: number;
  username: string;
  email: string;
  role: UserRole;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  user: ApiUser;
}

export type InsalubrityLevel = 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH';

export interface EmployeeDto {
  id?: number;
  name: string;
  cpf: string;
  position: string;
  department: string;
  admissionDate: string;
  baseSalary: number;
  dependents: number;
  hasHazardPay: boolean;
  insalubrity: InsalubrityLevel;
  transportVoucherValue: number;
  mealVoucherDaily: number;
  workDaysMonth: number;
  weeklyHours: number;
}

export interface DashboardStatsDto {
  totalEmployees: number;
  totalPayrolls: number;
  totalGrossSalary: number;
  totalNetSalary: number;
}

export interface DashboardResponse {
  stats: DashboardStatsDto;
  recentEmployees: Array<Pick<EmployeeDto, 'id' | 'name' | 'cpf' | 'position' | 'admissionDate'>>;
  recentPayrolls: Array<{
    id: number;
    employeeName: string;
    month: string;
    grossSalary: number;
    netSalary: number;
  }>;
}

export interface PayrollRecordDto {
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

export interface ReportHistoryEntryDto {
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

export interface FileEntryDto {
  id: string;
  name: string;
  uploadedAt: string;
  size: number;
  type: string;
  uploadedBy: string;
}
