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
package org.springframework.richclient.core;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.KeyStroke;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;

/**
 * Testcase for LabeledObjectSupport
 * 
 * @author Peter De Bruycker
 */
public class LabeledObjectSupportTests extends TestCase {

	public void testSetTitle() {
		LabeledObjectSupport support = new LabeledObjectSupport();

		PropertyChangeListener mockPropertyChangeListener = (PropertyChangeListener) EasyMock
				.createMock(PropertyChangeListener.class);

		mockPropertyChangeListener.propertyChange(eqPropertyChangeEvent(new PropertyChangeEvent(support,
				LabeledObjectSupport.DISPLAY_NAME_PROPERTY, null, "new title")));
		support.addPropertyChangeListener(LabeledObjectSupport.DISPLAY_NAME_PROPERTY, mockPropertyChangeListener);

		EasyMock.replay(mockPropertyChangeListener);

		support.setTitle("new title");

		EasyMock.verify(mockPropertyChangeListener);
	}

	public void testGetDisplayName() {
		LabeledObjectSupport support = new LabeledObjectSupport();
		assertEquals("displayName", support.getDisplayName());

		support.setTitle("title");
		assertEquals("title", support.getDisplayName());

		support.setLabelInfo(new CommandButtonLabelInfo("label info"));
		assertEquals("title", support.getDisplayName());

		support.setTitle(null);
		assertEquals("label info", support.getDisplayName());
	}

	public void testSetLabelInfo() {
		LabeledObjectSupport support = new LabeledObjectSupport();

		CommandButtonLabelInfo labelInfo = CommandButtonLabelInfo.valueOf("&Save@control S");

		PropertyChangeListener mockPropertyChangeListener = (PropertyChangeListener) EasyMock
				.createMock(PropertyChangeListener.class);

		mockPropertyChangeListener.propertyChange(eqPropertyChangeEvent(new PropertyChangeEvent(support,
				LabeledObjectSupport.DISPLAY_NAME_PROPERTY, null, "Save")));
		mockPropertyChangeListener.propertyChange(eqPropertyChangeEvent(new PropertyChangeEvent(support,
				"mnemonic", Integer.valueOf(0), Integer.valueOf('S'))));
		mockPropertyChangeListener.propertyChange(eqPropertyChangeEvent(new PropertyChangeEvent(support,
				"accelerator", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))));
		support.addPropertyChangeListener(mockPropertyChangeListener);

		EasyMock.replay(mockPropertyChangeListener);
		
		support.setLabelInfo(labelInfo);
		
		EasyMock.verify(mockPropertyChangeListener);
	}

	public static PropertyChangeEvent eqPropertyChangeEvent(PropertyChangeEvent expected) {
		EasyMock.reportMatcher(new PropertyChangeEventArgumentMatcher(expected));

		return null;
	}
}
