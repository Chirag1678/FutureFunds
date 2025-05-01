package com.cg.futurefunds.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RegisterDTO {
	@NotBlank(message = "Full Name Cannot be empty")
	@Size(min=4,message = "Full Name must contain at least four characters")
	@Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$", message = "Name must be in format: Firstname Lastname")
	private String fullName;
	
	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password cannot be Empty")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	@Pattern(regexp =  "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$",message = "Password must include at least one uppercase letter and one digit")
	private String password;
	
	
	

}
