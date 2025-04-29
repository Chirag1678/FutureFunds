package com.cg.futurefunds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserResponseDTO {
	private String name;
	private String email;
	private boolean isVerified;
	private String token;
}
