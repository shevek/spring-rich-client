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

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;

/**
 * Helper class for saving and restoring JFrame and JDialog settings.
 * 
 * @author Peter De Bruycker
 */
public class WindowSettings {

    private WindowSettings() {
        // no instances
    }

    private static void assertParameters(Settings s, String key, Window window) {
        Assert.notNull(s, "Settings cannot be null");
        Assert.hasText(key, "Key cannot be empty or null");
        Assert.notNull(window, "Window cannot be null");
    }

    public static void saveLocation(Settings s, String key, Window window) {
        assertParameters(s, key, window);

        s.setInt(key + ".x", window.getX());
        s.setInt(key + ".y", window.getY());
    }

    public static void saveState(Settings s, JFrame frame) {
        Assert.hasText(frame.getName(), "Frame must have it's name attribute filled in");

        saveState(s, frame.getName(), frame);
    }

    public static void saveState(Settings s, JDialog dialog) {
        Assert.hasText(dialog.getName(), "Dialog must have it's name attribute filled in");

        saveState(s, dialog.getName(), dialog);
    }

    public static void restoreState(Settings s, JFrame frame) {
        Assert.hasText(frame.getName(), "Frame must have it's name attribute filled in");

        restoreState(s, frame.getName(), frame);
    }

    public static void restoreState(Settings s, JDialog dialog) {
        Assert.hasText(dialog.getName(), "Dialog must have it's name attribute filled in");

        restoreState(s, dialog.getName(), dialog);
    }

    public static void saveState(Settings s, String key, JFrame frame) {
        assertParameters(s, key, frame);

        saveLocation(s, key, frame);
        saveSize(s, key, frame);
        saveMaximizedState(s, key, frame);
    }

    public static void saveState(Settings s, String key, JDialog dialog) {
        assertParameters(s, key, dialog);

        saveLocation(s, key, dialog);
        saveSize(s, key, dialog);
    }

    public static void saveMaximizedState(Settings s, String key, JFrame frame) {
        assertParameters(s, key, frame);

        s.setBoolean(key + ".maximized", frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
    }

    public static void saveSize(Settings s, String key, Window window) {
        assertParameters(s, key, window);

        s.setInt(key + ".height", window.getHeight());
        s.setInt(key + ".width", window.getWidth());
    }

    public static void restoreState(Settings s, String key, JFrame frame) {
        assertParameters(s, key, frame);

        restoreLocation(s, key, frame);
        restoreSize(s, key, frame);
        restoreMaximizedState(s, key, frame);
    }

    public static void restoreState(Settings s, String key, JDialog dialog) {
        assertParameters(s, key, dialog);

        restoreLocation(s, key, dialog);
        restoreSize(s, key, dialog);
    }

    public static void restoreMaximizedState(Settings s, String key, JFrame frame) {
        assertParameters(s, key, frame);

        frame.setExtendedState((s.getBoolean(key + ".maximized") ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL));
    }

    public static void restoreSize(Settings s, String key, Window window) {
        assertParameters(s, key, window);

        if (s.contains(key + ".height") && s.contains(key + ".width")) {
            window.setSize(s.getInt(key + ".width"), s.getInt(key + ".height"));
        }
    }

    public static void restoreLocation(Settings s, String key, Window window) {
        assertParameters(s, key, window);

        if (s.contains(key + ".x") && s.contains(key + ".y")) {
            window.setLocation(s.getInt(key + ".x"), s.getInt(key + ".y"));
        }
    }
}