package org.springframework.richclient.layout;

import java.awt.*;

import javax.swing.*;

import junit.framework.TestCase;

public class TestGridBagLayoutBuilder extends TestCase {
//    static {
//        Logger.getLogger(GridBagLayoutBuilder.class).setLevel(Level.DEBUG);
//    }

    public void testAppend1() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
        builder.append(new JPanel());
        builder.append(new JPanel(), 2, 2);
        builder.append(new JPanel());
        builder.nextLine();
        builder.append(new JPanel()).
                append(new JPanel()).nextLine();
        builder.append(new JPanel()).
                append(new JPanel()).
                append(new JPanel()).
                append(new JPanel()).nextLine();

        JPanel panel = builder.getPanel();
        final Component[] comps = panel.getComponents();
        final GridBagLayout layout = ((GridBagLayout)panel.getLayout());

        check(layout.getConstraints(comps[0]), 0, 0, 1, 1);
        check(layout.getConstraints(comps[1]), 1, 0, 2, 2);
        check(layout.getConstraints(comps[2]), 3, 0, 1, 1);
        check(layout.getConstraints(comps[3]), 0, 1, 1, 1);
        check(layout.getConstraints(comps[4]), 3, 1, 1, 1);
        check(layout.getConstraints(comps[5]), 0, 2, 1, 1);
        check(layout.getConstraints(comps[6]), 1, 2, 1, 1);
        check(layout.getConstraints(comps[7]), 2, 2, 1, 1);
        check(layout.getConstraints(comps[8]), 3, 2, 1, 1);
    }


    public void testAppend2() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
        builder.append(new JPanel()).
                append(new JPanel(), 2, 1).
                append(new JPanel()).nextLine();
        builder.append(new JPanel()).
                append(new JPanel()).
                append(new JPanel()).
                append(new JPanel()).nextLine();

        JPanel panel = builder.getPanel();
        final Component[] comps = panel.getComponents();
        final GridBagLayout layout = ((GridBagLayout)panel.getLayout());

        check(layout.getConstraints(comps[0]), 0, 0, 1, 1);
        check(layout.getConstraints(comps[1]), 1, 0, 2, 1);
        check(layout.getConstraints(comps[2]), 3, 0, 1, 1);
        check(layout.getConstraints(comps[3]), 0, 1, 1, 1);
        check(layout.getConstraints(comps[4]), 1, 1, 1, 1);
        check(layout.getConstraints(comps[5]), 2, 1, 1, 1);
        check(layout.getConstraints(comps[6]), 3, 1, 1, 1);
    }

    private void check(GridBagConstraints gbc, final int x, final int y,
                       final int width, final int height) {
        assertEquals(x, gbc.gridx);
        assertEquals(y, gbc.gridy);
        assertEquals(width, gbc.gridwidth);
        assertEquals(height, gbc.gridheight);
    }

}
