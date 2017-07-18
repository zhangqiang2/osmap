package com.zte.hacker.common.exception;

/**
 * 
 * @author 10180976
 *
 */
public class OpenSourceException extends RuntimeException {
	private static final long serialVersionUID = 6963273347779987989L;

	private int retCode;

	public OpenSourceException(String msg) {
		super(msg);
	}

	public OpenSourceException(int retCode, String msg) {
		super(msg);
		this.retCode = retCode;
	}

	public OpenSourceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public OpenSourceException(int retCode, String msg, Throwable cause) {
		super(msg, cause);
		this.retCode = retCode;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
}
