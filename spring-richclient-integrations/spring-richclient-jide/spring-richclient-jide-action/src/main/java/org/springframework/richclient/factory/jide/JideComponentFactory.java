package org.springframework.richclient.factory.jide;

import com.jidesoft.action.CommandBar;
import org.springframework.richclient.factory.DefaultComponentFactory;

import javax.swing.*;

/**
 * Extension of the default component factory to use a JIDE command bar
 * as the tool bar component.
 * 
 * @author Jonny Wray
 *
 */
public class JideComponentFactory extends DefaultComponentFactory
{

	private boolean borderPainted = true;
	private boolean paintBackground = true;
	
	public JComponent createToolBar() {
		CommandBar commandBar = new CommandBar(CommandBar.HORIZONTAL);
		commandBar.setBorderPainted(borderPainted);
		commandBar.setPaintBackground(paintBackground);
		return commandBar;
	}

}
