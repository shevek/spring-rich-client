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

import java.util.List;

import javax.swing.table.TableModel;

/**
 * @author Keith Donald
 */
public interface MutableTableModel extends TableModel {
    public void addRow(Object row);

    public void addRows(List rows);

    public void remove(int index);

    public void remove(int firstIndex, int lastIndex);

    public void remove(int[] indexes);

    public void clear();
}