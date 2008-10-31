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

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.springframework.richclient.core.DefaultMessage;

/**
 * Testcase for TitledApplicationDialog
 * 
 * @author Peter De Bruycker
 */
public abstract class TitledApplicationDialogTestCase extends ApplicationDialogTestCase {
	private TitledApplicationDialog dialogUnderTest;

	protected abstract TitledApplicationDialog createTitledApplicationDialog(final Runnable onAboutToShow);

	protected final ApplicationDialog createApplicationDialog(final Runnable onAboutToShow) {
		dialogUnderTest = createTitledApplicationDialog(onAboutToShow);

		return dialogUnderTest;
	}

	public void testGetAndSetTitlePaneTitle() {
		dialogUnderTest.setTitlePaneTitle("new title pane text");
		assertEquals("new title pane text", dialogUnderTest.getTitlePaneTitle());

		dialogUnderTest.getDialog();

		dialogUnderTest.setTitlePaneTitle("other title pane text");
		assertEquals("other title pane text", dialogUnderTest.getTitlePaneTitle());
	}

	public void testGetAndSetMessage() {
		dialogUnderTest.setMessage(new DefaultMessage("test message"));
		assertEquals("test message", dialogUnderTest.getMessage().getMessage());

		dialogUnderTest.getDialog();

		dialogUnderTest.setMessage(new DefaultMessage("new message"));
		assertEquals("new message", dialogUnderTest.getMessage().getMessage());
	}

	public void testGetAndSetTitlePaneImage() {
		Image image1 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Image image2 = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);

		dialogUnderTest.setTitlePaneImage(image1);
		assertEquals(image1, dialogUnderTest.getTitlePaneImage());

		dialogUnderTest.getDialog();

		dialogUnderTest.setImage(image2);
		assertEquals(image2, dialogUnderTest.getTitlePaneImage());
	}
}
