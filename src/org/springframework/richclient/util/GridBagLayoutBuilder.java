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
package org.springframework.richclient.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.ComponentFactory;

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
 * @see #setAutoSpanLastComponent(boolean)
 * @see #setShowGuidelines(boolean)
 * @see #setComponentFactory(ComponentFactory)
 */
public class GridBagLayoutBuilder {
    private static final Log LOG = LogFactory
        .getLog(GridBagLayoutBuilder.class);

    private Insets defaultInsets = new Insets(0, 0, 4, 4);

    private boolean showGuidelines = false;

    private boolean autoSpanLastComponent = true;

    private ComponentFactory componentFactory;

    private int currentCol;

    private List rows;

    private List currentRowList;

    private int maxCol = 0;


    public GridBagLayoutBuilder() {
        currentCol = 0;
        rows = new ArrayList();
        currentRowList = new ArrayList();
    }


    /**
     * Returns the default {@link Insets}used when adding components
     */
    public Insets getDefaultInsets() {
        return defaultInsets;
    }


    /**
     * Sets the default {@link Insets}used when adding components
     */
    public void setDefaultInsets(Insets defaultInsets) {
        this.defaultInsets = defaultInsets;
    }


    /**
     * Returns the current row (zero-based) that the builder is putting
     * components in
     */
    public int getCurrentRow() {
        return rows.size();
    }


    /**
     * Returns the current column (zero-based) that the builder is putting
     * components in
     */
    public int getCurrentCol() {
        return currentCol;
    }


    /**
     * Returns the {@link ComponentFactory}that this uses to create things like
     * labels.
     *
     * @return if not explicitly set, this uses the {@link Application}'s
     */
    public ComponentFactory getComponentFactory() {
        if (this.componentFactory == null) {
            this.componentFactory = Application.services()
                .getComponentFactory();
        }
        return this.componentFactory;
    }


