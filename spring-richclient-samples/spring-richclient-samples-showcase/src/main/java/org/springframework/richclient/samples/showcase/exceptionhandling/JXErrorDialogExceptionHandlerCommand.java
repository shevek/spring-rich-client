package org.springframework.richclient.samples.showcase.exceptionhandling;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.exceptionhandling.JXErrorDialogExceptionHandler;

/**
 * Command throwing the specific {@link JXErrorDialogException} that should be
 * caught by the {@link JXErrorDialogExceptionHandler}.
 *
 * @author Jan Hoskens
 *
 */
public class JXErrorDialogExceptionHandlerCommand extends ActionCommand {

	@Override
	protected void doExecuteCommand() {
		throw new JXErrorDialogException();
	}

}
