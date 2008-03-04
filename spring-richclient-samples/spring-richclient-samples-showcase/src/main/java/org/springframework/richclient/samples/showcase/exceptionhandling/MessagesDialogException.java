package org.springframework.richclient.samples.showcase.exceptionhandling;

import org.springframework.richclient.exceptionhandling.MessagesDialogExceptionHandler;

/**
 * Simple exception to use with the {@link MessagesDialogExceptionHandler}.
 *
 * @author Jan Hoskens
 *
 */
public class MessagesDialogException extends RuntimeException {

	public MessagesDialogException() {
		this("Some message");
	}

	public MessagesDialogException(String message) {
		this(message, new UnsupportedOperationException("Something is not supported."));
	}

	public MessagesDialogException(String message, Throwable cause) {
		super(message, cause);
	}
}