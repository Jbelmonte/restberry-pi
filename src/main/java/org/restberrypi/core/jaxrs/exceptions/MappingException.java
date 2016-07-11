package org.restberrypi.core.jaxrs.exceptions;

/**
 * Exception thrown when incomming data cannot be converted to internal types.
 */
public class MappingException extends ResourceException {

	private static final long serialVersionUID = -3842107528093771097L;

	public MappingException() {
	}

	public MappingException(String message) {
		super(message);
	}

	public MappingException(Throwable cause) {
		super(cause);
	}

	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
