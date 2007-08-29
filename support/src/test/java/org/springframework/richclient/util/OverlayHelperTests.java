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
package org.springframework.richclient.util;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;

import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author andy
 * @since May 12, 2006 8:32:27 AM
 */
public class OverlayHelperTests extends SpringRichTestCase {

    /**
     * OverlayHelper installs the overlay as the View of a JScrollPane viewport, if the component is in a JScrollPane,
     * so that the overlay is shown in the proper location when scrolled. However, to accomplish this, it will remove
     * the component that was in the viewport, add it to a JLayeredPane, and then add that JLayeredPane to the viewport
     * instead. This introduced a bug if the viewport's view happened to implement the Scrollable interface, since
     * JScrollPane does <i>not</i> implement the Scrollable interface. See issue RCP-344.
     * 
     * @throws Exception
     */
    public void testRegressionScrollableProxy() throws Exception {
        performScrollableTest();
        performNonScrollableTest();
    }

    /**
     * Ensures that OverlayHelper supports the Scrollable interface and properly proxies Scrollable methods.
     * 
     * @throws Exception
     */
    private void performScrollableTest() throws Exception {
        final ScrollablePanel view = new ScrollablePanel(new BorderLayout());
        view.setScrollableUnitIncrement(5);
        view.setScrollableBlockIncrement(30);
        view.setScrollableTracksViewportWidth(true);

        final JComponent overlay = createTestOverlay();
        final JComponent someField = createTestComponent();

        OverlayHelper.attachOverlay(overlay, someField, 0, 0, 0);

        view.add(someField);

        final JScrollPane scrollPane = new JScrollPane(view);

        waitUnitlEventQueueIsEmpty();

        final Component viewportView = scrollPane.getViewport().getView();

        // If OverlayHelper changes the way it handles scrollable overlays,
        // then the test will need to be revisited - this makes sure it
        // won't get ignored. :)
//        assertFalse(viewportView == view);

        assertTrue(viewportView instanceof Scrollable);
        assertTrue(((Scrollable) viewportView).getScrollableTracksViewportWidth());
        assertFalse(((Scrollable) viewportView).getScrollableTracksViewportHeight());
        assertEquals(5, ((Scrollable) viewportView).getScrollableUnitIncrement(null, 0, 0));
        assertEquals(30, ((Scrollable) viewportView).getScrollableBlockIncrement(null, 0, 0));
        assertEquals(view.getPreferredScrollableViewportSize(), ((Scrollable) viewportView)
                .getPreferredScrollableViewportSize());
    }

    private void waitUnitlEventQueueIsEmpty() throws InterruptedException {
        // we have to sleep here until the asynchronously attachement of JLayeredPane and the overlay is finished
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        AWTEvent peekEvent;
        while((peekEvent = eventQueue.peekEvent()) != null) {
            System.out.println("got event in queue: " + peekEvent);
            Thread.currentThread().sleep(0);
        }
    }

    /**
     * Ensures that OverlayHelper will NOT implement the Scrollable interface if the view component does not implement
     * the Scrollable interface.
     * 
     * @throws Exception
     */
    private void performNonScrollableTest() throws Exception {
        final JPanel view = new JPanel(new BorderLayout());
        final JComponent overlay = createTestOverlay();
        final JComponent someField = createTestComponent();

        OverlayHelper.attachOverlay(overlay, someField, 0, 0, 0);

        view.add(someField);

        final JScrollPane scrollPane = new JScrollPane(view);

        waitUnitlEventQueueIsEmpty();

        final Component viewportView = scrollPane.getViewport().getView();
//        assertFalse(viewportView == view);
        assertFalse(viewportView instanceof Scrollable);
    }

    public void testSwapScrollableForNonScrollable() throws Exception {
        JComponent view = new ScrollablePanel(new BorderLayout());

        final JComponent overlay = createTestOverlay();
        final JComponent someField = createTestComponent();

        OverlayHelper.attachOverlay(overlay, someField, 0, 0, 0);

        view.add(someField);

        final JScrollPane scrollPane = new JScrollPane(view);

        waitUnitlEventQueueIsEmpty();

        Component viewportView = scrollPane.getViewport().getView();
//        assertFalse(viewportView == view);
        assertTrue(viewportView instanceof Scrollable);

        view.remove(someField);
        view = new JPanel(new BorderLayout());
        view.add(someField);
        scrollPane.setViewportView(view);

        waitUnitlEventQueueIsEmpty();

        viewportView = scrollPane.getViewport().getView();
//        assertFalse(viewportView == view);
        assertFalse(viewportView instanceof Scrollable);

        view.remove(someField);
        view = new ScrollablePanel(new BorderLayout());
        view.add(someField);
        scrollPane.setViewportView(view);

        waitUnitlEventQueueIsEmpty();

        viewportView = scrollPane.getViewport().getView();
//        assertFalse(viewportView == view);
        assertTrue(viewportView instanceof Scrollable);
    }

    private JComponent createTestComponent() {
        return new JTextField("Hello, world!") {
            // This is to force the OverlayHelper to install the overlay,
            // even though we don't have a UI visible.
            public boolean isVisible() {
                return true;
            }

            public boolean isShowing() {
                return true;
            }
        };
    }

    private JComponent createTestOverlay() {
        final JComponent overlay = new JLabel("x") {
            // This is to force the OverlayHelper to install the overlay,
            // even though we don't have a UI visible.
            public boolean isVisible() {
                return true;
            }

            public boolean isShowing() {
                return true;
            }
        };
        overlay.setOpaque(false);
        return overlay;
    }

    public static class ScrollablePanel extends JPanel implements Scrollable {
        private int scrollableUnitIncrement = 10;

        private int scrollableBlockIncrement = 40;

        private boolean scrollableTracksViewportWidth = false;

        private boolean scrollableTracksViewportHeight = false;

        public ScrollablePanel(LayoutManager layout, boolean isDoubleBuffered) {
            super(layout, isDoubleBuffered);
        }

        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        public ScrollablePanel(boolean isDoubleBuffered) {
            super(isDoubleBuffered);
        }

        public ScrollablePanel() {
        }

        public void setScrollableUnitIncrement(final int scrollableUnitIncrement) {
            this.scrollableUnitIncrement = scrollableUnitIncrement;
        }

        public void setScrollableBlockIncrement(final int scrollableBlockIncrement) {
            this.scrollableBlockIncrement = scrollableBlockIncrement;
        }

        public void setScrollableTracksViewportWidth(final boolean scrollableTracksViewportWidth) {
            this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
        }

        public void setScrollableTracksViewportHeight(final boolean scrollableTracksViewportHeight) {
            this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return this.scrollableUnitIncrement;
        }

        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return this.scrollableBlockIncrement;
        }

        public boolean getScrollableTracksViewportWidth() {
            return this.scrollableTracksViewportWidth;
        }

        public boolean getScrollableTracksViewportHeight() {
            return this.scrollableTracksViewportHeight;
        }
    }
}
