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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * A helper class that attaches one component (the overlay) on top of another
 * component.
 * 
 * @author oliverh
 */
public class OverlayHelper implements SwingConstants {
    private static final String LAYERED_PANE_PROPERTY = "overlayLayeredPane";

    private final OverlayTargetChangeHandler overlayTargetChangeHandler = new OverlayTargetChangeHandler();

    private final OverlayChangeHandler overlayChangeHandler = new OverlayChangeHandler();

    protected final JComponent overlay;

    protected final JComponent overlayTarget;

    private final int center;

    private final int xOffset;

    private final int yOffset;

    private boolean isUpdating;

    /**
     * Attaches an overlay to the specified component.
     * 
     * @param overlay
     *            the overlay component
     * @param overlayTarget
     *            the component over which <code>overlay</code> will be
     *            attached
     * @param center
     *            position relative to <code>overlayTarget</code> that overlay
     *            should be centered. May be one of the
     *            <code>SwingConstants</code> compass positions or
     *            <code>SwingConstants.CENTER</code>. 
     * @param xOffset
     *            x offset from center 
     * @param yOffset
     *            y offset from center
     * 
     * @see SwingConstants
     */
    public static void attachOverlay(JComponent overlay, JComponent overlayTarget, int center, int xOffset, int yOffset) {
        new OverlayHelper(overlay, overlayTarget, center, xOffset, yOffset);
    }

    protected OverlayHelper(JComponent overlay, JComponent overlayTarget, int center, int xOffset, int yOffset) {
        this.overlay = overlay;
        this.overlayTarget = overlayTarget;
        this.center = center;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        installListeners();
    }

    private final class OverlayChangeHandler implements ComponentListener, PropertyChangeListener {
        public void componentHidden(ComponentEvent e) {
            hideOverlay();
        }

        public void componentMoved(ComponentEvent e) {
            // ignore
        }

        public void componentResized(ComponentEvent e) {
            // ignore
        }

        public void componentShown(ComponentEvent e) {
            updateOverlay();
        }

        public void propertyChange(PropertyChangeEvent e) {
            if ("ancestor".equals(e.getPropertyName()) || "layeredContainerLayer".equals(e.getPropertyName())) {
                return;
            }
            updateOverlay();
        }
    }

    private class OverlayTargetChangeHandler implements HierarchyListener, HierarchyBoundsListener, ComponentListener {
        public void hierarchyChanged(HierarchyEvent e) {
            updateOverlay();
        }

        public void ancestorMoved(HierarchyEvent e) {
            updateOverlay();
        }

        public void ancestorResized(HierarchyEvent e) {
            updateOverlay();
        }

        public void componentHidden(ComponentEvent e) {
            hideOverlay();
        }

        public void componentMoved(ComponentEvent e) {
            updateOverlay();
        }

        public void componentResized(ComponentEvent e) {
            updateOverlay();
        }

        public void componentShown(ComponentEvent e) {
            updateOverlay();
        }
    }

    private void installListeners() {
        overlayTarget.addHierarchyListener(overlayTargetChangeHandler);
        overlayTarget.addHierarchyBoundsListener(overlayTargetChangeHandler);
        overlayTarget.addComponentListener(overlayTargetChangeHandler);
        overlay.addComponentListener(overlayChangeHandler);
        overlay.addPropertyChangeListener(overlayChangeHandler);
    }

    private void updateOverlay() {
        if (isUpdating) {
            return;
        }
        try {
            isUpdating = true;
            Container overlayCapableParent = getOverlayCapableParent(overlayTarget);
            if (overlayCapableParent != null) {
                JLayeredPane layeredPane = getLayeredPane(overlayCapableParent);
                if (overlay.getParent() == null || overlay.getParent() != layeredPane) {
                    putOverlay(layeredPane);
                }
            }
            if (overlayCapableParent == null || !overlayTarget.isShowing() || !overlay.isVisible()) {
                hideOverlay();
            } else {
                positionOverlay(overlayCapableParent);
            }
        }
        finally {
            isUpdating = false;
        }
    }

    private void putOverlay(final JLayeredPane layeredPane) {
        if (overlay.getParent() != layeredPane) {
            JComponent parent = (JComponent)overlay.getParent();
            if (parent != null) {
                parent.remove(overlay);
            }
            layeredPane.add(overlay);
            layeredPane.setLayer(overlay, JLayeredPane.PALETTE_LAYER.intValue());
        }
    }

    private void positionOverlay(Container overlayCapableParent) {
        Point position = determineComponentLocation(overlayTarget, overlayCapableParent);
        Rectangle targetBounds = overlayTarget.getBounds();
        Dimension overlayBounds = overlay.getPreferredSize();
        int tlx = xOffset + position.x - overlayBounds.width / 2;
        int tly = yOffset + position.y - overlayBounds.height / 2;;
        switch (center) {
        case SwingConstants.NORTH:
        case SwingConstants.NORTH_WEST:
        case SwingConstants.NORTH_EAST:
            break;
        case SwingConstants.CENTER:
        case SwingConstants.EAST:
        case SwingConstants.WEST:
            tly += targetBounds.height / 2;
            break;
        case SwingConstants.SOUTH:
        case SwingConstants.SOUTH_EAST:
        case SwingConstants.SOUTH_WEST:
            tly += targetBounds.height;
            break;
        default:
            throw new IllegalArgumentException("Unknown value for center [" + center + "]");
        }
        switch (center) {
        case SwingConstants.WEST:
        case SwingConstants.NORTH_WEST:
        case SwingConstants.SOUTH_WEST:
            break;
        case SwingConstants.CENTER:
        case SwingConstants.NORTH:
        case SwingConstants.SOUTH:
            tlx += targetBounds.width / 2;
            break;
        case SwingConstants.EAST:
        case SwingConstants.NORTH_EAST:
        case SwingConstants.SOUTH_EAST:
            tlx += targetBounds.width;
            break;
        default:
            throw new IllegalArgumentException("Unknown value for center [" + center + "]");
        }
        Dimension size = overlay.getPreferredSize();
        Rectangle newBound = new Rectangle(tlx, tly, size.width, size.height);
        setOverlayBounds(newBound);
    }

