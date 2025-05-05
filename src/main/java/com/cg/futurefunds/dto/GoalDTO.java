package com.cg.futurefunds.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class GoalDTO {
    @NotNull(message = "Investment ID cannot be empty")
    private Long investmentId;

    @NotBlank(message = "Goal name cannot be empty")
    @Size(min = 4, message = "Goal name must contain at least 4 characters")
    private String name;
}
