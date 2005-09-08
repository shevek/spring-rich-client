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

import java.util.Comparator;
import java.util.HashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * <code>TableModel</code> that accepts a <code>EventList</code>.
 * <p>
 * By default, a {@link WritableTableFormat} will be generated for this model.
 * If you want to change this, you can override the {@link #createTableFormat()}
 * method to provide your own format.  In addition, an implementation of an
 * {@link AdvancedTableFormat} is provided for use.  It allows for the specification
 * of an object prototype (for determining column classes) and the ability to specify
 * comparators per column for sorting support.
 * 
 * @author Peter De Bruycker
 */
public class GlazedTableModel extends EventTableModel {

    private static final EventList EMPTY_LIST = new BasicEventList();

    private final BeanWrapper beanWrapper = new BeanWrapperImpl();

    private String columnLabels[];

    private MessageSourceAccessor messages;

    private final String columnPropertyNames[];

    public GlazedTableModel(String[] columnPropertyNames) {
        this((MessageSource)null, columnPropertyNames);
    }

    public GlazedTableModel(Class beanClass, EventList rows, String[] columnPropertyNames) {
        this(rows, null, columnPropertyNames);
    }

    public GlazedTableModel(EventList rows, MessageSource messageSource, String[] columnPropertyNames) {
        super(rows, null);
        Assert.notEmpty(columnPropertyNames, "ColumnPropertyNames parameter cannot be null.");
        this.columnPropertyNames = columnPropertyNames;
        setMessageSource(messageSource);
        columnLabels = createColumnNames(columnPropertyNames);
        setTableFormat(createTableFormat());
    }

    public GlazedTableModel(MessageSource messageSource, String[] columnPropertyNames) {
        this(EMPTY_LIST, messageSource, columnPropertyNames);
    }

    public void setMessageSource(MessageSource messages) {
        if (messages != null) {
            this.setMessages(new MessageSourceAccessor(messages));
        }
        else {
            this.setMessages(null);
        }
    }
    
    protected void setMessages(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    protected MessageSourceAccessor getMessages() {
        if (messages == null) {
            messages = Application.services().getMessages();
        }
        return messages;
    }

    protected Object getColumnValue(Object row, int column) {
        beanWrapper.setWrappedInstance(row);
        return beanWrapper.getPropertyValue(columnPropertyNames[column]);
    }

    protected String[] getColumnLabels() {
        return columnLabels;
    }

    protected String[] getColumnPropertyNames() {
        return columnPropertyNames;
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

    protected String[] createColumnNames(String[] propertyColumnNames) {
        String[] columnNames = new String[propertyColumnNames.length];
        Assert.notNull(getMessages());
        for (int i = 0; i < propertyColumnNames.length; i++) {
            final String columnPropertyName = propertyColumnNames[i];
            final String[] keys = {columnPropertyName + ".label", columnPropertyName};
            MessageSourceResolvable resolvable = new MessageSourceResolvable() {

                public String[] getCodes() {
                    return keys;
                }

                public Object[] getArguments() {
                    return null;
                }

                public String getDefaultMessage() {
                    return columnPropertyName;
                }
            };

            columnNames[i] = LabelInfoFactory.createLabelInfo(getMessages().getMessage(resolvable)).getText();
        }

        return columnNames;
    }

    protected TableFormat createTableFormat() {
        return new DefaultTableFormat();
    }

    /**
     * This inner class is the default TableFormat constructed.  In order to
     * extend this class you will also need to override
     * {@link GlazedTableModel#createTableFormat()} to instantiate an instance
     * of your derived table format.
     */
    protected class DefaultTableFormat implements WritableTableFormat {

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
    }
    
    /**
     * This inner class can be used by derived implementations to use an
     * AdvancedTableFormat instead of the default WritableTableFormat created by
     * {@link GlazedTableModel#createTableFormat()}.
     * <p>
     * If a prototype value is not provided (see {@link #setPrototypeValue(Object)}, then
     * the default implementation of getColumnClass will return Object.class, which is not
     * very usable. In that case, you should probably override
     * {@link #getColumnClass(int)}.
     * <p>
     * You can specify individual comparators for columns using
     * {@link #setComparator(int, Comparator)}. For any column that doesn't have a
     * comparator installed, a default comparable comparator will be handed out by
     * {@link #getColumnComparator(int)}.
     */
    protected class DefaultAdvancedTableFormat implements AdvancedTableFormat {

        public DefaultAdvancedTableFormat() {
        }

        public int getColumnCount() {
            return getColumnLabels().length;
        }

        public String getColumnName(int column) {
            return getColumnLabels()[column];
        }

        public Object getColumnValue(Object row, int column) {
            return GlazedTableModel.this.getColumnValue( row, column );
        }

        /**
         * Returns the class for all the cell values in the column. This is used by the
         * table to set up a default renderer and editor for the column. If a prototype
         * object has been specified, then the class will be obtained using introspection
         * using the property name associated with the specified column. If no prorotype
         * has been specified, then <code>Object.class</code> is returned.
         * 
         * @param column The index of the column being edited.
         */
        public Class getColumnClass(int column) {
            Class cls = Object.class;
            Integer columnKey = new Integer(column);
            if( _prototype != null ) {
                cls = (Class)_columnClasses.get(columnKey);
                if( cls == null ) {
                    cls = _beanWrapper.getPropertyType( getColumnPropertyNames()[column] );
                    _columnClasses.put(columnKey, cls);
                }
            }

            return cls;
        }

        /**
         * Get the comparator to use on values in the given column. If a comparator for
         * this column has been installed by calling
         * {@link #setComparator(int, Comparator)}, then it is returned. If not, then a
         * default comparator (assuming the objects implement Comparable) is returned.
         * 
         * @param column the column
         * @return the {@link Comparator} to use or <code>null</code> for an unsortable
         *         column.
         */
        public Comparator getColumnComparator(int column) {
            Comparator comparator = (Comparator)_comparators.get(new Integer(column));
            return comparator != null ? comparator : GlazedLists.comparableComparator();
        }

        /**
         * Set the comparator to use for a given column.
         * 
         * @param column The column for which the compartor is to be used
         * @param comparator The comparator to install
         */
        public void setComparator(int column, Comparator comparator) {
            _comparators.put(new Integer(column), comparator);
        }

        /**
         * Set the prototype value from which to determine column classes. If a prototype
         * value is not provided, then the default implementation of getColumnClass will
         * return Object.class, which is not very usable. If you don't provide a
         * prototype, you should probably override {@link #getColumnClass(int)}.
         */
        public void setPrototypeValue(Object prototype) {
            _prototype = prototype;
            _beanWrapper = new BeanWrapperImpl( _prototype );
        }

        private HashMap _comparators = new HashMap();
        private HashMap _columnClasses = new HashMap();
        private Object _prototype;
        private BeanWrapper _beanWrapper;
    }
}