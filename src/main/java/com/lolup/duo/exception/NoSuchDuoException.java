package com.lolup.duo.exception;

public class NoSuchDuoException extends RuntimeException {

	public NoSuchDuoException() {
		super("존재하지 않는 듀오입니다.");
	}
}
