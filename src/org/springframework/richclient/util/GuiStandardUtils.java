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
 */
package org.springframework.richclient.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import org.springframework.richclient.core.UIConstants;
import org.springframework.util.Assert;

import com.jgoodies.forms.factories.Borders;

/**
 * Utility functions that help enforce a standard look and feel in accordance
 * with the Java Look and Feel Design Guidelines.
 * 
 * @author Keith Donald
 */
public class GuiStandardUtils {

    private GuiStandardUtils() {
    }

    public static JComponent attachBorder(JComponent c) {
        return attachBorder(c, createStandardBorder());
    }

    public static JComponent attachBorder(JComponent c, Border border) {
        c.setBorder(border);
        return c;
    }
    
    public static JComponent attachDialogBorder(JComponent c) {
        if (c instanceof JTabbedPane) {
            c.setBorder(Borders.TABBED_DIALOG_BORDER);
        } else {
            c.setBorder(Borders.DIALOG_BORDER);
        }
        return c;
    }

    /**
     * Return a border of dimensions recommended by the Java Look and Feel
     * Design Guidelines, suitable for many common cases.
     * <p>
     * Each side of the border has size SwingConstants.STANDARD_BORDER
     */
    public static Border createStandardBorder() {
        return createEvenlySpacedBorder(UIConstants.STANDARD_BORDER);
    }

    /**
     * Return a border of dimensions recommended by the Java Look and Feel
     * Design Guidelines, suitable for many common cases.
     * <p>
     * Each side of the border has size SwingConstants.STANDARD_BORDER
     */
    public static Border createEvenlySpacedBorder(int spaces) {
        return BorderFactory.createEmptyBorder(spaces, spaces, spaces, spaces);
    }

    public static Border createLeftAndRightBorder(int spaces) {
        return BorderFactory.createEmptyBorder(0, spaces, 0, spaces);
    }

    public static Border createTopAndBottomBorder(int spaces) {
        return BorderFactory.createEmptyBorder(spaces, 0, spaces, 0);
    }

    /**
     * Return text which conforms to the Look and Feel Design Guidelines for the
     * title of a dialog : the application name, a colon, then the name of the
     * specific dialog.
     * 
     * @param dialogName
     *            the short name of the dialog.
     */
    public static String createDialogTitle(String appName, String dialogName) {
        Assert.hasText(dialogName);
        if (appName != null) {
            StringBuffer buf = new StringBuffer(appName);
            buf.append(": ");
            buf.append(dialogName);
            return buf.toString();
        }
        else {
            return dialogName;
        }
    }

    /**
     * Make a horizontal row of buttons of equal size, whch are equally spaced,
     * and aligned on the right.
     * 
     * <P>
     * The returned component has border spacing only on the top (of the size
     * recommended by the Look and Feel Design Guidelines). All other spacing
     * must be applied elsewhere ; usually, this will only mean that the
     * dialog's top-level panel should use {@link #buildStandardBorder}.
     * 
     * @param buttons
     *            contains <code>JButton</code> objects.
     * @return A row displaying the buttons horizontally.
     */
    public static JComponent createCommandButtonRow(JButton[] buttons) {
        equalizeSizes(buttons);
        JPanel panel = new JPanel();
        LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.add(Box.createHorizontalGlue());
        for (int i = 0; i < buttons.length; i++) {
            panel.add(buttons[i]);
            if (i < (buttons.length - 1)) {
                panel.add(Box.createHorizontalStrut(UIConstants.ONE_SPACE));
            }
        }
        panel.setBorder(createStandardBorder());
        return panel;
    }

