package com.ehpessoa.http;

/**
 * 
 * @author Everaldo Pessoa
 *
 */
public class HTTPClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public HTTPClientException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public HTTPClientException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public HTTPClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param cause
	 */
	public HTTPClientException(Throwable cause) {
		super(cause);
	}

}
