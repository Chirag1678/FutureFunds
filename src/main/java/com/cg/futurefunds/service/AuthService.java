package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.LoginDTO;
import com.cg.futurefunds.dto.RegisterDTO;
import com.cg.futurefunds.dto.ResponseDTO;

public interface AuthService {
	ResponseDTO userVerification(LoginDTO loginDTO);
	ResponseDTO userLogin(LoginDTO loginDto);
	ResponseDTO registerUser(RegisterDTO registerDTO);
}
