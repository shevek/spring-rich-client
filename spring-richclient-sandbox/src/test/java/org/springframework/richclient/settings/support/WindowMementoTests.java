/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.settings.support;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.framework.TestCase;

import org.springframework.richclient.settings.TransientSettings;

/**
 * @author Peter De Bruycker
 */
public class WindowMementoTests extends TestCase {
	private JFrame frame;

	private JDialog dialog;

	private TransientSettings settings;

	protected void setUp() throws Exception {
		frame = new JFrame("test frame");
		dialog = new JDialog(frame, "test dialog");
		settings = new TransientSettings();
	}

	public void testConstructor() {
		try {
			new WindowMemento(null);
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// test passes
		}

		try {
			frame.setName(null);
			new WindowMemento(frame, "");
			fail("Should throw IllegalArgumentException: window has no name");
		} catch (Exception e) {
			// test passes
		}

		frame.setName("frame0");

		WindowMemento memento = new WindowMemento(frame);
		assertEquals(frame, memento.getWindow());
		assertEquals("frame0", memento.getKey());
		
		memento = new WindowMemento(frame, "key");
		assertEquals(frame, memento.getWindow());
		assertEquals("key", memento.getKey());
	}

	public void testSaveLocation() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");
		frame.setLocation(100, 99);
		frameMemento.saveLocation(settings);

		assertEquals(100, settings.getInt("frame.x"));
		assertEquals(99, settings.getInt("frame.y"));

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");
		dialog.setLocation(20, 15);
		dialogMemento.saveLocation(settings);

		assertEquals(20, settings.getInt("dialog.x"));
		assertEquals(15, settings.getInt("dialog.y"));
	}

	public void testRestoreLocation() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setLocation(100, 99);
		settings.setInt("frame.x", 15);
		settings.setInt("frame.y", 30);
		frameMemento.restoreLocation(settings);

		assertEquals(15, frame.getX());
		assertEquals(30, frame.getY());

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");

		dialog.setLocation(20, 15);
		settings.setInt("dialog.x", 100);
		settings.setInt("dialog.y", 115);
		dialogMemento.restoreLocation(settings);

		assertEquals(100, dialog.getX());
		assertEquals(115, dialog.getY());
	}

	public void testRestoreLocationNotInSettings() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setLocation(100, 99);
		assertFalse(settings.contains("frame.x"));
		assertFalse(settings.contains("frame.y"));
		frameMemento.restoreLocation(settings);

		assertEquals(100, frame.getX());
		assertEquals(99, frame.getY());

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");

		dialog.setLocation(20, 15);
		assertFalse(settings.contains("dialog.x"));
		assertFalse(settings.contains("dialog.y"));
		dialogMemento.restoreLocation(settings);

		assertEquals(20, dialog.getX());
		assertEquals(15, dialog.getY());
	}

	public void testSaveSize() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setSize(800, 600);
		frameMemento.saveSize(settings);

		assertEquals(800, settings.getInt("frame.width"));
		assertEquals(600, settings.getInt("frame.height"));

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");

		dialog.setSize(150, 100);
		dialogMemento.saveSize(settings);

		assertEquals(150, settings.getInt("dialog.width"));
		assertEquals(100, settings.getInt("dialog.height"));
	}

	public void testSaveMaximizedState() {
		// skip test if platform doesn't support this frame state.
		if (!Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH))
			return;

		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frameMemento.saveMaximizedState(settings);

		assertTrue(settings.getBoolean("frame.maximized"));

		frame.setExtendedState(Frame.NORMAL);
		frameMemento.saveMaximizedState(settings);
		assertFalse(settings.getBoolean("frame.maximized"));
	}

	public void testRestoreMaximizedState() {
		// skip test if platform doesn't support this frame state.
		if (!Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH))
			return;

		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setExtendedState(Frame.NORMAL);
		settings.setBoolean("frame.maximized", true);
		frameMemento.restoreMaximizedState(settings);

		assertEquals(Frame.MAXIMIZED_BOTH, frame.getExtendedState());

		settings.setBoolean("frame.maximized", false);
		frameMemento.restoreMaximizedState(settings);
		assertEquals(Frame.NORMAL, frame.getExtendedState());
	}

	public void testRestoreSize() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setSize(800, 600);
		settings.setInt("frame.width", 1024);
		settings.setInt("frame.height", 768);
		frameMemento.restoreSize(settings);

		assertEquals(1024, frame.getWidth());
		assertEquals(768, frame.getHeight());

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");

		dialog.setSize(150, 100);
		settings.setInt("dialog.width", 200);
		settings.setInt("dialog.height", 150);
		dialogMemento.restoreSize(settings);

		assertEquals(200, dialog.getWidth());
		assertEquals(150, dialog.getHeight());
	}

	public void testRestoreSizeNotInSettings() {
		// frame
		WindowMemento frameMemento = new WindowMemento(frame, "frame");

		frame.setSize(800, 600);
		assertFalse(settings.contains("frame.width"));
		assertFalse(settings.contains("frame.height"));
		frameMemento.restoreSize(settings);

		assertEquals(800, frame.getWidth());
		assertEquals(600, frame.getHeight());

		// dialog
		WindowMemento dialogMemento = new WindowMemento(dialog, "dialog");

		dialog.setSize(150, 100);
		assertFalse(settings.contains("dialog.width"));
		assertFalse(settings.contains("dialog.height"));
		dialogMemento.restoreSize(settings);

		assertEquals(150, dialog.getWidth());
		assertEquals(100, dialog.getHeight());
	}

	protected Component createComponent() {
		return null;
	}

	protected String getKey() {
		return null;
	}
}
