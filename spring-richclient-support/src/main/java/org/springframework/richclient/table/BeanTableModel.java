/*
 * Copyright 2002-2007 the original author or authors.
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

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author Keith Donald
 */
public abstract class BeanTableModel extends BaseTableModel {
    private BeanWrapper beanWrapper = new BeanWrapperImpl();

    private Class beanClass;

    private String[] columnPropertyNames;

    private MessageSourceAccessor messages;

    public BeanTableModel(Class beanClass) {
        this(beanClass, (MessageSource)null);
    }

    public BeanTableModel(Class beanClass, List rows) {
        this(beanClass, rows, null);
    }

    public BeanTableModel(Class beanClass, MessageSource messages) {
        super();
        setBeanClass(beanClass);
        setMessageSource(messages);
    }

    public BeanTableModel(Class beanClass, List rows, MessageSource messages) {
        super(rows);
        setBeanClass(beanClass);
        setMessageSource(messages);
    }

    public void setBeanClass(Class clazz) {
        this.beanClass = clazz;
    }

    public void setMessageSource(MessageSource messages) {
        if (messages != null) {
            this.messages = new MessageSourceAccessor(messages);
            createColumnInfo();
        }
        else {
            this.messages = null;
        }
    }

    protected void createColumnInfo() {
        this.columnPropertyNames = createColumnPropertyNames();
        super.createColumnInfo();
    }

    protected abstract String[] createColumnPropertyNames();

    protected String[] createColumnNames() {
        String[] columnPropertyNames = getColumnPropertyNames();
        String[] columnNames = new String[columnPropertyNames.length];
        Assert.state(this.messages != null, "First set the MessageSource.");
        for (int i = 0; i < columnPropertyNames.length; i++) {
            String className = ClassUtils.getShortNameAsProperty(beanClass);
            String columnPropertyName = columnPropertyNames[i];
            try {
                columnNames[i] = messages.getMessage(className + "." + columnPropertyName);
            } catch(NoSuchMessageException e) {
                columnNames[i] = messages.getMessage(columnPropertyName, columnPropertyName);
            }            
        }
        return columnNames;
    }

    protected String[] getColumnPropertyNames() {
        return columnPropertyNames;
    }

    private String getColumnPropertyName(int index) {
        return columnPropertyNames[index];
    }

    protected Object getValueAtInternal(Object row, int columnIndex) {
        beanWrapper.setWrappedInstance(row);
        return beanWrapper.getPropertyValue(columnPropertyNames[columnIndex]);
    }

    protected boolean isCellEditableInternal(Object row, int columnIndex) {
        beanWrapper.setWrappedInstance(row);
        return beanWrapper.isWritableProperty(getColumnPropertyName(columnIndex));
    }

    protected void setValueAtInternal(Object value, Object bean, int columnIndex) {        
        beanWrapper.setWrappedInstance(bean);
        beanWrapper.setPropertyValue(getColumnPropertyName(columnIndex), value);        
    }
}