package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    private Position position;

    @BeforeEach
    void setUp() {
        position = new Position(1, "Analista", BigDecimal.valueOf(5000));
    }

    // Testa o ajuste do salário base de um cargo
    @Test
    void testAjustarSalario() {
        position.ajustarSalario(BigDecimal.valueOf(6000));
        assertEquals(BigDecimal.valueOf(6000), position.getSalarioBase());
    }
}
