/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.samples.simple.ui.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.support.DateFormatter;

/**
 * Convert a Date value to a String. This will determine how all Date fields are rendered
 * as Strings in the UI.
 * <p>
 * This converter is wired into the application in the application context. Take a look at
 * the <code>conversionService</code> bean and the method invoking factory bean
 * following it.
 * 
 * @author Larry Streepy
 * 
 */
public class DateToText extends AbstractFormattingConverter {

    /** Default Date format. */
    public static final DateFormatter DEFAULT_DATE_FORMAT = new DateFormatter(new SimpleDateFormat("MM-dd-yyyy"));

    private DateFormatter format;

    /** A static converter using the default date formatter. */
    public static final DateToText DATE_CONVERTER = new DateToText(DEFAULT_DATE_FORMAT);

    /**
     * Default constructor - use the default format.
     */
    public DateToText() {
        this(DEFAULT_DATE_FORMAT);
    }

    /**
     * Constructor using the specified format.
     * 
     * @param format Date format specification to use for formatting
     */
    public DateToText( DateFormatter format ) {
        super(null);
        this.format = format;
    }

    /**
     * Convert the Date to a String using the configured format.
     */
    protected Object doConvert( Object source, Class targetClass ) throws Exception {
        return (source == null) ? "" : format.formatValue(source);
    }

    /**
     * Our source class.
     */
    public Class[] getSourceClasses() {
        return new Class[] { Date.class };
    }

    /**
     * Our target class.
     */
    public Class[] getTargetClasses() {
        return new Class[] { String.class };
    }

}
