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
package org.springframework.richclient.form.builder;

import java.awt.*;

import javax.swing.*;

import org.springframework.enums.ShortCodedEnum;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.LayoutBuilder;

/**
 * This provides an easy way to create panels using a {@link GridBagLayout}.
 * <p />
 *
 * Usage is:
 *
 * <pre>
 * GridBagLayoutBuilder builder = new GridBagLayoutBuilder();
 *
 * builder.appendRightLabel(&quot;label.field1&quot;).appendField(field1);
 * builder.appendRightLabel(&quot;label.field2&quot;).appendField(field2);
 * builder.nextLine();
 *
 * builder.appendRightLabel(&quot;label.field3&quot;).appendField(field3);
 * // because &quot;field3&quot; is the last component on this line, but the panel has
 * // 4 columns, &quot;field3&quot; will span 3 columns
 * builder.nextLine();
 *
 * // once everything's been put into the builder, ask it to build a panel
 * // to use in the UI.
 * JPanel panel = builder.getPanel();
 * </pre>
 *
 * @author Jim Moore
 * @see #setAutoSpanLastComponent(boolean)
 * @see #setShowGuidelines(boolean)
 * @see #setComponentFactory(ComponentFactory)
 */
public class GridBagLayoutFormBuilder extends AbstractFormBuilder
        implements LayoutBuilder {

    private final GridBagLayoutBuilder builder;

    public static final class LabelOrientation extends ShortCodedEnum {
        public static final LabelOrientation TOP =
                new LabelOrientation(SwingConstants.TOP, "Top");
        public static final LabelOrientation BOTTOM =
                new LabelOrientation(SwingConstants.BOTTOM, "Bottom");
        public static final LabelOrientation LEFT =
                new LabelOrientation(SwingConstants.LEFT, "Left");
        public static final LabelOrientation RIGHT =
                new LabelOrientation(SwingConstants.RIGHT, "Right");

        private LabelOrientation(int code, String label) {
            super(code, label);
        }
    }

    public GridBagLayoutFormBuilder(SwingFormModel swingFormModel) {
        super(swingFormModel);
        this.builder = new GridBagLayoutBuilder();
    }

    /**
     * Returns the underlying {@link GridBagLayoutBuilder}.  Should be used
     * with caution.
     *
     * @return never null
     */
    public final GridBagLayoutBuilder getBuilder() {
        return builder;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        super.setComponentFactory(componentFactory);
        builder.setComponentFactory(componentFactory);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see SwingFormModel#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName) {
        return appendLabeledField(propertyName, LabelOrientation.LEFT);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see SwingFormModel#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       int colSpan) {
        return appendLabeledField(propertyName, LabelOrientation.LEFT,
                colSpan);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see SwingFormModel#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       LabelOrientation labelOrientation) {
        return appendLabeledField(propertyName, labelOrientation, 1);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see SwingFormModel#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       LabelOrientation labelOrientation,
                                                       int colSpan) {
        final JComponent field = getDefaultComponent(propertyName);

        return appendLabeledField(propertyName, field, labelOrientation,
                colSpan);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see SwingFormModel#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       final JComponent field,
                                                       LabelOrientation labelOrientation) {
        return appendLabeledField(propertyName, field, labelOrientation, 1);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see FormComponentInterceptor#processLabel(String, JComponent)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       final JComponent field,
                                                       LabelOrientation labelOrientation,
                                                       int colSpan) {
        return appendLabeledField(propertyName, field, labelOrientation,
                colSpan, 1, true, false);
    }

    /**
     * Appends a label and field to the end of the current line.<p />
     *
     * The label will be to the left of the field, and be right-justified.<br />
     * The field will "grow" horizontally as space allows.<p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see SwingFormModel#createLabel(String)
     * @see FormComponentInterceptor#processLabel(String, JComponent)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName,
                                                       final JComponent field,
                                                       LabelOrientation labelOrientation,
                                                       int colSpan,
                                                       int rowSpan,
                                                       boolean expandX,
                                                       boolean expandY) {
        final JLabel label = getLabelFor(propertyName, field);

        if (labelOrientation == LabelOrientation.LEFT ||
                labelOrientation == null) {
            label.setHorizontalAlignment(JLabel.RIGHT);
            builder.appendLabel(label).append(field, colSpan, rowSpan,
                    expandX, expandY);
        }
        else if (labelOrientation == LabelOrientation.RIGHT) {
            label.setHorizontalAlignment(JLabel.LEFT);
            builder.append(field, colSpan, rowSpan, expandX, expandY)
                    .appendLabel(label);
        }
        else if (labelOrientation == LabelOrientation.TOP) {
            label.setHorizontalAlignment(JLabel.LEFT);
            final int col = builder.getCurrentCol();
            final int row = builder.getCurrentRow();
            final Insets insets = builder.getDefaultInsets();
            builder.append(label, col, row, colSpan, 1, false, expandY,
                    insets).append(field, col, row + 1, colSpan, rowSpan,
                            expandX, expandY, insets);
        }
        else if (labelOrientation == LabelOrientation.BOTTOM) {
            label.setHorizontalAlignment(JLabel.LEFT);
            final int col = builder.getCurrentCol();
            final int row = builder.getCurrentRow();
            final Insets insets = builder.getDefaultInsets();
            builder.append(field, col, row, colSpan, rowSpan, expandX,
                    expandY, insets).append(label, col, row + rowSpan,
                            colSpan, 1, false, expandY, insets);
        }

        return this;
    }

    /**
     * Appends a seperator (usually a horizonal line). Has an implicit
     * {@link #nextLine()} before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder appendSeparator() {
        return appendSeparator(null);
    }

    /**
     * Appends a seperator (usually a horizonal line) using the provided
     * string as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}
     * message bundle for the text to put along with the seperator. Has an
     * implicit {@link #nextLine()} before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder appendSeparator(String labelKey) {
        builder.appendSeparator(labelKey);
        return this;
    }

    /**
     * Ends the current line and starts a new one
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder nextLine() {
        builder.nextLine();
        return this;
    }

    /**
     * Should this show "guidelines"? Useful for debugging layouts.
     */
    public void setShowGuidelines(boolean showGuidelines) {
        builder.setShowGuidelines(showGuidelines);
    }

    /**
     * Creates and returns a JPanel with all the given components in it, using
     * the "hints" that were provided to the builder.
     *
     * @return a new JPanel with the components laid-out in it
     */
    public JPanel getPanel() {
        return builder.getPanel();
    }

    /**
     * @see GridBagLayoutBuilder#setAutoSpanLastComponent(boolean)
     */
    public void setAutoSpanLastComponent(boolean autoSpanLastComponent) {
        builder.setAutoSpanLastComponent(autoSpanLastComponent);
    }

}
