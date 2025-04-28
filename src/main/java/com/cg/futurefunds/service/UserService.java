package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.LoginDto;
import com.cg.futurefunds.model.User;

public interface UserSevice {
    User loginUser(LoginDto loginDto);
}
