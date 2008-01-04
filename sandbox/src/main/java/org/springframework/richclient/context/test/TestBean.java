package org.springframework.richclient.context.test;

import org.springframework.richclient.core.TitleConfigurable;

public class TestBean implements TitleConfigurable {

    public void setTitle(String title) {
        System.out.println("title set to "+title);
    }

}
