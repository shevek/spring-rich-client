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
package org.springframework.richclient.progress;

import javax.swing.JComponent;

import org.springframework.richclient.test.SpringRichTestCase;

/**
 * TestCase for StatusBarCommandGroup.
 * 
 * @author Peter De Bruycker
 */
public class StatusBarCommandGroupTests extends SpringRichTestCase {
	public void testSetVisible() {
		StatusBarCommandGroup statusBarCommandGroup = new StatusBarCommandGroup();
		JComponent component = statusBarCommandGroup.getControl();

		statusBarCommandGroup.setVisible(false);
		assertFalse(statusBarCommandGroup.isVisible());
		assertFalse(component.isVisible());

		statusBarCommandGroup.setVisible(true);
		assertTrue(statusBarCommandGroup.isVisible());
		assertTrue(component.isVisible());
	}
}
