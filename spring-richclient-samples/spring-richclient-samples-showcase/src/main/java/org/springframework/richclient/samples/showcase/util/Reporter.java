package org.springframework.richclient.samples.showcase.util;

import javax.swing.JTextArea;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.factory.ControlFactory;

/**
 * Interface indicating a reporter object. It needs a messageArea to report to
 * and has some commands that may be called upon.
 *
 * @author Jan Hoskens
 *
 */
public interface Reporter extends ControlFactory{

	void setMessageArea(JTextArea messageArea);

	AbstractCommand[] getReporterCommands();
}
