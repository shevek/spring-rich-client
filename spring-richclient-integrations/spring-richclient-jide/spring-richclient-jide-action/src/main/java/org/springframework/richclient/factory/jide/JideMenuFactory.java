package org.springframework.richclient.factory.jide;

import com.jidesoft.action.CommandMenuBar;
import com.jidesoft.swing.JideMenu;
import org.springframework.richclient.factory.DefaultMenuFactory;

import javax.swing.*;

/**
 * Extension of the default menu factory to use JideMenu and
 * JIDE CommandMenuBar. Provides basic integration with the
 * JIDE Action framework.
 * 
 * @author Jonny Wray
 *
 */
public class JideMenuFactory extends DefaultMenuFactory
{

	private int preferredPopupHorizontalAlignment = JideMenu.LEFT;
	
	public void setPreferredPopupHorizontalAlignment(int alignment){
		this.preferredPopupHorizontalAlignment = alignment;
	}
	
	public JMenu createMenu() {
		JideMenu menu = new JideMenu();
		menu.setPreferredPopupHorizontalAlignment(preferredPopupHorizontalAlignment);
		return menu;
	}

	public JMenuBar createMenuBar() {
		CommandMenuBar menuBar = new CommandMenuBar();
		menuBar.setStretch(true);
		return menuBar;
	}

}
