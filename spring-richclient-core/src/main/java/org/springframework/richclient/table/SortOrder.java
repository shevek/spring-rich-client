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
package org.springframework.richclient.table;

/**
 * @author Keith Donald
 */
public class SortOrder {
    public static final SortOrder ASCENDING = new SortOrder(0);

    public static final SortOrder DESCENDING = new SortOrder(1);

    private int value;

    private SortOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public SortOrder flip() {
        if (this == SortOrder.ASCENDING)
            return SortOrder.DESCENDING;

        return SortOrder.ASCENDING;
    }

    public String toString() {
        return new StringBuffer().append(value).toString();
    }
}