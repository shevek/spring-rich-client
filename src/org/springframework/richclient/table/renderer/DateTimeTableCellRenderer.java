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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JTable;

/**
 * Renders a date/time in the standard format.
 * 
 * @author Keith Donald
 */
public class DateTimeTableCellRenderer extends OptimizedTableCellRenderer {
    private DateFormat dateFormatter;

    public DateTimeTableCellRenderer() {
        this.dateFormatter = new SimpleDateFormat("EEE M/d/yyyy H:mm:ss");
    }

    public DateTimeTableCellRenderer(DateFormat formatter) {
        this.dateFormatter = formatter;
    }

    public DateTimeTableCellRenderer(TimeZone timeZone) {
        this();
        this.dateFormatter.setTimeZone(timeZone);
    }

    public DateFormat getDateFormat() {
        return dateFormatter;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        doPrepareRenderer(table, isSelected, hasFocus, row, column);
        if (value != null) {
            Date date = (Date)value;
            setValue(dateFormatter.format(date));
        }
        else {
            setValue(null);
        }
        return this;
    }

    public void useGMTTime() {
        dateFormatter = new SimpleDateFormat("EEE M/d/yyyy H:mm:ss z");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void useLocalTime() {
        dateFormatter = new SimpleDateFormat("EEE M/d/yyyy H:mm:ss");
    }
}