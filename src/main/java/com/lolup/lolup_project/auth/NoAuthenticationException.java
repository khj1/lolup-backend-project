package com.lolup.lolup_project.auth;

public class NoAuthenticationException extends RuntimeException {

	public NoAuthenticationException() {
		super("권한 인증에 실패했습니다.");
	}
}
