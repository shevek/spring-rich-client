package org.springframework.richclient.layout;

import javax.swing.*;

import junit.framework.TestCase;

public class TestGridBagLayoutBuilder extends TestCase {

    public void testAppend() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
        builder.append(new JPanel()).append(new JPanel(), 2, 2).append(new JPanel()).nextLine();
        builder.append(new JPanel()).append(new JPanel()).nextLine();
        builder.append(new JPanel()).append(new JPanel()).append(new JPanel()).append(new JPanel()).nextLine();
        JPanel panel = builder.getPanel();
    }

}
