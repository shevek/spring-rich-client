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
import javax.swing.SwingConstants;

/**
 * A helper class that attaches one component (the overlay) on top of another
 * component.
 * 
 * @author oliverh
 */
public class OverlayHelper implements SwingConstants {
    private static final String LAYERED_PANE_PROPERTY = "overlayLayeredPane";

    private JComponent overlay;

    private JComponent overlayTarget;

    private int center;

    private int xOffset;

    private int yOffset;

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
     *            <code>SwingConstants</code> compass postions or
     *            <code>SwingConstants.CENTER</center>. 
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

    private void installListeners() {
        overlayTarget.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                updateOverlay();
            }
        });
        overlayTarget.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            public void ancestorMoved(HierarchyEvent e) {
                updateOverlay();
            }

            public void ancestorResized(HierarchyEvent e) {
                updateOverlay();
            }
        });
        overlayTarget.addComponentListener(new ComponentListener() {
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
        });
        overlay.addComponentListener(new ComponentListener() {

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
        });
        overlay.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if ("ancestor".equals(e.getPropertyName()) || "layeredContainerLayer".equals(e.getPropertyName())) {
                    return;
                }
                updateOverlay();
            }
        });
    }

    private void updateOverlay() {
        if (isUpdating) {
            return;
        }
        try {
            isUpdating = true;
            Container overlayCapableParent = getOverlayCapableParent(overlayTarget);
            if (overlayCapableParent == null || !(overlayTarget.isShowing() && overlay.isVisible())) {
                hideOverlay();
            }
            else {
                JLayeredPane layeredPane = getLayeredPane(overlayCapableParent);
                putOverlay(layeredPane);
                positionOverlay(layeredPane);
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

    private void positionOverlay(JLayeredPane layeredPane) {
        Point layOffset = layeredPane.getLocationOnScreen();
        Point targetOffset = overlayTarget.getParent().getLocationOnScreen();
        int centerX = xOffset + (targetOffset.x - layOffset.x);
        int centerY = yOffset + (targetOffset.y - layOffset.y);
        Rectangle overlayTargetBounds = overlayTarget.getBounds();
        switch (center) {
        case SwingConstants.NORTH:
        case SwingConstants.NORTH_WEST:
        case SwingConstants.NORTH_EAST:
            centerY += overlayTargetBounds.y;
            break;
        case SwingConstants.CENTER:
        case SwingConstants.EAST:
        case SwingConstants.WEST:
            centerY += overlayTargetBounds.y + (overlayTargetBounds.height / 2);
            break;
        case SwingConstants.SOUTH:
        case SwingConstants.SOUTH_EAST:
        case SwingConstants.SOUTH_WEST:
            centerY += overlayTargetBounds.y + overlayTargetBounds.height;
            break;
        default:
            throw new IllegalArgumentException("Unknown value for center [" + center + "]");
        }
        switch (center) {
        case SwingConstants.WEST:
        case SwingConstants.NORTH_WEST:
        case SwingConstants.SOUTH_WEST:
            centerX += overlayTargetBounds.x;
            break;
        case SwingConstants.CENTER:
        case SwingConstants.NORTH:
        case SwingConstants.SOUTH:
            centerX += overlayTargetBounds.x + (overlayTargetBounds.width / 2);
            break;
        case SwingConstants.EAST:
        case SwingConstants.NORTH_EAST:
        case SwingConstants.SOUTH_EAST:
            centerX += overlayTargetBounds.x + overlayTargetBounds.width;
            break;
        default:
            throw new IllegalArgumentException("Unknown value for center [" + center + "]");
        }
        Dimension size = overlay.getPreferredSize();
        Rectangle newBound = new Rectangle(centerX - (size.width / 2), centerY - (size.height / 2), size.width,
                size.height);
        setOverlayBounds(newBound);
    }

    private void setOverlayBounds(Rectangle newBounds) {
        if (!newBounds.equals(overlay.getBounds())) {
            overlay.setBounds(newBounds);
        }
    }

    private void hideOverlay() {
        setOverlayBounds(new Rectangle(0, 0, 0, 0));
    }

    private Container getOverlayCapableParent(JComponent component) {
        Container overlayCapableParent = component.getParent();
        while (overlayCapableParent != null && !(overlayCapableParent instanceof JRootPane)
                && !(overlayCapableParent instanceof JViewport)) {
            overlayCapableParent = (JComponent)overlayCapableParent.getParent();
        }
        return overlayCapableParent;
    }

    private JLayeredPane getLayeredPane(Container overlayCapableParent) {
        if (overlayCapableParent instanceof JRootPane) {
            return ((JRootPane)overlayCapableParent).getLayeredPane();
        }
        else if (overlayCapableParent instanceof JViewport) {
            JViewport viewPort = (JViewport)overlayCapableParent;
            JLayeredPane layeredPane = (JLayeredPane)viewPort.getClientProperty(LAYERED_PANE_PROPERTY);
            if (layeredPane != null) {
                return layeredPane;
            }
            layeredPane = new JLayeredPane();
            Component view = viewPort.getView();
            viewPort.remove(view);
            layeredPane.setLayout(new SingleComponentLayoutManager(view));
            layeredPane.add(view);
            layeredPane.setLayer(view, JLayeredPane.DEFAULT_LAYER.intValue());
            viewPort.setView(layeredPane);
            viewPort.putClientProperty(LAYERED_PANE_PROPERTY, layeredPane);
            return layeredPane;
        }
        else {
            throw new IllegalArgumentException("Don't know how to handle parent [" + overlayCapableParent + "].");
        }
    }

    public class SingleComponentLayoutManager implements LayoutManager {
        private Component singleComponent;

        public SingleComponentLayoutManager(Component singleComponent) {
            this.singleComponent = singleComponent;
        }

        public void removeLayoutComponent(Component comp) {
        }

        public void addLayoutComponent(String name, Component comp) {
        }

        public void layoutContainer(Container parent) {
            Dimension prefSize = singleComponent.getPreferredSize();
            singleComponent.setBounds(0, 0, prefSize.width, prefSize.height);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return singleComponent.getMinimumSize();
        }

        public Dimension preferredLayoutSize(Container parent) {
            return singleComponent.getPreferredSize();
        }
    }
}