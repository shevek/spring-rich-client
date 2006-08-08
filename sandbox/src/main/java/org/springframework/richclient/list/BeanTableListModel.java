/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.richclient.list;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.richclient.table.BeanTableModel;

/**
 * {@link TableListModel} implemetation for pojo based lists
 * 
 * @author Mathias Broekelmann
 * 
 */
public abstract class BeanTableListModel extends BeanTableModel implements TableListModel {

    public BeanTableListModel(Class beanClass, List rows, MessageSource messages) {
        super(beanClass, rows, messages);
    }

    public BeanTableListModel(Class beanClass, List rows) {
        super(beanClass, rows);
    }

    public BeanTableListModel(Class beanClass, MessageSource messages) {
        super(beanClass, messages);
    }

    public BeanTableListModel(Class beanClass) {
        super(beanClass);
    }

}
