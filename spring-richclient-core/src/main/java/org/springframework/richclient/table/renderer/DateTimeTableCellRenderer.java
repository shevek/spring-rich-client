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

import java.text.DateFormat;
import java.util.TimeZone;

/**
 * Renders a date/time in the standard format.
 * 
 * @author Keith Donald
 * @author Mathias Broekelmann
 */
public class DateTimeTableCellRenderer extends FormatTableCellRenderer {

    public DateTimeTableCellRenderer() {
        super(DateFormat.getInstance());
    }

    public DateTimeTableCellRenderer(DateFormat formatter) {
        super(formatter);
    }

    public DateTimeTableCellRenderer(TimeZone timeZone) {
        this();
        getDateFormat().setTimeZone(timeZone);
    }

    public DateFormat getDateFormat() {
        return (DateFormat) getFormat();
    }

    public void useGMTTime() {
        getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void useLocalTime() {
        getDateFormat().setTimeZone(TimeZone.getDefault());
    }
}