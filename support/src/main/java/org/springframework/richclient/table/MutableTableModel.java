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
 * A table model whose contents can change.
 * 
 * @author Keith Donald
 */
public interface MutableTableModel extends TableModel {
    
    /**
     * Adds the given row to the end of the list of existing rows in the table model. All 
     * registered table model listeners will be notified of the additional row.
     * 
     * @param row The row to be added. Must not be null.
     * 
     * @throws IllegalArgumentException if {@code row} is null.
     */
    public void addRow(Object row);

    /**
     * Adds the given rows to the end of the list of existing rows in the table model. All 
     * registered table model listeners will be notified of the additional rows.
     *
     * @param rows The rows to be added. May be empty but must not be null.
     * 
     * @throws IllegalArgumentException if {@code rows} is null.
     */
    public void addRows(List rows);

    /**
     * Removes from the table model the row at the given position in the list of rows. All 
     * registered table model listeners will be notified of the removal of the row. 
     *
     * @param index The zero-based position of the row to be removed.
     * 
     * @throws IndexOutOfBoundsException if {@code index} is not within the bounds of the 
     * model's collection of rows.
     */
    public void remove(int index);

    /**
     * Removes all of the rows from {@code firstIndex} to {@code lastIndex} inclusive. All 
     * registered table model listeners will be notified of the removal of the rows.
     *
     * @param firstIndex The zero-based position of the first row to be removed.
     * @param lastIndex The zero-based position of the last row to be removed.
     * 
     * @throws IllegalArgumentException if {@code lastIndex} is less than {@code firstIndex}.
     * @throws IndexOutOfBoundsException if either argument is outside the bounds of the 
     * number of rows in the model.
     */
    public void remove(int firstIndex, int lastIndex);

    /**
     * Removes the rows at each of the given positions. All registered table model listeners 
     * will be notified of the removal of the rows.
     *
     * @param indexes The array of zero-based indexes of the rows to be removed. May be empty
     * but must not be null.
     * 
     * @throws IllegalArgumentException if {@code indexes} is null.
     * @throws IndexOutOfBoundsException if any of the elements in the array are not within the
     * bounds of the model's collection of rows.
     */
    public void remove(int[] indexes);

    /**
     * Removes all rows from the table model. All registered table model listeners will be 
     * notified of the removal of the rows.
     */
    public void clear();
    
}
