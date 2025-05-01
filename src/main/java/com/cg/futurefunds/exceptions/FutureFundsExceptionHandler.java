package com.cg.futurefunds.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

import com.cg.futurefunds.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class FutureFundsExceptionHandler {
	private static final String message = "Exception while processing REST Request";
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid Date format", e);
        ResponseDTO responseDTO = new ResponseDTO(message, HttpStatus.BAD_REQUEST.value(), "Should have date in the format dd MM yyyy");
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        List<String> errorMessages = errorList.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return new ResponseEntity<>(new ResponseDTO(message, HttpStatus.BAD_REQUEST.value(), errorMessages), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(FutureFundsException.class)
	public ResponseEntity<ResponseDTO> handleFutureFundsException(FutureFundsException e) {
		return new ResponseEntity<>(new ResponseDTO(message, HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