    /**
     * Make a vertical row of buttons of equal size, whch are equally spaced,
     * and aligned on the right.
     * 
     * <P>
     * The returned component has border spacing only on the left (of the size
     * recommended by the Look and Feel Design Guidelines). All other spacing
     * must be applied elsewhere ; usually, this will only mean that the
     * dialog's top-level panel should use {@link #buildStandardBorder}.
     * 
     * @param buttons
     *            contains <code>JButton</code> objects.
     * @return A column displaying the buttons vertically.
     */
    public static JComponent createCommandButtonColumn(JButton[] buttons) {
        equalizeSizes(buttons);
        JPanel panel = new JPanel();
        LayoutManager layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(0,
                UIConstants.TWO_SPACES, 0, 0));
        for (int i = 0; i < buttons.length; i++) {
            panel.add(buttons[i]);
            if (i < (buttons.length - 1)) {
                panel.add(Box.createVerticalStrut(UIConstants.ONE_SPACE));
            }
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     * Sets the items in <code>aComponents</code> to the same size.
     * 
     * Sets each component's preferred and maximum sizes. The actual size is
     * determined by the layout manager, which adjusts for locale-specific
     * strings and customized fonts. (See this <a
     * href="http://java.sun.com/products/jlf/ed2/samcode/prefere.html">Sun doc
     * </a> for more information.)
     * 
     * @param components
     *            contains <code>JComponent</code> objects.
     */
    public static void equalizeSizes(JComponent[] components) {
        Dimension targetSize = new Dimension(0, 0);
        for (int i = 0; i < components.length; i++) {
            JComponent comp = components[i];
            Dimension compSize = comp.getPreferredSize();
            double width = Math.max(targetSize.getWidth(), compSize.getWidth());
            double height = Math.max(targetSize.getHeight(), compSize
                    .getHeight());
            targetSize.setSize(width, height);
        }
        setSizes(components, targetSize);
    }

    private static void setSizes(JComponent[] components,
            final Dimension dimension) {
        for (int i = 0; i < components.length; i++) {
            JComponent comp = components[i];
            // shouldn't have to clone these (hopefully awt does it for us)
            comp.setPreferredSize(dimension);
            comp.setMaximumSize(dimension);
        }
    }

    public static JTextArea createStandardTextArea(int rows, int columns) {
        JTextArea area = createStandardTextArea("");
        area.setRows(rows);
        area.setColumns(columns);
        return area;
    }
    
    /**
     * An alternative to multi-line labels, for the presentation of several
     * lines of text, and for which the line breaks are determined solely by the
     * control.
     * 
     * @param text
     *            text that does not contain newline characters or html.
     * @return <code>JTextArea</code> which is not editable, has improved
     *         spacing over the supplied default (placing
     *         {@link UIConstants#ONE_SPACE}on the left and right), and which
     *         wraps lines on word boundarie.
     */
    public static JTextArea createStandardTextArea(String text) {
        JTextArea result = new JTextArea(text);
        return configureStandardTextArea(result);
    }
    
    public static JTextArea configureStandardTextArea(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(0, UIConstants.ONE_SPACE, 0,
                UIConstants.ONE_SPACE));
        return textArea;
    }

    /**
     * An alternative to multi-line labels, for the presentation of several
     * lines of text, and for which line breaks are determined solely by
     * <code>aText</code>, and not by the control.
     * 
     * @param text
     *            the text to be placed in the text area.
     * @return <code>JTextArea</code> which is not editable and has improved
     *         spacing over the supplied default (placing
     *         {@link UIConstants#ONE_SPACE} on the left and right).
     */
    public static JTextArea createStandardTextAreaHardNewLines(String text) {
        JTextArea result = new JTextArea(text);
        result.setEditable(false);
        result.setMargin(new Insets(0, UIConstants.ONE_SPACE, 0,
                UIConstants.ONE_SPACE));
        return result;
    }

    /**
     * If aLabel has text which is longer than MAX_LABEL_LENGTH, then truncate
     * the label text and place an ellipsis at the end; the original text is
     * placed in a tooltip.
     * 
     * This is particularly useful for displaying file names, whose length can
     * vary widely between deployments.
     * 
     * @param label
     *            The label to truncate if length() > MAX_LABEL_LENGTH.
     */
    public static void truncateLabelIfLong(JLabel label) {
        String originalText = label.getText();
        if (originalText.length() > UIConstants.MAX_LABEL_LENGTH) {
            label.setToolTipText(originalText);
            String truncatedText = originalText.substring(0,
                    UIConstants.MAX_LABEL_LENGTH)
                    + "...";
            label.setText(truncatedText);
        }
    }

    /**
     * This will allow selection and copy to work but still retain the label
     * look
     */
    public static JTextArea textAreaAsLabel(JTextArea textArea) {
        //  Turn on word wrap
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        // Perform the other changes to complete the look
        textComponentAsLabel(textArea);
        return textArea;
    }

    /**
     * This will allow selection and copy to work but still retain the label
     * look
     */
    public static JTextComponent textComponentAsLabel(JTextComponent textcomponent) {
        //  Make the text component non editable
        textcomponent.setEditable(false);
        // Make the text area look like a label
        textcomponent.setBackground((Color)UIManager.get("Label.background"));
        textcomponent.setForeground((Color)UIManager.get("Label.foreground"));
        textcomponent.setBorder(null);
        return textcomponent;
    }

    /**
     * Useful debug function to place a colored, line border around a component
     * for layout management debugging.
     * 
     * @param c
     *            the component
     * @param color
     *            the border color
     */
    public static void createDebugBorder(JComponent c, Color color) {
        if (color == null) {
            color = Color.BLACK;
        }
        c.setBorder(BorderFactory.createLineBorder(color));
    }

}