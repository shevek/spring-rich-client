/*
 * $Header: /usr/local/cvs/module/src/java/File.java,v 1.7 2004/01/16 22:23:11
 * keith Exp $ $Revision$ $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.application.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.richclient.application.View;
import org.springframework.richclient.control.SimpleInternalFrame;
import org.springframework.richclient.factory.ControlFactory;

public class ViewPane implements ControlFactory, PropertyChangeListener {

    private View view;

    private SimpleInternalFrame pane;

    public ViewPane(View view) {
        this.view = view;
        this.pane = new SimpleInternalFrame(view.getIcon(), view
                .getDisplayName(), null, view.getControl());
        this.view.addPropertyChangeListener(this);
    }

    public View getView() {
        return view;
    }
    
    public JComponent getControl() {
        return pane;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        update();
    }

    private void update() {
        pane.setTitle(view.getDisplayName());
        pane.setFrameIcon(view.getIcon());
        pane.setToolTipText(view.getCaption());
    }

}