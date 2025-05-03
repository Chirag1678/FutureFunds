package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.RegisterDTO;
import com.cg.futurefunds.dto.UpdateUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cg.futurefunds.dto.LoginDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
		ResponseDTO responseDTO = userService.registerUser(registerDTO);
		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> userLogin(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = userService.userLogin(loginDTO);

		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}

	@PostMapping("/forgot")
	public ResponseEntity<ResponseDTO> forgotPassword(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = userService.forgotPassword(loginDTO);

		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}

	@PostMapping("/reset")
	public ResponseEntity<ResponseDTO> resetPassword(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = userService.resetPassword(loginDTO);

		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}

	@PostMapping("/change")
	public ResponseEntity<ResponseDTO> changePassword(@Valid @RequestBody LoginDTO loginDTO) {
		ResponseDTO responseDTO = userService.changePassword(loginDTO);

		return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}

	@PutMapping("/update")
	public ResponseEntity<ResponseDTO> updateUserDetails(@Valid @RequestBody UpdateUserDTO updateUserDTO){
		ResponseDTO responseDTO=userService.updateUserDetails(updateUserDTO);

		return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
	}
}
