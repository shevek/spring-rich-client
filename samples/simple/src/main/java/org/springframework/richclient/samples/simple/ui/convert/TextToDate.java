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

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.binding.format.support.DateFormatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Value converter to handle text to date. Both MM-dd-yyyy and MM/dd/YYYY are supported.
 * The year is required to be 4 digits to avoid dates in the wrong century. If the text
 * can not be converted using any of the supported formats, then a ParseException is
 * thrown.
 * <p>
 * This converter is wired into the application in the application context. Take a look at
 * the <code>conversionService</code> bean and the method invoking factory bean
 * following it.
 * 
 * @author Larry Streepy
 */
public class TextToDate extends AbstractConverter {

    /** Default date formats to use. */
    private DateFormatter dateFormat = new DateFormatter(new SimpleDateFormat("MM-dd-yyyy"));

    /** Pattern to verify date contains full 4 digit year. */
    private static final Pattern MDY_PATTERN = Pattern.compile("[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}");

    /**
     * Default constructor - use default date formats.
     */
    public TextToDate() {
    }

    /**
     * Convert the given source value to a Date using the configured formats. If all the
     * formats fail, a <code>ParseException</code> will be thrown.
     * 
     * @param source object to convert
     * @param targetClass (ignored)
     * @return converted Date
     * @throws Exception on parsing failure
     */
    protected Object doConvert( Object source, Class targetClass, ConversionContext context ) throws Exception {
        String src = (String) source;

        // If the user entered slashes, convert them to dashes
        if( src.indexOf('/') >= 0 ) {
            src = src.replace('/', '-');
        }

        Object value = null;
        if( StringUtils.hasText(src) ) {

            Matcher matcher = MDY_PATTERN.matcher(src);

            if( !matcher.matches() ) {
                throw new ParseException("Invalid date format: " + src, 0);
            }

            value = dateFormat.parseValue(src, Date.class);
        }
        return value;
    }

    /**
     * Our source class.
     */
    public Class[] getSourceClasses() {
        return new Class[] { String.class };
    }

    /**
     * Our target class.
     */
    public Class[] getTargetClasses() {
        return new Class[] { Date.class };
    }
}
