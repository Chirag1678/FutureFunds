package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.LoginDTO;
import com.cg.futurefunds.dto.RegisterDTO;
import com.cg.futurefunds.dto.ResponseDTO;

public interface UserService {
	ResponseDTO userLogin(LoginDTO loginDto);
	ResponseDTO registerUser(RegisterDTO registerDTO);
	ResponseDTO forgotPassword(LoginDTO loginDTO);
	ResponseDTO userVerification(LoginDTO loginDTO);
	ResponseDTO resetPassword(LoginDTO loginDTO);
}
