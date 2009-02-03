package org.springframework.richclient.samples.dataeditor.app;

import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.ApplicationWindow;

import java.awt.*;

public class DataEditorApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
    @Override
    public void onWindowOpened(ApplicationWindow window)
    {
        super.onWindowOpened(window);
        window.getControl().setExtendedState(Frame.MAXIMIZED_BOTH);
    }
}
