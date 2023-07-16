package com.lolup.lolup_project.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lolup.lolup_project.auth.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.InvalidTokenException;
import com.lolup.lolup_project.exception.ErrorResult;

@RestControllerAdvice
public class Oauth2ExceptionHandler {

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler({EmptyAuthorizationHeaderException.class, InvalidTokenException.class})
	public ErrorResult emptyAuthorizationHeaderException(Exception e) {
		return new ErrorResult("401", e.getMessage());
	}
}