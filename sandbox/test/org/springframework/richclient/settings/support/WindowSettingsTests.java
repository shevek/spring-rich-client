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

import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.framework.TestCase;

import org.springframework.richclient.settings.TransientSettings;

/**
 * @author Peter De Bruycker
 */
public class WindowSettingsTests extends TestCase {

    private TransientSettings settings;

    private JDialog dialog;

    private JFrame frame;

    public void testSaveLocation() {
        // frame
        frame.setLocation(100, 99);
        WindowSettings.saveLocation(settings, "frame", frame);

        assertEquals(100, settings.getInt("frame.x"));
        assertEquals(99, settings.getInt("frame.y"));

        // dialog
        dialog.setLocation(20, 15);
        WindowSettings.saveLocation(settings, "dialog", dialog);

        assertEquals(20, settings.getInt("dialog.x"));
        assertEquals(15, settings.getInt("dialog.y"));
    }

    public void testRestoreLocation() {
        // frame
        frame.setLocation(100, 99);
        settings.setInt("frame.x", 15);
        settings.setInt("frame.y", 30);
        WindowSettings.restoreLocation(settings, "frame", frame);

        assertEquals(15, frame.getX());
        assertEquals(30, frame.getY());

        // dialog
        dialog.setLocation(20, 15);
        settings.setInt("dialog.x", 100);
        settings.setInt("dialog.y", 115);
        WindowSettings.restoreLocation(settings, "dialog", dialog);

        assertEquals(100, dialog.getX());
        assertEquals(115, dialog.getY());
    }

    public void testRestoreLocationNotInSettings() {
        // frame
        frame.setLocation(100, 99);
        assertFalse(settings.contains("frame.x"));
        assertFalse(settings.contains("frame.y"));
        WindowSettings.restoreLocation(settings, "frame", frame);

        assertEquals(100, frame.getX());
        assertEquals(99, frame.getY());

        // dialog
        dialog.setLocation(20, 15);
        assertFalse(settings.contains("dialog.x"));
        assertFalse(settings.contains("dialog.y"));
        WindowSettings.restoreLocation(settings, "dialog", dialog);

        assertEquals(20, dialog.getX());
        assertEquals(15, dialog.getY());
    }

    public void testSaveSize() {
        // frame
        frame.setSize(800, 600);
        WindowSettings.saveSize(settings, "frame", frame);

        assertEquals(800, settings.getInt("frame.width"));
        assertEquals(600, settings.getInt("frame.height"));

        // dialog
        dialog.setSize(150, 100);
        WindowSettings.saveSize(settings, "dialog", dialog);

        assertEquals(150, settings.getInt("dialog.width"));
        assertEquals(100, settings.getInt("dialog.height"));
    }

    public void testSaveMaximizedState() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        WindowSettings.saveMaximizedState(settings, "frame", frame);

        assertTrue(settings.getBoolean("frame.maximized"));

        frame.setExtendedState(JFrame.NORMAL);
        WindowSettings.saveMaximizedState(settings, "frame", frame);
        assertFalse(settings.getBoolean("frame.maximized"));
    }

    public void testRestoreMaximizedState() {
        frame.setExtendedState(JFrame.NORMAL);
        settings.setBoolean("frame.maximized", true);
        WindowSettings.restoreMaximizedState(settings, "frame", frame);

        assertEquals(JFrame.MAXIMIZED_BOTH, frame.getExtendedState());

        settings.setBoolean("frame.maximized", false);
        WindowSettings.restoreMaximizedState(settings, "frame", frame);
        assertEquals(JFrame.NORMAL, frame.getExtendedState());
    }

    public void testRestoreSize() {
        // frame
        frame.setSize(800, 600);
        settings.setInt("frame.width", 1024);
        settings.setInt("frame.height", 768);
        WindowSettings.restoreSize(settings, "frame", frame);

        assertEquals(1024, frame.getWidth());
        assertEquals(768, frame.getHeight());

        // dialog
        dialog.setSize(150, 100);
        settings.setInt("dialog.width", 200);
        settings.setInt("dialog.height", 150);
        WindowSettings.restoreSize(settings, "dialog", dialog);

        assertEquals(200, dialog.getWidth());
        assertEquals(150, dialog.getHeight());
    }

    public void testRestoreSizeNotInSettings() {
        // frame
        frame.setSize(800, 600);
        assertFalse(settings.contains("frame.width"));
        assertFalse(settings.contains("frame.height"));
        WindowSettings.restoreSize(settings, "frame", frame);

        assertEquals(800, frame.getWidth());
        assertEquals(600, frame.getHeight());

        // dialog
        dialog.setSize(150, 100);
        assertFalse(settings.contains("dialog.width"));
        assertFalse(settings.contains("dialog.height"));
        WindowSettings.restoreSize(settings, "dialog", dialog);

        assertEquals(150, dialog.getWidth());
        assertEquals(100, dialog.getHeight());
    }

    protected void setUp() throws Exception {
        frame = new JFrame("test frame");

        dialog = new JDialog(frame, "test dialog");

        settings = new TransientSettings();
    }

}