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
package org.springframework.richclient.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A table cell renderer that has been optimized for performance
 * 
 * @author Keith Donald
 * <p>
 * XXX: please describe what is being optimized here and how it should be used.
 * 
 * @deprecated OptimizedTableCellRenderer messes up cell rendering see
 *             {@linkplain http://opensource.atlassian.com/projects/spring/browse/RCP-354}
 * 
 */
public class OptimizedTableCellRenderer extends DefaultTableCellRenderer {
    protected Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

    protected Color background = UIManager.getColor("Table.focusCellBackground");

    protected Color foreground = UIManager.getColor("Table.focusCellForeground");

    protected Color editableForeground;

    protected Color editableBackground;

    protected void doPrepareRenderer(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
    	setFont(table.getFont());
    	if (hasFocus) {
    	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
    	    if (table.isCellEditable(row, column)) {
    	        super.setForeground( UIManager.getColor("Table.focusCellForeground") );
    	        super.setBackground( UIManager.getColor("Table.focusCellBackground") );
    	    }
    	} else {
    	    setBorder(noFocusBorder);
    	}
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        doPrepareRenderer(table, isSelected, hasFocus, row, column);
        setValue(value);
        return this;
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // As long as you don't have any HTML text, this override is ok.
    }

    // This override is only appropriate if this will never contain any
    // children AND the Graphics is not clobbered during painting.
    public void paint(Graphics g) {
        ui.update(g, this);
    }

    public void setBackground(Color c) {
        this.background = c;
    }

    public Color getBackground() {
        return background;
    }

    public void setForeground(Color c) {
        this.foreground = c;
    }

    public Color getForeground() {
        return foreground;
    }

    public boolean isOpaque() {
        return (background != null);
    }

    // This is generally ok for non-Composite components (like Labels)
    public void invalidate() {

    }

    // Can be ignored, we don't exist in the containment hierarchy.
    public void repaint() {

    }
}