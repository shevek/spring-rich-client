package org.springframework.richclient.application.support;

import org.springframework.richclient.application.ApplicationPage;


public class DefaultApplicationPageTests extends AbstractApplicationPageTestCase {

    @Override
    protected ApplicationPage createApplicationPage() {
        return new DefaultApplicationPage();
    }

}
