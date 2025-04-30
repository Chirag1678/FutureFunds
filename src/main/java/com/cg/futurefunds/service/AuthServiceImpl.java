package com.cg.futurefunds.service;

import java.util.Random;

import com.cg.futurefunds.dto.RegisterDTO;
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

	private  String generateOtp(){
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return  String.valueOf(otp);
	}

	@Override
	public ResponseDTO registerUser(RegisterDTO registerDTO){
		if(userRepository.findByEmail(registerDTO.getEmail()).isPresent()){
			throw new FutureFundsException("Email is already registered");
		}
		User user = new User();
		user.setName(registerDTO.getFullName());
		user.setEmail(registerDTO.getEmail());
		user.setPassword(encoder.encode(registerDTO.getPassword()));
		user.setVerified(false);

		String otp=generateOtp();
		user.setOtp(otp);

		userRepository.save(user);

		return new ResponseDTO("User registered successfuly",HttpStatus.CREATED.value(),null);

	}

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
