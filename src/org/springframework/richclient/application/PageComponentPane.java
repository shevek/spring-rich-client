package org.springframework.richclient.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.springframework.richclient.control.SimpleInternalFrame;
import org.springframework.richclient.factory.AbstractControlFactory;

public class PageComponentPane extends AbstractControlFactory implements
        PropertyChangeListener {
    private PageComponent component;

    public PageComponentPane(PageComponent component) {
        this.component = component;
        this.component.addPropertyChangeListener(this);
    }

    public PageComponent getPageComponent() {
        return component;
    }

    protected JComponent createControl() {
        return new SimpleInternalFrame(component.getIcon(), component
                .getDisplayName(), createViewToolBar(), component.getControl());
    }

    protected JToolBar createViewToolBar() {
        // todo
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        handleViewPropertyChange();
    }

    protected void handleViewPropertyChange() {
        SimpleInternalFrame frame = (SimpleInternalFrame)getControl();
        frame.setTitle(component.getDisplayName());
        frame.setFrameIcon(component.getIcon());
        frame.setToolTipText(component.getCaption());
    }

    public void requestFocusInWindow() {
        getControl().requestFocusInWindow();
    }

}