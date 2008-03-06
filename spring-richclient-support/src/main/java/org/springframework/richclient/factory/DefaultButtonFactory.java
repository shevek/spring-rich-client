/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

/**
 * Default implementation of a {@link ButtonFactory}.
 *
 * @author Keith Donald
 */
public class DefaultButtonFactory implements ButtonFactory {

    /**
     * {@inheritDoc}
     */
    public AbstractButton createButton() {
        return new JButton();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractButton createCheckBox() {
        return new JCheckBox();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractButton createToggleButton() {
        return new JToggleButton();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractButton createRadioButton() {
        return new JRadioButton();
    }
}