/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

/**
 * @author Keith Donald
 */
public class DefaultButtonFactory implements ButtonFactory {

    private static final ButtonFactory INSTANCE = new DefaultButtonFactory();
    
    public static final ButtonFactory instance() {
        return INSTANCE;
    }
    
    /**
     * @see org.springframework.richclient.factory.ButtonFactory#createButton()
     */
    public JButton createButton() {
        return new JButton();
    }

    /**
     * @see org.springframework.richclient.factory.ButtonFactory#createMenuItem()
     */
    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    /**
     * @see org.springframework.richclient.factory.ButtonFactory#createCheckBox()
     */
    public JCheckBox createCheckBox() {
        return new JCheckBox();
    }

    /**
     * @see org.springframework.richclient.factory.ButtonFactory#createToggleButton()
     */
    public JToggleButton createToggleButton() {
        return new JToggleButton();
    }

    /**
     * @see org.springframework.richclient.factory.ButtonFactory#createRadioButton()
     */
    public JRadioButton createRadioButton() {
        return new JRadioButton();
    }
}