package org.springframework.richclient.application;

import java.beans.PropertyChangeListener;

import org.springframework.richclient.factory.ControlFactory;

public interface ApplicationPage extends ControlFactory, PropertyChangeListener {

    public ApplicationWindow getParentWindow();

    public void addViewListener(ViewListener listener);

    public void removeViewListener(ViewListener listener);

    public void showView(String viewDescriptorId);

    public void showView(ViewDescriptor viewDescriptor);

    public View getActiveView();

}