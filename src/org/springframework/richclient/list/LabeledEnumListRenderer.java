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
package org.springframework.richclient.list;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.enums.LabeledEnum;

/**
 * Renders a enumeration in a list.
 * 
 * @author Keith Donald
 */
public class LabeledEnumListRenderer extends DefaultListCellRenderer {
	private MessageSource messages;

	public LabeledEnumListRenderer() {

	}

	public LabeledEnumListRenderer(MessageSource messages) {
		this.messages = messages;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == null) {
			return this;
		}
		if (messages != null && value instanceof MessageSourceResolvable) {
			setText(messages.getMessage((MessageSourceResolvable)value, Locale.getDefault()));
		}
		else {
			setText(((LabeledEnum)value).getLabel());
		}
		return this;
	}

}