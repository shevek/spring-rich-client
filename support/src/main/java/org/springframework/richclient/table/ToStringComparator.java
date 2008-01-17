/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.table;

import java.util.Comparator;

/**
 * {@link Comparator} implementation that compares the {@link #toString()} representation of the argument.
 *  
 * @author Peter De Bruycker
 */
public class ToStringComparator implements Comparator {

    public static final ToStringComparator INSTANCE = new ToStringComparator();
    
    public int compare(Object o1, Object o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();
        
        return s1.compareTo(s2);
    }
    
}
