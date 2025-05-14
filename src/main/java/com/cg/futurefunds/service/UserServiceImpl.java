package com.cg.futurefunds.service;

import java.time.LocalDateTime;
import java.util.Random;

import com.cg.futurefunds.dto.*;
import com.cg.futurefunds.model.NotificationType;
import jakarta.validation.Valid;
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
	private NotificationService notificationService;

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

		String title = "Welcome to Future Funds";
		String message = "Welcome to Future Funds. Your account has been created successfully. Explore our features and start investing today.\n\n User details:\nName: " + user.getName() + "\nEmail: " + user.getEmail() + "\n\nThank you for using Future Funds. We hope you enjoy our services.";
		ResponseDTO notificationResponse = createNotification(user.getId(), title, message, NotificationType.USER_CREATED);

		if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
			return new ResponseDTO("User registered successfully", HttpStatus.CREATED.value(), userResponseDTO);
		} else {
			throw new FutureFundsException("User registration failed");
		}
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

		String title = "OTP to reset password";
		String message = "Your OTP to reset password is: " + user.getOtp() + ". Please enter this OTP in the reset password form to reset your password.";
		ResponseDTO notificationResponse = createNotification(user.getId(), title, message, NotificationType.USR_PASSWORD_RESET);

		if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
			return new ResponseDTO("Otp sent successfully", HttpStatus.OK.value(), null);
		} else {
			throw new FutureFundsException("Otp sending failed");
		}
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
				return changePassword(loginDTO);
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

		String title = "User details updated";
		String message = "User details updated\n\n New Details:\n name: " + user.getName() + ", email: " + user.getEmail();
		ResponseDTO notificationResponse = createNotification(user.getId(), title, message, NotificationType.USER_UPDATED);

		if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
			return new ResponseDTO("User details updated successfully", HttpStatus.OK.value(), userResponseDTO);
		} else {
			throw new FutureFundsException("User details update failed");
		}
	}

	public UserResponseDTO convertToUserResponseDTO(User user) {
		UserResponseDTO userResponseDTO = new UserResponseDTO();
		userResponseDTO.setName(user.getName());
		userResponseDTO.setEmail(user.getEmail());
		userResponseDTO.setVerified(user.isVerified());
		return userResponseDTO;
	}

	public ResponseDTO createNotification(@Valid Long userId, @Valid String title, @Valid String message, @Valid NotificationType type) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setUserId(userId);
		notificationDTO.setTitle(title);
		notificationDTO.setMessage(message);
		notificationDTO.setType(type);
		String scheduledAt = LocalDateTime.now().toString(); // e.g. "2025-05-02T15:30:00"
		notificationDTO.setScheduledAt(scheduledAt);
		try {
			notificationService.createNotification(notificationDTO);
			return new ResponseDTO("Notification created successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			throw new FutureFundsException("Notification creation failed");
		}
	}
}
