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
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.springframework.context.MessageSource;
import org.springframework.enums.CodedEnum;

/**
 * @author Keith Donald
 */
public class CodedEnumTableCellRenderer extends OptimizedTableCellRenderer implements TableCellRenderer {
	private MessageSource messages;

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value == null) {
			return this;
		}
		if (messages != null) {
			setText(messages.getMessage((CodedEnum)value, Locale.getDefault()));
		}
		else {
			setText(((CodedEnum)value).getLabel());
		}
		return this;
	}

}