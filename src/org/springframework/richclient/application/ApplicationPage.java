package org.springframework.richclient.application;

import org.springframework.richclient.factory.ControlFactory;

public interface ApplicationPage extends ControlFactory {
    public String getId();

    public ApplicationWindow geWindow();

    public void addPageComponentListener(PageComponentListener listener);

    public void removePageComponentListener(PageComponentListener listener);

    public PageComponent getActiveComponent();

    public void showView(String viewDescriptorId);

    public void showView(ViewDescriptor viewDescriptor);

    public void openEditor(Object editorInput);

    public boolean closeAllEditors();
    
    public boolean close();
}