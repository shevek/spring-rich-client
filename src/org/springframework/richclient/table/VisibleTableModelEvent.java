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

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 * VisibleTableModelEvent adds the method isVisible to test if the cell
 * identified by the event is visible.
 */
public class VisibleTableModelEvent extends TableModelEvent {
    private Point tmpPoint;

    // This implementation caches the information for one JTable, it is
    // certainly possible to cache it for more than one should
    // you have this need.
    private boolean valid;

    private int firstVisRow;

    private int lastVisRow;

    private int firstVisCol;

    private int lastVisCol;

    public VisibleTableModelEvent(TableModel source) {
        super(source, 0, 0, 0, UPDATE);
        tmpPoint = new Point();
    }

    /**
     * Resets the underlying fields of the TableModelEvent. This assumes no ONE
     * is going to cache the TableModelEvent.
     */
    public void set(int row, int col) {
        firstRow = row;
        lastRow = row;
        column = col;
    }

    /**
     * Invoked to indicate the visible rows/columns need to be recalculated
     * again.
     */
    public void reset() {
        valid = false;
    }

    public boolean isVisible(JTable table) {
        if (!valid) {
            // Determine the visible region of the table.
            Rectangle visRect = table.getVisibleRect();

            tmpPoint.x = visRect.x;
            tmpPoint.y = visRect.y;
            firstVisCol = table.columnAtPoint(tmpPoint);
            firstVisRow = table.rowAtPoint(tmpPoint);

            tmpPoint.x += visRect.width;
            tmpPoint.y += visRect.height;
            lastVisCol = table.columnAtPoint(tmpPoint);
            if (lastVisCol == -1) {
                lastVisCol = table.getColumnCount() - 1;
            }
            if ((lastVisRow = table.rowAtPoint(tmpPoint)) == -1) {
                lastVisRow = table.getRowCount();
            }
            valid = true;
        }
        return (firstRow >= firstVisRow && firstRow <= lastVisRow && column >= firstVisCol && column <= lastVisCol);
    }
}