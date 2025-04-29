package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.LoginDto;
import com.cg.futurefunds.model.User;

import com.cg.futurefunds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // For encrypted password matching
    public User userLogin(LoginDto loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail());

        if (user == null) {
            throw new Exception("User not found with the provided email");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        if (!user.isVerified()) {
            throw new Exception("User email is not verified yet");
        }

        return user; 
    }
}

