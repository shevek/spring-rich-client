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
package org.springframework.richclient.dialog;

import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

import org.springframework.richclient.core.DefaultMessage;

/**
 * Testcase for TitlePane
 * 
 * @author Peter De Bruycker
 */
public class TitlePaneTests extends TestCase {

	public void testBlah() {
		TitlePane titlePane = new TitlePane();
		titlePane.setImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB));
		titlePane.setTitle("new title");
		titlePane.setMessage(new DefaultMessage("test message", null));
		assertEquals("new title", titlePane.getTitle());

		// trigger control creation
		JPanel panel = (JPanel) titlePane.getControl();

		assertEquals("must have 3 components: title, icon and message", 3, panel.getComponentCount());

		JLabel titleLabel = (JLabel) panel.getComponent(0);
		assertEquals("new title", titleLabel.getText());

		JLabel iconLabel = (JLabel) panel.getComponent(1);
		assertNotNull(iconLabel.getIcon());

		JLabel messageLabel = (JLabel) panel.getComponent(2);
		assertEquals("<html>test message</html>", messageLabel.getText());
		
		// change title and message after control creation
		titlePane.setTitle("other title");
		titlePane.setMessage(new DefaultMessage("other message", null));

		assertEquals("other title", titleLabel.getText());
		assertEquals("<html>other message</html>", messageLabel.getText());
	}

}
