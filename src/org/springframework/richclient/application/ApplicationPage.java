package org.springframework.richclient.application;

import java.beans.PropertyChangeListener;

import javax.swing.*;

public interface ApplicationPage extends PropertyChangeListener {

    ApplicationWindow getParentWindow();

    JComponent getControl();

    void addViewListener(ViewListener listener);

    void showView(String viewName);

    void showView(ViewDescriptor viewDescriptor);

    View getView();

}
