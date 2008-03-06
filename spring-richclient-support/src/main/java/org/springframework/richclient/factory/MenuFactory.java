/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Marker for menu factories.
 *
 * @author Keith Donald
 */
public interface MenuFactory {

    /**
     * Create a menu.
     */
    public JMenu createMenu();

    /**
     * Create a menu item.
     */
    public JMenuItem createMenuItem();

    /**
     * Create a menu item with a checkbox LaF.
     */
    public JCheckBoxMenuItem createCheckBoxMenuItem();

    /**
     * Create a menu item with a radio button LaF.
     */
    public JRadioButtonMenuItem createRadioButtonMenuItem();

    /**
     * Create a popup menu most commonly used when with the mouse.
     */
    public JPopupMenu createPopupMenu();

    /**
     * Create a menu bar.
     */
    public JMenuBar createMenuBar();

}