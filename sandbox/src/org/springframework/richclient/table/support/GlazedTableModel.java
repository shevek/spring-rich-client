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
package org.springframework.richclient.table.support;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableFormat;
import ca.odell.glazedlists.swing.WritableTableFormat;

/**
 * <code>TableModel</code> that accepts a <code>EventList</code>.
 * 
 * @author Peter De Bruycker
 */
public class GlazedTableModel extends EventTableModel {

    private static final EventList EMPTY_LIST = new BasicEventList();

    private BeanWrapper beanWrapper = new BeanWrapperImpl();

    private String columnLabels[];

    private MessageSourceAccessor messages;

    private String columnPropertyNames[];

    public GlazedTableModel(String[] columnPropertyNames) {
        this((MessageSource) null, columnPropertyNames);
    }

    public GlazedTableModel(Class beanClass, EventList rows, String[] columnPropertyNames) {
        this(rows, null, columnPropertyNames);
    }

    public GlazedTableModel(EventList rows, MessageSource messageSource, String[] columnPropertyNames) {
        super(rows, null);
        Assert.notEmpty(columnPropertyNames, "ColumnPropertyNames parameter cannot be null.");
        this.columnPropertyNames = columnPropertyNames;
        setMessageSource(messageSource);
        setTableFormat(createTableFormat());
    }

    public GlazedTableModel(MessageSource messageSource, String[] columnPropertyNames) {
        this(EMPTY_LIST, messageSource, columnPropertyNames);
    }

    public void setMessageSource(MessageSource messages) {
        if (messages != null) {
            this.messages = new MessageSourceAccessor(messages);
        } else {
            this.messages = null;
        }
    }

    protected Object getColumnValue(Object row, int column) {
        beanWrapper.setWrappedInstance(row);
        return beanWrapper.getPropertyValue(columnPropertyNames[column]);
    }

    /**
     * May be overridden to achieve control over editable columns.
     * 
     * @param row
     *            the current row
     * @param column
     *            the column
     * @return editable
     */
    protected boolean isEditable(Object row, int column) {
        beanWrapper.setWrappedInstance(row);

        return beanWrapper.isWritableProperty(columnPropertyNames[column]);
    }

    protected Object setColumnValue(Object row, Object value, int column) {
        beanWrapper.setWrappedInstance(row);
        beanWrapper.setPropertyValue(columnPropertyNames[column], value);

        return row;
    }

    private String[] createColumnNames(String[] propertyColumnNames) {
        String[] columnNames = new String[propertyColumnNames.length];
        Assert.notNull(this.messages);
        for (int i = 0; i < propertyColumnNames.length; i++) {
            String columnPropertyName = propertyColumnNames[i];

            final String[] keys = { "label." + columnPropertyName, columnPropertyName };

            MessageSourceResolvable resolvable = new MessageSourceResolvable() {

                public String[] getCodes() {
                    return keys;
                }

                public Object[] getArguments() {
                    return null;
                }

                public String getDefaultMessage() {
                    if (keys.length > 0) {
                        return keys[0];
                    }
                    return "";
                }
            };

            columnNames[i] = LabelInfoFactory.createLabelInfo(messages.getMessage(resolvable)).getText();
        }

        return columnNames;
    }

    private TableFormat createTableFormat() {
        columnLabels = createColumnNames(columnPropertyNames);
        return new WritableTableFormat() {

            public int getColumnCount() {
                return columnLabels.length;
            }

            public String getColumnName(int column) {
                return columnLabels[column];
            }

            public Object getColumnValue(Object row, int column) {
                return GlazedTableModel.this.getColumnValue(row, column);
            }

            public boolean isEditable(Object row, int column) {
                return GlazedTableModel.this.isEditable(row, column);
            }

            public Object setColumnValue(Object row, Object value, int column) {
                return GlazedTableModel.this.setColumnValue(row, value, column);
            }
        };
    }
}