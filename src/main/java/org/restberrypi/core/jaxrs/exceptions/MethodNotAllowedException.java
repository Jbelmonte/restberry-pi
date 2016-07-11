package org.restberrypi.core.jaxrs.exceptions;

/**
 * Exception thrown when the request method specified is not allowed for a given
 * resource path.
 */
public class MethodNotAllowedException extends ResourceException {

	private static final long serialVersionUID = 2108499963285662188L;

	public MethodNotAllowedException() {
	}

	public MethodNotAllowedException(String message) {
		super(message);
	}

	public MethodNotAllowedException(Throwable cause) {
		super(cause);
	}

	public MethodNotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodNotAllowedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
