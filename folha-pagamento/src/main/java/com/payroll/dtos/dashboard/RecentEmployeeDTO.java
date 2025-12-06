package com.payroll.dtos.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class RecentEmployeeDTO {
    private Long id;
    private String fullName;
    private String position;
    private BigDecimal salary;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;

    public RecentEmployeeDTO() {}

    public RecentEmployeeDTO(Long id, String fullName, String position, BigDecimal salary, LocalDate admissionDate) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.salary = salary;
        this.admissionDate = admissionDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
}
