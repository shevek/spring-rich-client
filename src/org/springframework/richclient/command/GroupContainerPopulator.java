/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.command;

import java.awt.Component;

import javax.swing.JComponent;

/**
 * @author Keith Donald
 */
public interface GroupContainerPopulator {
    public JComponent getControl();
    public void add(Component component);
    public void addSeparator();
    public void onComponentsAdded();
}
