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
import javax.swing.JToolBar;

import org.springframework.richclient.application.View;
import org.springframework.richclient.control.SimpleInternalFrame;
import org.springframework.richclient.factory.AbstractControlFactory;

public class ViewPane extends AbstractControlFactory implements
        PropertyChangeListener {
    private View view;

    public ViewPane(View view) {
        this.view = view;
        this.view.addPropertyChangeListener(this);
    }

    public View getView() {
        return view;
    }

    protected JComponent createControl() {
        return new SimpleInternalFrame(view.getIcon(), view.getDisplayName(),
                new JToolBar(), view.getControl());
    }

    public void propertyChange(PropertyChangeEvent evt) {
       handleViewPropertyChange();
    }

    protected void handleViewPropertyChange() {
        SimpleInternalFrame frame = (SimpleInternalFrame)getControl();
        frame.setTitle(view.getDisplayName());
        frame.setFrameIcon(view.getIcon());
        frame.setToolTipText(view.getCaption());
    }

    public void requestFocusInWindow() {
        getControl().requestFocusInWindow();
    }

}