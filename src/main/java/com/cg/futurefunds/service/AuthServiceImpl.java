package com.cg.futurefunds.service;

import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cg.futurefunds.dto.LoginDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.dto.UserResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.UserRepository;
import com.cg.futurefunds.utility.JwtUtility;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtility jwtUtility;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Override
	public ResponseDTO userVerification(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("User with email: " + loginDTO.getEmail() + " not exists."));
		
		if(!user.isVerified()) {
			if(user.getOtp().equals(loginDTO.getOtp())) {
				user.setOtp(null);
				user.setVerified(true);
			} else {
				throw new FutureFundsException("Invaid otp, try again");
			}
		}
		
		return new ResponseDTO("User verified successfully", HttpStatus.OK.value(), null);
	}
	
	@Override
	public ResponseDTO userLogin(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("Invalid login credentials, try again."));


        if (!encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new FutureFundsException("Invalid login credentials, try again.");
        }

        if (!user.isVerified()) {
            throw new FutureFundsException("User email is not verified yet");
        }

        String token = jwtUtility.generateToken(loginDTO.getEmail(), user.getId());
        
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setVerified(user.isVerified());
        userResponseDTO.setToken(token);
        
        return new ResponseDTO("User logged in successfully", HttpStatus.OK.value(), userResponseDTO);
	}

}
