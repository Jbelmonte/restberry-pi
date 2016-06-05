package org.company.core.jaxrs.exceptions;

/**
 * Exception thrown when response content type negotiation is not satisfied.
 */
public class NotAcceptableException extends ResourceException {

	private static final long serialVersionUID = 4697534520351065564L;

	public NotAcceptableException() {
	}

	public NotAcceptableException(String message) {
		super(message);
	}

	public NotAcceptableException(Throwable cause) {
		super(cause);
	}

	public NotAcceptableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAcceptableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
