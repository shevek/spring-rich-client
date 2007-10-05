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

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import junit.framework.TestCase;

/**
 * Testcase for <code>MenuElementUtils</code>
 * 
 * @author Peter De Bruycker
 */
public class SeparatorUtilsTests extends TestCase {
	public void testConsolidateSeparatorsInToolBar() {
		JToolBar toolBar = new JToolBar();

		toolBar.add(createButton("button1", false));
		toolBar.addSeparator(); // index 1
		toolBar.add(createButton("button2", true));
		toolBar.addSeparator(); // index 3
		toolBar.add(createButton("button3", false));
		toolBar.addSeparator(); // index 5
		toolBar.add(createButton("button4",true));
		toolBar.addSeparator(); // index 7
		toolBar.add(createButton("button5", false));

		SeparatorUtils.consolidateSeparators(toolBar);
		
		assertEquals(9, toolBar.getComponentCount());
		
		assertFalse(toolBar.getComponent(1).isVisible());
		assertFalse(toolBar.getComponent(3).isVisible());
		assertTrue(toolBar.getComponent(5).isVisible());
		assertFalse(toolBar.getComponent(7).isVisible());
	}
	
	public void testConsolidateSeparatorsReset() {
		JMenu menu = new JMenu("test-menu");

		menu.add(createMenuItem("item1", false));
		menu.addSeparator(); // index 1
		menu.add(createMenuItem("item2", true));
		menu.addSeparator(); // index 3
		menu.add(createMenuItem("item3", false));
		menu.addSeparator(); // index 5
		menu.add(createMenuItem("item4", true));
		menu.addSeparator(); // index 7
		menu.add(createMenuItem("item5", false));

		SeparatorUtils.consolidateSeparators(menu);

		menu.getMenuComponent(0).setVisible(true);

		SeparatorUtils.consolidateSeparators(menu);

		assertTrue(((JSeparator)menu.getMenuComponent(1)).isVisible());
		assertFalse(((JSeparator)menu.getMenuComponent(3)).isVisible());
		assertTrue(((JSeparator)menu.getMenuComponent(5)).isVisible());
		assertFalse(((JSeparator)menu.getMenuComponent(7)).isVisible());
	}

	public void testConsolidateSeparatorsInMenu() {
		JMenu menu = new JMenu("test-menu");

		menu.add(createMenuItem("item1", false));
		menu.addSeparator(); // index 1
		menu.add(createMenuItem("item2", true));
		menu.addSeparator(); // index 3
		menu.add(createMenuItem("item3", false));
		menu.addSeparator(); // index 5
		menu.add(createMenuItem("item4", true));
		menu.addSeparator(); // index 7
		menu.add(createMenuItem("item5", false));

		SeparatorUtils.consolidateSeparators(menu);

		assertEquals(9, menu.getMenuComponentCount());

		assertFalse(menu.getMenuComponent(1).isVisible());
		assertFalse(menu.getMenuComponent(3).isVisible());
		assertTrue(menu.getMenuComponent(5).isVisible());
		assertFalse(menu.getMenuComponent(7).isVisible());
	}


	public void testConsolidateSeparatorsInSubMenu() {
		JMenu sub = new JMenu("sub-menu");
		sub.add(createMenuItem("sub-item1", true));
		sub.addSeparator();
		sub.add(createMenuItem("sub-item2", false));
		sub.addSeparator();
		sub.add(createMenuItem("sub-item3", true));

		JMenu menu = new JMenu("test-menu");
		menu.add(sub);

		SeparatorUtils.consolidateSeparators(menu);

		assertFalse(sub.getMenuComponent(1).isVisible());
		assertTrue(sub.getMenuComponent(3).isVisible());
	}

	private static JMenuItem createMenuItem(String text, boolean visible) {
		JMenuItem item = new JMenuItem(text);

		item.setVisible(visible);

		return item;
	}

	private static JButton createButton(String text, boolean visible) {
		JButton button = new JButton(text);
		button.setVisible(visible);

		return button;
	}
}
