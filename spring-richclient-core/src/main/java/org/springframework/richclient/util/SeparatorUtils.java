/*
 * Copyright 2002-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.util;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * Utils class for consolidating separators on toolbars, popupmenus and menus.
 * 
 * @author Benoit Xhenseval
 * @author Peter De Bruycker
 */
public class SeparatorUtils {

	private SeparatorUtils() {
		// static utils only
	}

	/**
	 * Consolidates separators in a toolbar:
	 * <ul>
	 * <li>subsequent separators will be collapsed to one separator</li>
	 * <li>if the first visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * <li>if the last visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * </ul>
	 * @param menu the menu (cannot be null)
	 */
	public static void consolidateSeparators(JToolBar toolBar) {
		Assert.notNull(toolBar, "toolBar cannot be null");

		consolidateSeparators(toolBar.getComponents());
	}

	/**
	 * Consolidates separators in a popupmenu:
	 * <ul>
	 * <li>subsequent separators will be collapsed to one separator</li>
	 * <li>if the first visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * <li>if the last visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * </ul>
	 * @param menu the menu (cannot be null)
	 */
	public static void consolidateSeparators(JPopupMenu popupMenu) {
		Assert.notNull(popupMenu, "popupMenu cannot be null");

		consolidateSeparators(popupMenu.getComponents());
	}

	private static void consolidateSeparators(Component[] menuComponents) {
		Assert.notNull(menuComponents, "menuComponents cannot be null");

		Component previousVisibleComponent = null;
		boolean everythingInvisibleSoFar = true;

		for (int i = 0; i < menuComponents.length; i++) {
			Component menuComponent = menuComponents[i];

			// reset all separators
			if (menuComponent instanceof JSeparator) {
				menuComponent.setVisible(true);
			}

			// Separator should be invisible if
			// - previous visible item one is a separator
			// - it is the first one visible item (ie everything invisible
			// before)
			if (menuComponent instanceof JSeparator && everythingInvisibleSoFar) {
				menuComponent.setVisible(false);
			}
			else if (menuComponent instanceof JSeparator && previousVisibleComponent instanceof JSeparator) {
				previousVisibleComponent.setVisible(false);
			}

			if (menuComponent instanceof JSeparator) {
				previousVisibleComponent = menuComponent;
			}
			else if (menuComponent.isVisible()) {
				everythingInvisibleSoFar = false;
				previousVisibleComponent = menuComponent;
			}

			if (menuComponent instanceof JMenu) {
				consolidateSeparators((JMenu) menuComponent);
			}
		}

		// and if the last item on the menu is a separator -> make it invisible.
		if (previousVisibleComponent instanceof JSeparator) {
			previousVisibleComponent.setVisible(false);
		}
	}

	/**
	 * Consolidates separators in a menu:
	 * <ul>
	 * <li>subsequent separators will be collapsed to one separator</li>
	 * <li>if the first visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * <li>if the last visible item of a menu is a separator, it will be made
	 * invisible</li>
	 * </ul>
	 * @param menu the menu (cannot be null)
	 */
	public static void consolidateSeparators(JMenu menu) {
		Assert.notNull(menu, "menu cannot be null");

		Component previousVisibleComponent = null;
		boolean everythingInvisibleSoFar = true;

		for (int j = 0; j < menu.getMenuComponentCount(); j++) {
			Component menuComponent = menu.getMenuComponent(j);

			// reset all separators
			if (menuComponent instanceof JSeparator) {
				menuComponent.setVisible(true);
			}

			// Separator should be invisible if
			// - previous visible item one is a separator
			// - it is the first one visible item (ie everything invisible
			// before)
			if (menuComponent instanceof JSeparator && everythingInvisibleSoFar) {
				menuComponent.setVisible(false);
			}
			else if (menuComponent instanceof JSeparator && previousVisibleComponent instanceof JSeparator) {
				previousVisibleComponent.setVisible(false);
			}

			if (menuComponent instanceof JSeparator) {
				previousVisibleComponent = menuComponent;
			}
			else if (menuComponent.isVisible()) {
				everythingInvisibleSoFar = false;
				previousVisibleComponent = menuComponent;
			}

			if (menuComponent instanceof JMenu) {
				consolidateSeparators((JMenu) menuComponent);
			}
		}

		// and if the last item on the menu is a separator -> make it invisible.
		if (previousVisibleComponent instanceof JSeparator) {
			previousVisibleComponent.setVisible(false);
		}
	}

	/**
	 * Consolidates separators in a menubar. This essentialy calls
	 * {@link #consolidateSeparators(JMenu)} for each menu in the menubar.
	 * @param menuBar the menu bar (cannot be null)
	 * @see #consolidateSeparators(JMenu)
	 */
	public static void consolidateSeparators(JMenuBar menuBar) {
		Assert.notNull(menuBar, "menu bar cannot be null");

		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			consolidateSeparators(menuBar.getMenu(i));
		}
	}
}
