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
 * @author Keith Donald
 */
public class TableRenderers {
    public static final DateTimeTableCellRenderer LOCAL_DATE = new DateTimeTableCellRenderer();

    private static final DateFormat GMT_DATE_FORMAT = DateFormat.getInstance();

    static {
        GMT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static final DateTimeTableCellRenderer GMT_DATE = new DateTimeTableCellRenderer(GMT_DATE_FORMAT);
}