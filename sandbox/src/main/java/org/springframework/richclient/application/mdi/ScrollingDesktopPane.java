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
package org.springframework.richclient.application.mdi;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.springframework.richclient.util.PopupMenuMouseListener;

/**
 * An extension of WDesktopPane that supports often used MDI functionality. This
 * class also handles setting scroll bars for when windows move too far to the
 * left or bottom, providing the MDIDesktopPane is in a ScrollPane.
 */
public class ScrollingDesktopPane extends JDesktopPane {
    private static int FRAME_OFFSET = 20;

    private ScrollingDesktopManager manager;

    public ScrollingDesktopPane() {
        manager = new ScrollingDesktopManager(this);
        super.setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        addMouseListener(new PopupMenuMouseListener() {
            protected JPopupMenu getPopupMenu() {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem tile = new JMenuItem("Tile");
                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        tileFrames();
                    }
                });
                JMenuItem cascade = new JMenuItem("Cascade");
                cascade.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cascadeFrames();
                    }
                });
                popupMenu.add(tile);
                popupMenu.add(cascade);
                if (getAllFrames().length > 0) {
                    popupMenu.addSeparator();
                    for (int i = 0; i < getAllFrames().length; i++) {
                        final JInternalFrame frame = getAllFrames()[i];
                        JMenuItem activate = new JMenuItem(i + " " + frame.getTitle());
                        activate.setIcon(frame.getFrameIcon());
                        if (i < 10) {
                            activate.setMnemonic(KeyEvent.VK_0 + i);
                        }
                        activate.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                try {
                                    frame.setSelected(true);
                                }
                                catch (PropertyVetoException e) {
                                    // ignore
                                }
                            }
                        });
                        popupMenu.add(activate);
                    }
                }
                return popupMenu;
            }
        });
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        checkDesktopSize();
    }

    public Component add(JInternalFrame frame) {
        JInternalFrame[] array = getAllFrames();
        Point p;

        Component retval = super.add(frame);
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        }
        else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);

        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            frame.toBack();
        }
        return retval;
    }

    public void remove(Component c) {
        super.remove(c);
        checkDesktopSize();
    }

    /**
     * Cascade all internal frames
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;
        JInternalFrame allFrames[] = getAllFrames();

        manager.setNormalSize();
        int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
        int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
        for (int i = allFrames.length - 1; i >= 0; i--) {
            allFrames[i].setSize(frameWidth, frameHeight);
            allFrames[i].setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }

    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        JInternalFrame allFrames[] = getAllFrames();
        manager.setNormalSize();
        int frameHeight = getBounds().height / allFrames.length;
        int y = 0;
        for (int i = 0; i < allFrames.length; i++) {
            allFrames[i].setSize(getBounds().width, frameHeight);
            allFrames[i].setLocation(0, y);
            y = y + frameHeight;
        }
    }

    public void setAllSize(int width, int height) {
        Dimension d = new Dimension(width, height);

        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    private void checkDesktopSize() {
        if (getParent() != null && isVisible())
            manager.resizeDesktop();
    }
}
