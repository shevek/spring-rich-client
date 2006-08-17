package org.springframework.richclient.application.mdi;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

public class DesktopApplicationPageFactory implements ApplicationPageFactory{

    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
        return new DesktopApplicationPage(window, descriptor);
    }

}
