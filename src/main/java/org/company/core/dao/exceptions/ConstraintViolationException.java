package org.company.core.dao.exceptions;

public class ConstraintViolationException extends PersistenceException {
	private static final long serialVersionUID = 5104958055702271916L;

	public ConstraintViolationException() {
	}

	public ConstraintViolationException(String message) {
		super(message);
	}

	public ConstraintViolationException(Throwable cause) {
		super(cause);
	}

	public ConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstraintViolationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
