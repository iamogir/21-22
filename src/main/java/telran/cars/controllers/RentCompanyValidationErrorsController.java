package telran.cars.controllers;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;
import static telran.cars.api.RentCompanyErrorMessage.*;

@ControllerAdvice
@Slf4j
public class RentCompanyValidationErrorsController
{
	private ResponseEntity<String> returnResponse(String message, HttpStatus status)
	{
		log.error(message);
		return new ResponseEntity<String>(message, status);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
	{
		String message = e.getAllErrors().stream().map(er -> er.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return returnResponse(message, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	ResponseEntity<String> handlerMethodValidationExceptionHandler(HandlerMethodValidationException e)
	{
		String message = e.getAllErrors().stream().map(er -> er.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return returnResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<String> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e)
	{
		return returnResponse(TYPE_MISMATCH, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<String> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e)
	{
		return returnResponse(JSON_TYPE_MISMATCH, HttpStatus.BAD_REQUEST);
	}
}
