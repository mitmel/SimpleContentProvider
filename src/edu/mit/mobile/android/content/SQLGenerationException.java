package edu.mit.mobile.android.content;

public class SQLGenerationException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1987806236697877222L;

	public SQLGenerationException() {
		super();
	}

	public SQLGenerationException(String message) {
		super(message);
	}

	public SQLGenerationException(String message, Throwable initCause) {
		super(message);
		initCause(initCause);
	}
}
