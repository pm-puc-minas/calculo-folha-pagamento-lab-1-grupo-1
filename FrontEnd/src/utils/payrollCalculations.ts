import { Employee, PayrollCalculation, INSSBracket, IRPFBracket } from '@/types/employee';

// Tabela INSS 2024
const INSS_BRACKETS: INSSBracket[] = [
  { min: 0, max: 1302.00, rate: 0.075 },
  { min: 1302.01, max: 2571.29, rate: 0.09 },
  { min: 2571.30, max: 3856.94, rate: 0.12 },
  { min: 3856.95, max: 7507.49, rate: 0.14 }
];

// Tabela IRPF 2024
const IRPF_BRACKETS: IRPFBracket[] = [
  { min: 0, max: 1903.98, rate: 0, deduction: 0 },
  { min: 1903.99, max: 2826.65, rate: 0.075, deduction: 142.80 },
  { min: 2826.66, max: 3751.05, rate: 0.15, deduction: 354.80 },
  { min: 3751.06, max: 4664.68, rate: 0.225, deduction: 636.13 },
  { min: 4664.69, max: Infinity, rate: 0.275, deduction: 869.36 }
];

const MINIMUM_WAGE = 1380.60; // 2024
const DEPENDENT_DEDUCTION = 189.59;
const FGTS_RATE = 0.08;

export class PayrollCalculator {
  
  // RF1 - Calcular Salário Hora
  static calculateHourlyWage(employee: Employee): { hourlyWage: number; weeklyHours: number; monthlyHours: number } {
    const weeklyHours = employee.hoursPerDay * employee.daysPerWeek;
    const monthlyHours = weeklyHours * (employee.workDaysInMonth / employee.daysPerWeek);
    const hourlyWage = employee.grossSalary / monthlyHours;
    
    return { hourlyWage, weeklyHours, monthlyHours };
  }
  
  // RF2 - Calcular Periculosidade (30% do salário base)
  static calculateDangerousBonus(employee: Employee): number {
    return employee.isDangerous ? employee.grossSalary * 0.30 : 0;
  }
  
  // RF3 - Calcular Insalubridade (10%, 20%, 40% do salário mínimo)
  static calculateUnhealthyBonus(employee: Employee): number {
    const rates = {
      'none': 0,
      'low': 0.10,
      'medium': 0.20,
      'high': 0.40
    };
    
    return MINIMUM_WAGE * rates[employee.unhealthyLevel];
  }
  
  // RF4 - Calcular Vale Transporte (desconto máximo 6%)
  static calculateTransportVoucherDiscount(employee: Employee): number {
    const maxDiscount = employee.grossSalary * 0.06;
    return Math.min(employee.transportVoucherValue, maxDiscount);
  }
  
  // RF5 - Calcular Vale Alimentação
  static calculateMealVoucher(employee: Employee): number {
    return employee.mealVoucherDaily * employee.workDaysInMonth;
  }
  
  // RF6 - Calcular Desconto de INSS (progressivo)
  static calculateINSS(grossSalary: number): { discount: number; effectiveRate: number } {
    let totalDiscount = 0;
    
    for (const bracket of INSS_BRACKETS) {
      if (grossSalary > bracket.min) {
        const taxableAmount = Math.min(grossSalary, bracket.max) - (bracket.min - 0.01);
        totalDiscount += taxableAmount * bracket.rate;
      }
    }
    
    const effectiveRate = grossSalary > 0 ? totalDiscount / grossSalary : 0;
    return { discount: totalDiscount, effectiveRate };
  }
  
  // RF7 - Calcular FGTS (8% do salário bruto - encargo patronal)
  static calculateFGTS(grossSalary: number): number {
    return grossSalary * FGTS_RATE;
  }
  
  // RF8 - Calcular Desconto de IRPF
  static calculateIRPF(employee: Employee, grossSalary: number, inssDiscount: number): { discount: number; effectiveRate: number; calculationBase: number } {
    // Base de cálculo = salário bruto - INSS - deduções por dependente
    const dependentDeductions = employee.dependents * DEPENDENT_DEDUCTION;
    const calculationBase = Math.max(0, grossSalary - inssDiscount - dependentDeductions - employee.pensionAlimony);
    
    // Encontrar a faixa apropriada
    let bracket = IRPF_BRACKETS[0];
    for (const b of IRPF_BRACKETS) {
      if (calculationBase >= b.min && calculationBase <= b.max) {
        bracket = b;
        break;
      }
    }
    
    const grossTax = calculationBase * bracket.rate;
    const discount = Math.max(0, grossTax - bracket.deduction);
    const effectiveRate = grossSalary > 0 ? discount / grossSalary : 0;
    
    return { discount, effectiveRate, calculationBase };
  }
  
  // RF9 - Calcular Salário Líquido
  static calculateNetSalary(
    grossTotal: number, 
    inssDiscount: number, 
    irpfDiscount: number, 
    transportVoucherDiscount: number
  ): number {
    return grossTotal - inssDiscount - irpfDiscount - transportVoucherDiscount;
  }
  
  // RF10 - Cálculo Completo da Folha
  static calculatePayroll(employee: Employee, referenceMonth: string): PayrollCalculation {
    // Cálculos básicos
    const { hourlyWage, weeklyHours, monthlyHours } = this.calculateHourlyWage(employee);
    
    // Adicionais
    const dangerousBonus = this.calculateDangerousBonus(employee);
    const unhealthyBonus = this.calculateUnhealthyBonus(employee);
    
    // Benefícios
    const transportVoucher = employee.transportVoucherValue;
    const mealVoucher = this.calculateMealVoucher(employee);
    
    // Salário bruto total (salário + adicionais)
    const grossTotal = employee.grossSalary + dangerousBonus + unhealthyBonus;
    
    // Descontos
    const { discount: inssDiscount, effectiveRate: inssEffectiveRate } = this.calculateINSS(grossTotal);
    const { discount: irpfDiscount, effectiveRate: irpfEffectiveRate, calculationBase: irpfCalculationBase } = 
      this.calculateIRPF(employee, grossTotal, inssDiscount);
    const transportVoucherDiscount = this.calculateTransportVoucherDiscount(employee);
    
    // FGTS (encargo patronal)
    const fgts = this.calculateFGTS(grossTotal);
    
    // Salário líquido
    const netSalary = this.calculateNetSalary(grossTotal, inssDiscount, irpfDiscount, transportVoucherDiscount);
    
    return {
      employee,
      referenceMonth,
      hourlyWage,
      weeklyHours,
      monthlyHours,
      dangerousBonus,
      unhealthyBonus,
      transportVoucher,
      mealVoucher,
      inssDiscount,
      inssEffectiveRate,
      irpfDiscount,
      irpfEffectiveRate,
      transportVoucherDiscount,
      fgts,
      grossTotal,
      netSalary,
      inssCalculationBase: grossTotal,
      irpfCalculationBase
    };
  }
}