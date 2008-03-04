package org.springframework.richclient.samples.showcase.exceptionhandling;

import org.springframework.richclient.exceptionhandling.JXErrorDialogExceptionHandler;

/**
 * Simple exception to use with the {@link JXErrorDialogExceptionHandler}.
 *
 * @author Jan Hoskens
 *
 */
public class JXErrorDialogException extends RuntimeException {

	public JXErrorDialogException() {
		this("JXErrorDialogException message");
	}

	public JXErrorDialogException(String message) {
		this(message, new UnsupportedOperationException("JXErrorDialogException cause message."));
	}

	public JXErrorDialogException(String message, Throwable cause) {
		super(message, cause);
	}
}