package org.springframework.richclient.application.support;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentListener;

public class PageComponentListenerAdapter implements PageComponentListener {
    private PageComponent activeComponent;

    public PageComponent getActiveComponent() {
        return activeComponent;
    }

    public void componentOpened(PageComponent component) {

    }

    public void componentFocusGained(PageComponent component) {
        this.activeComponent = component;
    }

    public void componentFocusLost(PageComponent component) {
        this.activeComponent = null;
    }

    public void componentClosed(PageComponent component) {

    }

}