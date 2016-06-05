package org.company.core.jaxrs.exceptions;

/**
 * Exception thrown when logged user has not required roles to perform the
 * request.
 */
public class ForbiddenException extends ResourceException {

	private static final long serialVersionUID = 4697534520351065564L;

	public ForbiddenException() {
	}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
