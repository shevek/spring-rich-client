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
package org.springframework.richclient.util;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.springframework.util.Assert;

/**
 * Convenient superclass that encapsulates common rendering code for an object
 * applied to different controls.
 * 
 * @author Keith Donald
 */
public abstract class AbstractCellRenderer implements TreeCellRenderer, TableCellRenderer, ListCellRenderer {

    private TableCellRenderer tableCellRenderer;

    private ListCellRenderer listCellRenderer;

    private TreeCellRenderer treeCellRenderer;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        if (treeCellRenderer == null) {
            this.treeCellRenderer = createTreeCellRenderer();
            Assert.notNull(this.treeCellRenderer, "Cell renderer implementation has not been configured for trees?");
        }
        return treeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (tableCellRenderer == null) {
            this.tableCellRenderer = createTableCellRenderer();
            Assert.notNull(this.tableCellRenderer, "Cell renderer implementation has not been configured for tables?");
        }
        return tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (listCellRenderer == null) {
            this.listCellRenderer = createListCellRenderer();
            Assert.notNull(this.listCellRenderer, "Cell renderer has not been configured for lists?");
        }
        return listCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    protected ListCellRenderer createListCellRenderer() {
        return null;
    }

    protected TableCellRenderer createTableCellRenderer() {
        return null;
    }

    protected TreeCellRenderer createTreeCellRenderer() {
        return null;
    }

}