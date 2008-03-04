package org.springframework.richclient.samples.showcase.exceptionhandling;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.exceptionhandling.MessagesDialogExceptionHandler;

/**
 * Command throwing the specific {@link MessagesDialogException} that should be
 * caught by the {@link MessagesDialogExceptionHandler}.
 *
 * @author Jan Hoskens
 *
 */
public class MessagesDialogExceptionHandlerCommand extends ActionCommand {

	@Override
	protected void doExecuteCommand() {
		throw new MessagesDialogException();
	}

}