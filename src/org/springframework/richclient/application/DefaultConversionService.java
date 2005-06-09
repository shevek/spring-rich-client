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
package org.springframework.richclient.application;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.binding.convert.Converter;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.FormatterLocator;
import org.springframework.util.StringUtils;

public class DefaultConversionService extends org.springframework.binding.convert.support.DefaultConversionService {

    public DefaultConversionService() {
        super(new Converter[] {});
    }

    protected void addDefaultConverters() {
        addConverter(new TextToDate(getFormatterLocator(), true));
        addConverter(new DateToText(getFormatterLocator(), true));
        addConverter(new TextToNumber(getFormatterLocator(), true));
        addConverter(new NumberToText(getFormatterLocator(), true));
        addConverter(new BooleanToText());
        addConverter(new TextToBoolean());
    }

    public static final class TextToDate extends AbstractFormattingConverter {

        private final boolean allowEmpty;

        protected TextToDate(FormatterLocator formatterLocator, boolean allowEmpty) {
            super(formatterLocator);
            this.allowEmpty = allowEmpty;
        }

        public Class[] getSourceClasses() {
            return new Class[] {String.class};
        }

        public Class[] getTargetClasses() {
            return new Class[] {Date.class};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            return (!allowEmpty || StringUtils.hasText((String)source)) ? getFormatterLocator().getDateTimeFormatter()
                    .parseValue((String)source) : null;
        }
    }

    public static final class DateToText extends AbstractFormattingConverter {

        private final boolean allowEmpty;

        protected DateToText(FormatterLocator formatterLocator, boolean allowEmpty) {
            super(formatterLocator);
            this.allowEmpty = allowEmpty;
        }

        public Class[] getSourceClasses() {
            return new Class[] {Date.class};
        }

        public Class[] getTargetClasses() {
            return new Class[] {String.class};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            return (!allowEmpty || source != null) ? getFormatterLocator().getDateTimeFormatter().formatValue(
                    (Date)source) : "";
        }
    }

    public static final class TextToNumber extends AbstractFormattingConverter {

        private final boolean allowEmpty;

        protected TextToNumber(FormatterLocator formatterLocator, boolean allowEmpty) {
            super(formatterLocator);
            this.allowEmpty = allowEmpty;
        }

        public Class[] getSourceClasses() {
            return new Class[] {String.class};
        }

        public Class[] getTargetClasses() {
            return new Class[] {Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
                    BigInteger.class, BigDecimal.class,};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            return (!allowEmpty || StringUtils.hasText((String)source)) ? getFormatterLocator().getNumberFormatter(
                    targetClass).parseValue((String)source) : null;
        }
    }

    public static final class NumberToText extends AbstractFormattingConverter {

        private final boolean allowEmpty;

        protected NumberToText(FormatterLocator formatterLocator, boolean allowEmpty) {
            super(formatterLocator);
            this.allowEmpty = allowEmpty;
        }

        public Class[] getSourceClasses() {
            return new Class[] {Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
                    BigInteger.class, BigDecimal.class,};
        }

        public Class[] getTargetClasses() {
            return new Class[] {String.class};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            return (!allowEmpty || source != null) ? getFormatterLocator().getNumberFormatter(source.getClass())
                    .formatValue(source) : "";
        }
    }

    public static final class TextToBoolean extends AbstractConverter {

        public static final String VALUE_TRUE = "true";

        public static final String VALUE_FALSE = "false";

        public static final String VALUE_ON = "on";

        public static final String VALUE_OFF = "off";

        public static final String VALUE_YES = "yes";

        public static final String VALUE_NO = "no";

        public static final String VALUE_1 = "1";

        public static final String VALUE_0 = "0";

        private String trueString;

        private String falseString;

        public TextToBoolean() {
        }

        public TextToBoolean(String trueString, String falseString) {
            this.trueString = trueString;
            this.falseString = falseString;
        }

        public Class[] getSourceClasses() {
            return new Class[] {String.class};
        }

        public Class[] getTargetClasses() {
            return new Class[] {Boolean.class};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            String text = (String)source;
            if (!StringUtils.hasText(text)) {
                return null;
            }
            else if (this.trueString != null && text.equalsIgnoreCase(this.trueString)) {
                return Boolean.TRUE;
            }
            else if (this.falseString != null && text.equalsIgnoreCase(this.falseString)) {
                return Boolean.FALSE;
            }
            else if (this.trueString == null
                    && (text.equalsIgnoreCase(VALUE_TRUE) || text.equalsIgnoreCase(VALUE_ON)
                            || text.equalsIgnoreCase(VALUE_YES) || text.equals(VALUE_1))) {
                return Boolean.TRUE;
            }
            else if (this.falseString == null
                    && (text.equalsIgnoreCase(VALUE_FALSE) || text.equalsIgnoreCase(VALUE_OFF)
                            || text.equalsIgnoreCase(VALUE_NO) || text.equals(VALUE_0))) {
                return Boolean.FALSE;
            }
            else {
                throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
            }
        }
    }

    public static final class BooleanToText extends AbstractConverter {

        public static final String VALUE_YES = "yes";

        public static final String VALUE_NO = "no";

        private String trueString;

        private String falseString;

        public BooleanToText() {
        }

        public BooleanToText(String trueString, String falseString) {
            this.trueString = trueString;
            this.falseString = falseString;
        }

        public Class[] getSourceClasses() {
            return new Class[] {Boolean.class};
        }

        public Class[] getTargetClasses() {
            return new Class[] {String.class};
        }

        protected Object doConvert(Object source, Class targetClass) throws Exception {
            Boolean bool = (Boolean)source;
            if (this.trueString != null && bool.booleanValue()) {
                return trueString;
            }
            else if (this.falseString != null && !bool.booleanValue()) {
                return falseString;
            }
            else if (bool.booleanValue()) {
                return VALUE_YES;
            }
            else {
                return VALUE_NO;
            }
        }
    }

}