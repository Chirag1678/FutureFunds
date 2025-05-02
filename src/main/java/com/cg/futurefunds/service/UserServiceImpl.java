package com.cg.futurefunds.service;

import java.util.Random;

import com.cg.futurefunds.dto.*;
import com.cg.futurefunds.utility.MailSenderUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.UserRepository;
import com.cg.futurefunds.utility.JwtUtility;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtility jwtUtility;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private MailSenderUtility mailSenderUtility;

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
		user.setOtp(null);
		user.setVerified(true);
		userRepository.save(user);

		UserResponseDTO userResponseDTO = new UserResponseDTO();
		userResponseDTO.setName(user.getName());
		userResponseDTO.setEmail(user.getEmail());
		userResponseDTO.setVerified(user.isVerified());

		String to = user.getEmail();
		String subject = "Welcome to Future Funds";
		String body = "Welcome to Future Funds. Your account has been created successfully. Explore our features and start investing today.";
		mailSenderUtility.sendEmail(to, subject, body);

		return new ResponseDTO("User registered successfully", HttpStatus.CREATED.value(), userResponseDTO);

	}

	@Override
	public ResponseDTO userLogin(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("Invalid login credentials, try again."));

        if (!user.isVerified()) {
            throw new FutureFundsException("User email is not verified yet");
        }

        if (!encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new FutureFundsException("Invalid login credentials, try again.");
        }


        String token = jwtUtility.generateToken(loginDTO.getEmail(), user.getId());

        UserResponseDTO userResponseDTO = convertToUserResponseDTO(user);
        userResponseDTO.setToken(token);

        return new ResponseDTO("User logged in successfully", HttpStatus.OK.value(), userResponseDTO);
	}

	@Override
	public ResponseDTO forgotPassword(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("User with email: " + loginDTO.getEmail() + " not exists."));

		user.setOtp(generateOtp());
		user.setVerified(false);

		userRepository.save(user);

		String to = user.getEmail();
		String subject = "OTP to reset password";
		String body = "Your OTP to reset password is: " + user.getOtp() + ". Please enter this OTP in the reset password form to reset your password.";
		mailSenderUtility.sendEmail(to, subject, body);

		return new ResponseDTO("Otp sent successfully", HttpStatus.OK.value(), null);
	}

	@Override
	public ResponseDTO resetPassword(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("User with email: " + loginDTO.getEmail() + " not exists."));

		if(!user.isVerified()) {
			if(user.getOtp().equals(loginDTO.getOtp())) {
				user.setOtp(null);
				user.setVerified(true);
				userRepository.save(user);
				return resetPassword(loginDTO);
			} else {
				throw new FutureFundsException("Invalid otp, try again");
			}
		}

		return new ResponseDTO("You can change password only once", HttpStatus.OK.value(), null);
	}

	@Override
	public ResponseDTO changePassword(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FutureFundsException("User with email: " + loginDTO.getEmail() + " not exists."));

		if(user.isVerified()) {
			user.setPassword(encoder.encode(loginDTO.getPassword()));
			user.setOtp(null);
		} else {
			throw new FutureFundsException("User email is not verified yet");
		}

		userRepository.save(user);

		return new ResponseDTO("Password reset successfully", HttpStatus.OK.value(), null);
	}

	@Override
	public ResponseDTO updateUserDetails(UpdateUserDTO updateUserDTO) {
		User user=userRepository.findByEmail(updateUserDTO.getOldEmail())
				.orElseThrow(()-> new FutureFundsException("User with email : "+updateUserDTO.getOldEmail()+ " does not exist."));

		user.setEmail(updateUserDTO.getEmail());
		user.setName(updateUserDTO.getName());
		userRepository.save(user);

		UserResponseDTO userResponseDTO = convertToUserResponseDTO(user);

		return new ResponseDTO("User updated successfully ", HttpStatus.OK.value(), userResponseDTO);
	}

	public UserResponseDTO convertToUserResponseDTO(User user) {
		UserResponseDTO userResponseDTO = new UserResponseDTO();
		userResponseDTO.setName(user.getName());
		userResponseDTO.setEmail(user.getEmail());
		userResponseDTO.setVerified(user.isVerified());
		return userResponseDTO;
	}
}
