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

import java.util.ArrayList;
import java.util.List;

public class TableDataProvider {
    private List currentTableData = new ArrayList();

    private List lastTableData = new ArrayList();

    public TableDataProvider() {
    }

    public synchronized void put(Object datum) {
        currentTableData.add(datum);
    }

    /**
     * Returns the current set of changes to be published. This changes the List
     * that changes are added to. It is assumed consumers will have cleared out
     * the List when done with it.
     */
    public synchronized List takeData() {
        List list = currentTableData;
        currentTableData = lastTableData;
        return list;
    }
}