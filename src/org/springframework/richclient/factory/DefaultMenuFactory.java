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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * @author Keith Donald
 */
public class DefaultMenuFactory implements MenuFactory {

    private static final MenuFactory INSTANCE = new DefaultMenuFactory();
    
    public static final MenuFactory instance() {
        return INSTANCE;
    }
    
    /**
     * @see org.springframework.richclient.factory.MenuFactory#createMenu()
     */
    public JMenu createMenu() {
        return new JMenu();
    }

    /**
     * @see org.springframework.richclient.factory.MenuFactory#createMenuItem()
     */
    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    /**
     * @see org.springframework.richclient.factory.MenuFactory#createCheckBoxMenuItem()
     */
    public JCheckBoxMenuItem createCheckBoxMenuItem() {
        return new JCheckBoxMenuItem();
    }

    /**
     * @see org.springframework.richclient.factory.MenuFactory#createRadioButtonMenuItem()
     */
    public JRadioButtonMenuItem createRadioButtonMenuItem() {
        return new JRadioButtonMenuItem();
    }

    /**
     * @see org.springframework.richclient.factory.MenuFactory#createPopupMenu()
     */
    public JPopupMenu createPopupMenu() {
        return new JPopupMenu();
    }

}
