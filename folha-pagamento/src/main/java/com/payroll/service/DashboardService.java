package com.payroll.service;

import com.payroll.dtos.dashboard.DashboardDTO;
import com.payroll.dtos.dashboard.RecentEmployeeDTO;
import com.payroll.dtos.dashboard.SalaryDistributionDTO;
import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollCalculationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    public DashboardDTO getDashboardData(String currentUsername) {
        DashboardDTO dashboard = new DashboardDTO();
        
        // 1. Current User
        dashboard.setCurrentUser(currentUsername);

        // 2. Total Employees
        long totalEmployees = employeeRepository.count();
        dashboard.setTotalEmployees(totalEmployees);

        // 3. Last Payroll Date
        dashboard.setLastPayrollDate(
            payrollRepository.findTopByOrderByCreatedAtDesc()
                .map(p -> p.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .orElse("N/A")
        );

        // 4. Total Payrolls (Current Month)
        // Calculate current month string YYYY-MM
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        long totalPayrolls = payrollRepository.countByReferenceMonth(currentMonth);
        dashboard.setTotalPayrolls(totalPayrolls);

        // 5. Pending Calculations
        // Only consider pending if totalEmployees > totalPayrolls, else 0 (avoid negative if logic changes)
        long pending = Math.max(0, totalEmployees - totalPayrolls);
        dashboard.setPendingCalculations(pending);

        // 6. Recent Employees
        List<Employee> recent = employeeRepository.findTop5ByOrderByAdmissionDateDesc();
        List<RecentEmployeeDTO> recentDTOs = recent.stream().map(e -> new RecentEmployeeDTO(
            e.getId(),
            e.getFullName(),
            e.getPosition(),
            e.getSalary(),
            e.getAdmissionDate()
        )).collect(Collectors.toList());
        dashboard.setRecentEmployees(recentDTOs);

        // 7. Salary Distribution
        List<Employee> allEmployees = employeeRepository.findAll();
        dashboard.setSalaryDistribution(calculateSalaryDistribution(allEmployees));

        // 8. Total Salaries
        BigDecimal totalSalaries = allEmployees.stream()
            .map(e -> e.getSalary() != null ? e.getSalary() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalSalaries(totalSalaries);

        return dashboard;
    }

    private List<SalaryDistributionDTO> calculateSalaryDistribution(List<Employee> employees) {
        long range1 = 0; // Até 2.000
        long range2 = 0; // 2.001 – 4.000
        long range3 = 0; // 4.001 – 6.000
        long range4 = 0; // 6.001 – 10.000
        long range5 = 0; // Acima de 10.000

        for (Employee e : employees) {
            BigDecimal salary = e.getSalary();
            if (salary == null) continue;
            double val = salary.doubleValue();

            if (val <= 2000) range1++;
            else if (val <= 4000) range2++;
            else if (val <= 6000) range3++;
            else if (val <= 10000) range4++;
            else range5++;
        }

        List<SalaryDistributionDTO> distribution = new ArrayList<>();
        distribution.add(new SalaryDistributionDTO("Até R$ 2.000", range1));
        distribution.add(new SalaryDistributionDTO("R$ 2.001 – R$ 4.000", range2));
        distribution.add(new SalaryDistributionDTO("R$ 4.001 – R$ 6.000", range3));
        distribution.add(new SalaryDistributionDTO("R$ 6.001 – R$ 10.000", range4));
        distribution.add(new SalaryDistributionDTO("Acima de R$ 10.000", range5));

        return distribution;
    }
}
