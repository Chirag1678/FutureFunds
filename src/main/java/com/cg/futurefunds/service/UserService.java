package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.LoginDto;
import com.cg.futurefunds.model.User;

public interface UserService {
	User userLogin(LoginDto loginDto);
}
