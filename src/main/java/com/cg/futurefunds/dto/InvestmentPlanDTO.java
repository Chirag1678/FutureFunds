package com.cg.futurefunds.dto;

import com.cg.futurefunds.model.InvestmentType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvestmentPlanDTO {
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "Investment name is required")
    @Size(min = 4, max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Investment type is required")
    private InvestmentType type;

    @Positive(message = "Monthly amount must be greater than 0")
    private double monthlyAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Expected return must be positive")
    @DecimalMax(value = "100.0", message = "Expected return must be less than or equal to 100")
    private double expectedReturn;

    @Positive(message = "Duration must be greater than 0")
    private int durationMonths;

    @Size(min = 4, max = 100, message = "Goal must not exceed 100 characters")
    private String goal;
}
