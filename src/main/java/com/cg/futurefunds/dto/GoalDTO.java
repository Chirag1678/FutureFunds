package com.cg.futurefunds.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class GoalDTO {
    @NotBlank(message = "Investment ID cannot be empty")
    private Long investmentId;

    @NotBlank(message = "Goal name cannot be empty")
    @Size(min = 4, message = "Goal name must contain at least 4 characters")
    private String name;

    @Positive(message = "Target value must be greater than 0")
    private double targetValue;

    @Positive(message = "Progress must be greater than 0")
    private double progress;

    @Future(message = "Target date must be in future")
    private LocalDate targetDate;
}
