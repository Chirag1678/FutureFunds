package com.cg.futurefunds.dto;

import com.cg.futurefunds.model.InvestmentType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class InvestmentResponseDTO {
    private String name;
    private InvestmentType type;
    private double monthlyAmount;
    private double expectedReturn;
    private int durationMonths;
    private double targetAmount;
    private double currentValue;
    private LocalDate startDate;
    private UserResponseDTO user;
}
