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
package org.springframework.richclient.table.renderer;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author Keith Donald
 */
public class BeanTableCellRenderer extends OptimizedTableCellRenderer {
	private BeanWrapper beanWrapper;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value == null) {
			return this;
		}
		if (beanWrapper == null) {
			beanWrapper = new BeanWrapperImpl(value);
		}
		else {
			beanWrapper.setWrappedInstance(value);
		}
		try {
			BeanInfo info = Introspector.getBeanInfo(value.getClass());
			int index = info.getDefaultPropertyIndex();
			if (index != -1) {
				String defaultPropName = beanWrapper.getPropertyDescriptors()[index].getName();
				Object val = beanWrapper.getPropertyValue(defaultPropName);
				TableCellRenderer r = table.getDefaultRenderer(val.getClass());
				return r.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
			}
			else {
				setText(String.valueOf(value));
			}
		}
		catch (IntrospectionException e) {
			setText(String.valueOf(value));
		}
		return this;
	}

	/**
	 * Creates a ValuedEnumRenderer %DOC short caption%
	 * 
	 * %long description%.
	 *  
	 */
	public BeanTableCellRenderer() {
		super();
	}

}