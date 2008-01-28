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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Skeleton test for ApplicationDialog
 * 
 * @author Peter De Bruycker
 */
public abstract class ApplicationDialogTestCase extends SpringRichTestCase {
	private ApplicationDialog applicationDialog;

	private OnAboutToShow onAboutToShow = new OnAboutToShow();

	public void testSetAndGetTitle() {
		applicationDialog.setTitle("new title");

		assertEquals("new title", applicationDialog.getTitle());
		assertEquals("new title", applicationDialog.getDialog().getTitle());

		applicationDialog.setTitle("other title");

		assertEquals("other title", applicationDialog.getTitle());
		assertEquals("other title", applicationDialog.getDialog().getTitle());
	}

	public void testOnAboutToShowIsCalled() {
		applicationDialog.getDialog().addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						applicationDialog.getDialog().dispose();
					}
				});
			}
		});
		applicationDialog.showDialog();

		assertTrue(onAboutToShow.wasRun());
	}

	protected final void doSetUp() throws Exception {
		applicationDialog = createApplicationDialog(onAboutToShow);

		assertNotNull(applicationDialog);

		doMoreSetUp();
	}

	protected void doMoreSetUp() throws Exception {

	}

	protected abstract ApplicationDialog createApplicationDialog(Runnable onAboutToShow);

	private static class OnAboutToShow implements Runnable {
		public ValueHolder booleanHolder = new ValueHolder();

		public void run() {
			booleanHolder.setValue(Boolean.TRUE);
		}

		public boolean wasRun() {
			return Boolean.TRUE.equals(booleanHolder.getValue());
		}
	}
}
