/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.layout;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

public class GridBagLayoutBuilderTests extends TestCase {
    //    static {
    //        Logger.getLogger(GridBagLayoutBuilder.class).setLevel(Level.DEBUG);
    //    }

    public void testAppend1() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
        builder.append(new JPanel());
        builder.append(new JPanel(), 2, 2);
        builder.append(new JPanel());
        builder.nextLine();
        builder.append(new JPanel()).append(new JPanel()).nextLine();
        builder.append(new JPanel()).append(new JPanel()).append(new JPanel()).append(new JPanel()).nextLine();

        JPanel panel = builder.getPanel();
        final Component[] comps = panel.getComponents();
        final GridBagLayout layout = ((GridBagLayout)panel.getLayout());

        check(layout.getConstraints(comps[0]), 0, 0, 1, 1, false, false);
        check(layout.getConstraints(comps[1]), 1, 0, 2, 2, false, false);
        check(layout.getConstraints(comps[2]), 3, 0, 1, 1, false, false);
        check(layout.getConstraints(comps[3]), 0, 1, 1, 1, false, false);
        check(layout.getConstraints(comps[4]), 3, 1, 1, 1, false, false);
        check(layout.getConstraints(comps[5]), 0, 2, 1, 1, false, false);
        check(layout.getConstraints(comps[6]), 1, 2, 1, 1, false, false);
        check(layout.getConstraints(comps[7]), 2, 2, 1, 1, false, false);
        check(layout.getConstraints(comps[8]), 3, 2, 1, 1, false, false);
    }

    public void testAppend2() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
        builder.append(new JPanel()).append(new JPanel(), 2, 1).append(new JPanel()).nextLine();
        builder.append(new JPanel()).append(new JPanel()).append(new JPanel()).append(new JPanel()).nextLine();

        JPanel panel = builder.getPanel();
        final Component[] comps = panel.getComponents();
        final GridBagLayout layout = ((GridBagLayout)panel.getLayout());

        check(layout.getConstraints(comps[0]), 0, 0, 1, 1, false, false);
        check(layout.getConstraints(comps[1]), 1, 0, 2, 1, false, false);
        check(layout.getConstraints(comps[2]), 3, 0, 1, 1, false, false);
        check(layout.getConstraints(comps[3]), 0, 1, 1, 1, false, false);
        check(layout.getConstraints(comps[4]), 1, 1, 1, 1, false, false);
        check(layout.getConstraints(comps[5]), 2, 1, 1, 1, false, false);
        check(layout.getConstraints(comps[6]), 3, 1, 1, 1, false, false);
    }

    private void check(GridBagConstraints gbc, final int x, final int y, final int width, final int height,
            boolean expandX, boolean expandY) {
        assertEquals(x, gbc.gridx);
        assertEquals(y, gbc.gridy);
        assertEquals(width, gbc.gridwidth);
        assertEquals(height, gbc.gridheight);
        assertEquals(expandX, gbc.weightx > 0.0);
        assertEquals(expandY, gbc.weighty > 0.0);
    }

    public void testAppendLabeledField() throws Exception {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();

        builder.appendLabeledField(new JLabel("0"), new JLabel("3"), LabelOrientation.TOP, 1, 2, true, true);
        builder.appendLabeledField(new JLabel("1"), new JLabel("2"), LabelOrientation.LEFT, 2, 1, true, false);
        builder.nextLine();

        builder.appendLabeledField(new JLabel("5"), new JLabel("4"), LabelOrientation.BOTTOM, 3, 1, true, false);
        builder.nextLine();
        builder.nextLine();

        builder.appendLabeledField(new JLabel("7"), new JLabel("6"), LabelOrientation.RIGHT, 3, 1, true, false);
        builder.nextLine();

        JPanel panel = builder.getPanel();
        final Component[] comps = panel.getComponents();
        final GridBagLayout layout = ((GridBagLayout)panel.getLayout());

        check(layout.getConstraints(comps[0]), 0, 0, 1, 1, true, false);
        check(layout.getConstraints(comps[1]), 1, 0, 1, 1, false, false);
        check(layout.getConstraints(comps[2]), 2, 0, 2, 1, true, false);
        check(layout.getConstraints(comps[3]), 0, 1, 1, 2, true, true);
        check(layout.getConstraints(comps[4]), 1, 1, 3, 1, true, false);
        check(layout.getConstraints(comps[5]), 1, 2, 3, 1, true, false);
        check(layout.getConstraints(comps[6]), 0, 3, 3, 1, true, false);
        check(layout.getConstraints(comps[7]), 3, 3, 1, 1, false, false);
    }

}