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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.springframework.beans.BeanWrapperImpl;

/**
 * Renders a enumeration in a list.
 * 
 * @author Keith Donald
 */
public class BeanPropertyValueListRenderer extends DefaultListCellRenderer {
    private BeanWrapperImpl beanWrapper;
    private String propertyName;

    public BeanPropertyValueListRenderer(String propertyName) {
        this.propertyName = propertyName;
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, "", index, isSelected,
                cellHasFocus);
        if (value == null) {
            return this;
        }
        if (beanWrapper == null) {
            beanWrapper = new BeanWrapperImpl(value);
        } else {
            beanWrapper.setWrappedInstance(value);
        }
        setText(String.valueOf(beanWrapper.getPropertyValue(propertyName)));
        return this;
    }

}