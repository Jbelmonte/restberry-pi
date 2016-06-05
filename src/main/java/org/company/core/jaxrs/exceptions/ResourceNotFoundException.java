package org.company.core.jaxrs.exceptions;

/**
 * Exception thrown when requesting for an unexisting resource.
 */
public class ResourceNotFoundException extends ResourceException {

	private static final long serialVersionUID = 466510455986297264L;

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
