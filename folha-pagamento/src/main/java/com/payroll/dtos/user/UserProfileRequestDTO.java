package com.payroll.dtos.user;

public class UserProfileRequestDTO {
    private String password; // nova senha opcional

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