    /**
     * Sets the {@link ComponentFactory}that this uses to create things like
     * labels.
     */
    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }


    /**
     * Appends the given component to the end of the current line, using the
     * default insets and no expansion
     *
     * @param component the component to add to the current line
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder append(Component component) {
        return append(component, 1, 1);
    }


    /**
     * Appends the given component to the end of the current line, using the
     * default insets and no expansion
     *
     * @param component the component to add to the current line
     * @param colSpan   the number of columns to span
     * @param rowSpan   the number of rows to span
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder append(Component component, int colSpan,
                                       int rowSpan) {
        return append(component, 1, 1, 0.0, 0.0);
    }


    /**
     * Appends the given component to the end of the current line, using the
     * default insets
     *
     * @param component the component to add to the current line
     * @param colSpan   the number of columns to span
     * @param rowSpan   the number of rows to span
     * @param expandX   should the component "grow" horrizontally?
     * @param expandY   should the component "grow" vertically?
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder append(Component component, int colSpan,
                                       int rowSpan, boolean expandX,
                                       boolean expandY) {
        return append(component, colSpan, rowSpan, expandX, expandY,
            defaultInsets);
    }


    /**
     * Appends the given component to the end of the current line
     *
     * @param component the component to add to the current line
     * @param colSpan   the number of columns to span
     * @param rowSpan   the number of rows to span
     * @param expandX   should the component "grow" horrizontally?
     * @param expandY   should the component "grow" vertically?
     * @param insets    the insets to use for this component
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder append(Component component, int colSpan,
                                       int rowSpan, boolean expandX,
                                       boolean expandY, Insets insets) {
        if (expandX && expandY)
            return append(component, colSpan, rowSpan, 1.0, 1.0, insets);
        else if (expandX)
            return append(component, colSpan, rowSpan, 1.0, 0.0, insets);
        else if (expandY)
            return append(component, colSpan, rowSpan, 0.0, 1.0, insets);
        else
            return append(component, colSpan, rowSpan, 0.0, 0.0, insets);
    }


    /**
     * Appends the given component to the end of the current line, using the
     * default insets
     *
     * @param component the component to add to the current line
     * @param colSpan   the number of columns to span
     * @param rowSpan   the number of rows to span
     * @param xweight   the "growth weight" horrizontally
     * @param yweight   the "growth weight" horrizontally
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see GridBagConstraints#weightx
     * @see GridBagConstraints#weighty
     */
    public GridBagLayoutBuilder append(Component component, int colSpan,
                                       int rowSpan, double xweight,
                                       double yweight) {
        return append(component, colSpan, rowSpan, xweight, yweight,
            defaultInsets);
    }


    /**
     * Appends the given component to the end of the current line
     *
     * @param component the component to add to the current line
     * @param colSpan   the number of columns to span
     * @param rowSpan   the number of rows to span
     * @param xweight   the "growth weight" horrizontally
     * @param yweight   the "growth weight" horrizontally
     * @param insets    the insets to use for this component
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see GridBagConstraints#weightx
     * @see GridBagConstraints#weighty
     */
    public GridBagLayoutBuilder append(Component component, int colSpan,
                                       int rowSpan, double xweight,
                                       double yweight, Insets insets) {
        final GridBagConstraints gbc = createGridBagConstraint(colSpan,
            rowSpan, xweight, yweight, insets);

        this.currentRowList.add(new Item(component, gbc));

        this.currentCol++;

        return this;
    }


    /**
     * Appends the given label to the end of the current line. The label does
     * not "grow."
     *
     * @param label the label to append
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendLabel(JLabel label) {
        return appendLabel(label, 1);
    }


    /**
     * Appends the given label to the end of the current line. The label does
     * not "grow."
     *
     * @param label   the label to append
     * @param colSpan the number of columns to span
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendLabel(JLabel label, int colSpan) {
        return append(label, colSpan, 1, false, false);
    }


    /**
     * Appends a right-justified label to the end of the given line, using the
     * provided string as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to use.
     *
     * @param labelKey the key into the message bundle; if not found the key is used
     *                 as the text to display
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendRightLabel(String labelKey) {
        return appendRightLabel(labelKey, 1);
    }


    /**
     * Appends a right-justified label to the end of the given line, using the
     * provided string as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to use.
     *
     * @param labelKey the key into the message bundle; if not found the key is used
     *                 as the text to display
     * @param colSpan  the number of columns to span
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendRightLabel(String labelKey, int colSpan) {
        final JLabel label = getComponentFactory().createLabel(labelKey);
        label.setHorizontalAlignment(JLabel.RIGHT);
        return appendLabel(label, colSpan);
    }


    /**
     * Appends a left-justified label to the end of the given line, using the
     * provided string as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to use.
     *
     * @param labelKey the key into the message bundle; if not found the key is used
     *                 as the text to display
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendLeftLabel(String labelKey) {
        return appendLeftLabel(labelKey, 1);
    }


    /**
     * Appends a left-justified label to the end of the given line, using the
     * provided string as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to use.
     *
     * @param labelKey the key into the message bundle; if not found the key is used
     *                 as the text to display
     * @param colSpan  the number of columns to span
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendLeftLabel(String labelKey, int colSpan) {
        final JLabel label = getComponentFactory().createLabel(labelKey);
        label.setHorizontalAlignment(JLabel.LEFT);
        return appendLabel(label, colSpan);
    }


    /**
     * Appends the given component to the end of the current line. The component
     * will "grow" horizontally as space allows.
     *
     * @param component the item to append
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendField(Component component) {
        return appendField(component, 1);
    }


    /**
     * Appends the given component to the end of the current line. The component
     * will "grow" horizontally as space allows.
     *
     * @param component the item to append
     * @param colSpan   the number of columns to span
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendField(Component component, int colSpan) {
        return append(component, colSpan, 1, true, false);
    }


    /**
     * Appends a seperator (usually a horizonal line). Has an implicit
     * {@link #nextLine()}before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendSeparator() {
        return appendSeparator(null);
    }


    /**
     * Appends a seperator (usually a horizonal line) using the provided string
     * as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to put along with the seperator. Has an implicit
     * {@link #nextLine()}before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder appendSeparator(String labelKey) {
        if (this.currentRowList.size() > 0) {
            nextLine();
        }
        final JComponent separator = getComponentFactory()
            .createLabeledSeparator(labelKey);
        return append(separator, 1, 1, true, false).nextLine();
    }


    /**
     * Ends the current line and starts a new one
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutBuilder nextLine() {
        if (currentRowList.size() == 0) {
            // accomidate trying to do an empty line
            append(new JPanel(), 1, 1, true, false);
        }

        rows.add(currentRowList);
        this.currentRowList = new ArrayList();
        this.currentCol = 0;
        return this;
    }


    private GridBagConstraints createGridBagConstraint(int colSpan,
                                                       int rowSpan,
                                                       double xweight,
                                                       double yweight,
                                                       Insets insets) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = this.currentCol;
        gbc.gridy = getCurrentRow();
        gbc.gridwidth = colSpan;
        gbc.gridheight = rowSpan;
        gbc.weightx = xweight;
        gbc.weighty = yweight;
        gbc.insets = insets;

        // in theory other ones can be used, but I've never seen why...
        gbc.fill = GridBagConstraints.BOTH;

        // keep track of the largest column this has seen...
        this.maxCol = Math.max(this.maxCol, this.currentCol);

        return gbc;
    }


    /**
     * Should this show "guidelines"? Useful for debugging layouts.
     */
    public void setShowGuidelines(boolean showGuidelines) {
        this.showGuidelines = showGuidelines;
    }


    /**
     * Creates and returns a JPanel with all the given components in it, using
     * the "hints" that were provided to the builder.
     *
     * @return a new JPanel with the components laid-out in it
     */
    public JPanel getPanel() {
        if (this.currentRowList.size() > 0) {
            this.rows.add(this.currentRowList);
        }

        final JPanel panel = this.showGuidelines ? new GridBagLayoutDebugPanel()
            : new JPanel(new GridBagLayout());

        final int lastRowIndex = this.rows.size() - 1;
        for (int currentRowIndex = 0; currentRowIndex <= lastRowIndex; currentRowIndex++) {
            final List row = (List)this.rows.get(currentRowIndex);
            addRow(row, currentRowIndex, lastRowIndex, panel);
        }
        return panel;
    }


    private void addRow(final List row, final int currentRowIndex,
                        final int lastRowIndex, final JPanel panel) {
        final int lastColIndex = row.size() - 1;

        for (int currentColIndex = 0; currentColIndex <= lastColIndex; currentColIndex++) {
            final Item item = (Item)row.get(currentColIndex);
            final GridBagConstraints gbc = item.gbc;

            if (currentRowIndex == lastRowIndex) {
                formatLastRow(gbc);
            }

            if (currentColIndex == lastColIndex) {
                formatLastColumn(gbc, currentColIndex);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding to panel: "
                    + getDebugString(item.component, gbc));
            }
            panel.add(item.component, gbc);
        }
    }


    private String getDebugString(Component component, GridBagConstraints gbc) {
        final StringBuffer buffer = new StringBuffer();

        if (component instanceof JComponent) {
            final JComponent jcomp = (JComponent)component;
            final String name = jcomp.getName();
            if (name != null && !"".equals(jcomp.getName())) {
                buffer.append(name);
            }
            else {
                if (jcomp instanceof JLabel) {
                    buffer.append(((JLabel)jcomp).getText());
                }
                else {
                    buffer.append(jcomp.toString());
                }
            }
        }
        else {
            buffer.append(component.toString());
        }

        buffer.append(", ");
        buffer.append("GridBagConstraint[");
        buffer.append("anchor=" + gbc.anchor).append(",");
        buffer.append("fill=").append(gbc.fill).append(",");
        buffer.append("gridheight=").append(gbc.gridheight).append(",");
        buffer.append("gridwidth=").append(gbc.gridwidth).append(",");
        buffer.append("gridx=").append(gbc.gridx).append(",");
        buffer.append("gridy=").append(gbc.gridy).append(",");
        buffer.append("weightx=").append(gbc.weightx).append(",");
        buffer.append("weighty=").append(gbc.weighty).append("]");
        return buffer.toString();
    }


    private void formatLastRow(final GridBagConstraints gbc) {
        // remove any insets at the bottom of the GBC
        final Insets oldInset = gbc.insets;
        gbc.insets =
            new Insets(oldInset.top, oldInset.left, 0, oldInset.right);
    }


    /**
     * Should the last column before a {@link #nextLine()}automaticly span to
     * the end of the panel?
     * <p />
     *
     * For example, if you have
     *
     * <pre>
     * append(a).append(b).append(c).nextLine();
     * append(d).append(e).nextLine();
     * </pre>
     *
     * then "e" would automaticly span two columns.
     *
     * @param autoSpanLastComponent default is true
     */
    public void setAutoSpanLastComponent(boolean autoSpanLastComponent) {
        this.autoSpanLastComponent = autoSpanLastComponent;
    }


    private void formatLastColumn(final GridBagConstraints gbc,
                                  final int currentColIndex) {
        // remove any insets at the right of the GBC
        final Insets oldInset = gbc.insets;
        gbc.insets =
            new Insets(oldInset.top, oldInset.left, oldInset.bottom, 0);

        if (this.autoSpanLastComponent) {
            // increase the gridwidth if needed
            final int colSpan = (this.maxCol - currentColIndex) + 1;
            if (colSpan > gbc.gridwidth) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Increasing gridwidth from " + gbc.gridwidth
                        + " to " + colSpan);
                }
                gbc.gridwidth = colSpan;
            }
        }
    }

    //*************************************************************************
    //
    // INNER CLASSES
    //
    //*************************************************************************

    private static final class Item {
        public Component component;

        public GridBagConstraints gbc;


        public Item(Component component, GridBagConstraints gbc) {
            this.component = component;
            this.gbc = gbc;
        }
    }

}
