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
package org.springframework.richclient.progress;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Support for showing a Busy Cursor during a long running process.
 */
public class BusyIndicator {

    public static class BusyGlassPanel extends JPanel {

        public static final Component INSTANCE = new BusyGlassPanel();

        public static Component instance() {
            return INSTANCE;
        }

        public BusyGlassPanel() {
            super.setOpaque(false);
            super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            super.addKeyListener((new KeyAdapter() {
            }));
            super.addMouseListener((new MouseAdapter() {
            }));
            super.addMouseMotionListener((new MouseMotionAdapter() {
            }));
        }
    }

    /**
     * Runs the given <code>Runnable</code> while providing busy feedback
     * using this busy indicator.
     *
     * @param component the display on which the busy feedback should be displayed. If the
     *                  display is null, the Display for the current thread will be
     *                  used. If there is no Display for the current thread, the
     *                  runnable code will be executed and no busy feedback will be
     *                  displayed.
     * @param runnable  the runnable for which busy feedback is to be shown
     *
     * @see #showWhile
     */
    public static void showWhile(Component component, Runnable runnable) {
        if (component != null) {
            showAt(component);
        }
        try {
            runnable.run();
        }
        catch (RuntimeException x) {
            x.printStackTrace();
            throw x;
        }
        catch (Error x) {
            x.printStackTrace();
            throw x;
        }
        finally {
            if (component != null) {
                clearAt(component);
            }
        }
    }

    public static void showAt(Component component) {
        JRootPane root = SwingUtilities.getRootPane(component);
        if (root != null && root.isShowing()) {
            root.setGlassPane(BusyGlassPanel.INSTANCE);
            root.getGlassPane().setVisible(true);
        }
    }

    public static void clearAt(Component component) {
        JRootPane root = SwingUtilities.getRootPane(component);
        if (root != null && root.isShowing()) {
            root.getGlassPane().setVisible(false);
        }
    }

}

