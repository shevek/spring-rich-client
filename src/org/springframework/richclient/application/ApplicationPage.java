package org.springframework.richclient.application;

import org.springframework.richclient.factory.ControlFactory;

public interface ApplicationPage extends ControlFactory {
    public String getId();

    public ApplicationWindow getParentWindow();

    public void addViewListener(ViewListener listener);

    public void removeViewListener(ViewListener listener);

    public void showView(String viewDescriptorId);

    public void showView(ViewDescriptor viewDescriptor);
}