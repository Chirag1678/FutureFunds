package com.cg.futurefunds.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class UpdateUserDTO {
    @NotBlank(message = "Old Email cannot be empty")
    @Email(message = "Invalid email format")
    private String oldEmail;

    @Size(min=4,message = "Full Name must contain at least four characters")
    @Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$", message = "Name must be in format: Firstname Lastname")
    private String name;

    @Email(message = "Invalid email format")
    private String email;





}

