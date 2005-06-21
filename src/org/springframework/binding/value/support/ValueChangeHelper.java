/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.value.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ValueChangeHelper {
    /*
     * All the classes that are known to have a safe implementation of equals.
     */
    private static final Set classesWithSafeEquals = new HashSet(Arrays.asList(new Class[] {Boolean.class, Byte.class,
            Short.class, Integer.class, Long.class, Float.class, Double.class, String.class, Character.class,
            BigDecimal.class, BigInteger.class, Date.class, Calendar.class}));

    /**
     * Determines if there has been a change in value between the provided arguments. 
     * As many objects do not implement #equals in a manner that is strict enough for
     * the requirements of this class, difference is determined using <code>!=</code>, 
     * however, to improve accuracy #equals will be used when this is definitely 
     * safe e.g. for Strings, Booleans, Numbers. 
     * 
     * @param oldValue the value before the change
     * @param newValue the value after the change
     * @return true if the values have changed
     */
    public static boolean hasValueChanged(Object oldValue, Object newValue) {
        if (oldValue != null && classesWithSafeEquals.contains(oldValue.getClass())) {
            return !oldValue.equals(newValue);
        }
        else {
            return oldValue != newValue;
        }
    }
}
