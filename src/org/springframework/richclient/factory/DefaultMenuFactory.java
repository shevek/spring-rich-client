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

import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class DefaultMenuFactory implements MenuFactory {

    private static MenuFactory INSTANCE = new DefaultMenuFactory();

    public static final MenuFactory instance() {
        return INSTANCE;
    }

    public static void load(DefaultMenuFactory instance) {
        Assert.notNull(instance, "The sole default menu factory instance is required");
        INSTANCE = instance;
    }

    public JMenu createMenu() {
        return new JMenu();
    }

    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem() {
        return new JCheckBoxMenuItem();
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem() {
        return new JRadioButtonMenuItem();
    }

    public JPopupMenu createPopupMenu() {
        return new JPopupMenu();
    }

    public JMenuBar createMenuBar() {
        return new JMenuBar();
    }

}