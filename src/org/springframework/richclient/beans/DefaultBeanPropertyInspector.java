/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.beans;

import java.awt.Component;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.springframework.enum.CodedEnum;
import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.renderers.DateTimeTableCellRenderer;
import org.springframework.richclient.table.renderers.OptimizedTableCellRenderer;
import org.springframework.richclient.treetable.JTreeTable;

/**
 * A base implementation of a BeanPropertyInspector implemented as a tree table.
 * 
 * @author Keith Donald
 * @see BeanPropertyInspector
 */
public class DefaultBeanPropertyInspector implements BeanPropertyInspector {
    private Object bean;

    private JTreeTable beanInspector;

    private BeanInspectorTreeModel beanInspectorModel;

    public DefaultBeanPropertyInspector() {

    }

    /**
     * @see org.springframework.richclient.beans.BeanPropertyInspector#getBeanClassName()
     */
    public String getBeanClassName() {
        return bean.getClass().getName();
    }

    /**
     * @see org.springframework.richclient.beans.BeanPropertyInspector#setBean(java.lang.Object)
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    public JComponent getControl() {
        if (beanInspector == null) {
            initializeTreeTable();
        }
        return beanInspector;
    }

    private void initializeTreeTable() {
        beanInspectorModel = new BeanInspectorTreeModel(bean);
        beanInspector = new JTreeTable(beanInspectorModel);
        TableUtils.installDefaultRenderers(beanInspector);
        //@REFACTOR
        DateTimeTableCellRenderer r = new DateTimeTableCellRenderer(TimeZone
                .getTimeZone("GMT"));
        beanInspector.setDefaultRenderer(Date.class, r);
        TableColumn col = beanInspector.getColumnModel().getColumn(1);
        col.setCellRenderer(new BeanValueCellRenderer());
        JTree tree = beanInspector.getTree();
        tree.setCellRenderer(new BeanInspectorRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
    }

    class BeanValueCellRenderer extends OptimizedTableCellRenderer {
        /**
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
         *      java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            TableCellRenderer r = null;
            if (value != null) {
                r = beanInspector.getDefaultRenderer(value.getClass());
            }
            if (r == null) {
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (value != null) {
                    if (CodedEnum.class.isAssignableFrom(value.getClass())) {
                        setText(((CodedEnum)value).getLabel());
                    }
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
            else {
                Component c = r.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        }

    }
}