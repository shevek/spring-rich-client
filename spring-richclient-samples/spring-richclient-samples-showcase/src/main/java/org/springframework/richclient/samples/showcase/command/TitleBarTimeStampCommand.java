package org.springframework.richclient.samples.showcase.command;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

/**
 * Simple command testing the {@link ApplicationWindowAwareCommand} class. If
 * everything goes as expected, a timestamp will be set on the correct window.
 *
 * @author Jan Hoskens
 *
 */
public class TitleBarTimeStampCommand extends ApplicationWindowAwareCommand {

	private static final String TIMESTAMP_PREFIX = " [time: ";

	private static final String TIMESTAMP_POSTFIX = "]";

	private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, Locale.getDefault());

	@Override
	protected void doExecuteCommand() {
		Calendar currentTime = Calendar.getInstance();
		String title = getApplicationWindow().getControl().getTitle();
		int pos = title.indexOf(TIMESTAMP_PREFIX);
		if (pos != -1)
			title = title.substring(0, pos);
		title += TIMESTAMP_PREFIX + format.format(currentTime.getTime()) + TIMESTAMP_POSTFIX;
		getApplicationWindow().getControl().setTitle(title);
	}
}