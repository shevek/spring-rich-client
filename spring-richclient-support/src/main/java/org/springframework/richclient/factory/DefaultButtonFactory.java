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

    public JButton createButton() {
        return new JButton();
    }

    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    public JCheckBox createCheckBox() {
        return new JCheckBox();
    }

    public JToggleButton createToggleButton() {
        return new JToggleButton();
    }

    public JRadioButton createRadioButton() {
        return new JRadioButton();
    }
}