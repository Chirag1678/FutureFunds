package com.cg.futurefunds.dto;

import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.InvestmentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentResponseDTO {
    private String name;
    private InvestmentType type;
    private double monthlyAmount;
    private double expectedReturn;
    private int durationMonths;
    private double targetAmount;
    private double currentValue;
    private LocalDate startDate;
    private String goal;
    private UserResponseDTO user;
}
