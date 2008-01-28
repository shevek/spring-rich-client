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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.richclient.text.TimeFormat;

/**
 * Formats a cell in d h m s
 * 
 * @author Keith Donald
 */
public class TimeTableCellRenderer extends DefaultTableCellRenderer {
    public TimeFormat formatter;

    public TimeTableCellRenderer() {
        formatter = TimeFormat.getDaysInstance();
    }

    public TimeTableCellRenderer(TimeFormat formatter) {
        this.formatter = formatter;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value != null && value instanceof Number) {
            value = formatter.format(((Number) value).longValue());
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}