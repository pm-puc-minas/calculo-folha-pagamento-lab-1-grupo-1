export interface Employee {
  id?: number;
  name: string;
  cpf: string;
  position: string;
  admissionDate: string;
  grossSalary: number;
  hoursPerDay: number;
  daysPerWeek: number;
  dependents: number;
  transportVoucherValue: number;
  mealVoucherDaily: number;
  workDaysInMonth: number;
  isDangerous: boolean; // periculosidade
  unhealthyLevel: 'none' | 'low' | 'medium' | 'high'; // insalubridade
  pensionAlimony: number;
}

export interface PayrollCalculation {
  employee: Employee;
  referenceMonth: string;
  
  // Base calculations
  hourlyWage: number;
  weeklyHours: number;
  monthlyHours: number;
  
  // Additions (proventos)
  dangerousBonus: number; // periculosidade 30%
  unhealthyBonus: number; // insalubridade 10%, 20%, 40%
  
  // Benefits
  transportVoucher: number;
  mealVoucher: number;
  
  // Discounts (descontos)
  inssDiscount: number;
  inssEffectiveRate: number;
  irpfDiscount: number;
  irpfEffectiveRate: number;
  transportVoucherDiscount: number;
  
  // FGTS (employer cost, not employee discount)
  fgts: number;
  
  // Final values
  grossTotal: number;
  netSalary: number;
  
  // Calculation bases
  inssCalculationBase: number;
  irpfCalculationBase: number;
}

export interface INSSBracket {
  min: number;
  max: number;
  rate: number;
}

export interface IRPFBracket {
  min: number;
  max: number;
  rate: number;
  deduction: number;
}
