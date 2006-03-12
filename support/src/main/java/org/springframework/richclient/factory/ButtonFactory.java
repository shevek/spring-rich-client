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
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

/**
 * @author Keith Donald
 */
public interface ButtonFactory {
    public JButton createButton();

    public JCheckBox createCheckBox();

    public JToggleButton createToggleButton();

    public JRadioButton createRadioButton();
}