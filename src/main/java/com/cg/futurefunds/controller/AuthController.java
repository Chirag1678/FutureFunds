package com.cg.futurefunds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.futurefunds.dto.LoginDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/verify")
	public ResponseEntity<ResponseDTO> userVerification(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = authService.userVerification(loginDTO);
		
		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> userLogin(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = authService.userLogin(loginDTO);
		
		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}
}