    /**
     * Determine the location of a component in the given container.
     * The component must be in the component tree of the container
     * 
     * @param parent
     * @param component
     * @return
     */
    private Point determineComponentLocation(Component component, Container parent) {
        Point location = component.getLocation();
        if(component.getParent() == parent) {
            return location;
        }
        Point parentLocation = determineComponentLocation(component.getParent(), parent);
        location.translate(parentLocation.x, parentLocation.y);
        return location;
    }

    private void setOverlayBounds(Rectangle newBounds) {        
        if (!newBounds.equals(overlay.getBounds())) {
            overlay.setBounds(newBounds);
        }
    }

    private void hideOverlay() {
        setOverlayBounds(new Rectangle(0, 0, 0, 0));
    }

    protected Container getOverlayCapableParent(JComponent component) {
        Container overlayCapableParent = component.getParent();
        while (overlayCapableParent != null && !(overlayCapableParent instanceof JRootPane)
               && !(overlayCapableParent instanceof JViewport)) {
            overlayCapableParent = overlayCapableParent.getParent();
        }
        return overlayCapableParent;
    }

    protected JLayeredPane getLayeredPane(Container overlayCapableParent) {
        if (overlayCapableParent instanceof JRootPane) {
            return ((JRootPane)overlayCapableParent).getLayeredPane();
        }
        else if (overlayCapableParent instanceof JViewport) {
            JViewport viewPort = (JViewport)overlayCapableParent;
            JLayeredPane layeredPane = (JLayeredPane)viewPort.getClientProperty(LAYERED_PANE_PROPERTY);
            Component view = viewPort.getView();
            // check if we already have a layeredPane installed and if it's still available
            if ((layeredPane != null) && (view == layeredPane))
                return layeredPane;

            // no layeredPane or it was removed at some point, so construct a new one
            if(view instanceof Scrollable) {
                layeredPane = new ScrollableProxyingLayeredPane((Scrollable)view);
            } else {
                layeredPane = new JLayeredPane();
            }
            viewPort.putClientProperty(LAYERED_PANE_PROPERTY, layeredPane);
            viewPort.remove(view);
            layeredPane.setLayout(new SingleComponentLayoutManager(view));
            layeredPane.add(view);
            layeredPane.setLayer(view, JLayeredPane.DEFAULT_LAYER.intValue());
            viewPort.setView(layeredPane);
            return layeredPane;
        }
        else {
            throw new IllegalArgumentException("Don't know how to handle parent of type ["
                                               + overlayCapableParent.getClass().getName() + "].");
        }
    }

    public static class SingleComponentLayoutManager implements LayoutManager {
        private Component singleComponent;

        public SingleComponentLayoutManager(Component singleComponent) {
            this.singleComponent = singleComponent;
        }

        public void removeLayoutComponent(Component comp) {
        }

        public void addLayoutComponent(String name, Component comp) {
        }

        public void layoutContainer(Container parent) {
            // Fix 5/12/06 AlD: we don't need to base this on the
            // preferred size of the singleComponent or the extentSize
            // of the viewport because the viewport will have already resized
            // the JLayeredPane and taken everything else into consideration.
            // It will have also honored the Scrollable flags, which is
            // something the original code here did not do.
            singleComponent.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        }

        public Dimension minimumLayoutSize(Container parent) {
            return singleComponent.getMinimumSize();
        }

        public Dimension preferredLayoutSize(Container parent) {
            return singleComponent.getPreferredSize();
        }
    }




    public static class ScrollableProxyingLayeredPane extends JLayeredPane
        implements Scrollable {
        private final Scrollable scrollableDelegate;

        public ScrollableProxyingLayeredPane(final Scrollable delegate) {
            super();
            this.scrollableDelegate = delegate;
        }
        
        
        //
        // METHODS FROM INTERFACE Scrollable
        //

        public Dimension getPreferredScrollableViewportSize() {
            return this.scrollableDelegate.getPreferredScrollableViewportSize();
        }

        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return this.scrollableDelegate.getScrollableUnitIncrement(visibleRect, orientation, direction);
        }

        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return this.scrollableDelegate.getScrollableBlockIncrement(visibleRect, orientation, direction);
        }

        public boolean getScrollableTracksViewportWidth() {
            return this.scrollableDelegate.getScrollableTracksViewportWidth();
        }

        public boolean getScrollableTracksViewportHeight() {
            return this.scrollableDelegate.getScrollableTracksViewportHeight();
        }
    }
}