package org.restberrypi.core.jaxrs.exceptions;

/**
 * Exception thrown when request contains invalid or not enough parameters.
 */
public class InvalidRequestException extends ResourceException {

	private static final long serialVersionUID = -1937513575767283443L;

	public InvalidRequestException() {
	}

	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(Throwable cause) {
		super(cause);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
