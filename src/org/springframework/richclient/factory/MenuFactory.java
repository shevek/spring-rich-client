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
 * @author Keith Donald
 */
public interface MenuFactory {
    public JMenu createMenu();

    public JMenuItem createMenuItem();

    public JCheckBoxMenuItem createCheckBoxMenuItem();

    public JRadioButtonMenuItem createRadioButtonMenuItem();

    public JPopupMenu createPopupMenu();
    
    public JMenuBar createMenuBar();

}