package org.restberrypi.core.jaxrs.exceptions;

/**
 * Exception thrown when request content type negotiation is not satisfied.
 */
public class UnsupportedMediaTypeException extends ResourceException {

	private static final long serialVersionUID = 4697534520351065564L;

	public UnsupportedMediaTypeException() {
	}

	public UnsupportedMediaTypeException(String message) {
		super(message);
	}

	public UnsupportedMediaTypeException(Throwable cause) {
		super(cause);
	}

	public UnsupportedMediaTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedMediaTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
