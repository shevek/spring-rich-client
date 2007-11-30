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
package org.springframework.richclient.command;

import javax.swing.JButton;

import junit.framework.TestCase;

/**
 * Testcase for ActionCommand
 * 
 * @author Peter De Bruycker
 */
public class ActionCommandTests extends TestCase {

	public void testOnButtonAttached() {
		final boolean[] executed = { false };

		ActionCommand command = new ActionCommand() {
			protected void doExecuteCommand() {
				executed[0] = true;
			}
		};
		command.setActionCommand("theActionCommand");

		JButton button = new JButton("test");

		command.onButtonAttached(button);

		assertEquals("theActionCommand", button.getActionCommand());

		button.doClick();
		assertTrue(executed[0]);
	}

	public void testOnButtonAttachedWithDisplayDialog() {
		ActionCommand command = new ActionCommand() {
			protected void doExecuteCommand() {
				// do nothing
			}
		};
		command.setDisplaysInputDialog(true);

		JButton button = new JButton();
		button.setText(null);

		command.onButtonAttached(button);

		assertEquals(null, button.getText());
	}

}
