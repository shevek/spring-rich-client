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
package org.springframework.richclient.forms;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.richclient.list.ListListModel;

class BufferedListValueModel extends BufferedValueModel {
	private ListListModel itemsBuffer;

	private boolean updating;

	public BufferedListValueModel(ValueModel wrappedModel) {
		super(wrappedModel);
	}

	public Object getValue() {
		if (!hasChangeBuffered()) {
			super.setValue(internalGet());
		}
		return super.getValue();
	}

	/**
	 * Gets the list value associated with this value model, creating a list
	 * model buffer containing its contents, suitable for manipulation.
	 * 
	 * @return The list model buffer
	 */
	protected Object internalGet() {
		List itemsList = (List)getWrappedModel().getValue();
		if (this.itemsBuffer == null) {
			this.itemsBuffer = new ListListModel(itemsList);
			this.itemsBuffer.addListDataListener(new ListDataListener() {
				public void contentsChanged(ListDataEvent e) {
					fireValueChanged();
				}

				public void intervalAdded(ListDataEvent e) {
					fireValueChanged();
				}

				public void intervalRemoved(ListDataEvent e) {
					fireValueChanged();
				}
			});
		}
		else {
			try {
				updating = true;
				this.itemsBuffer.clear();
				if (itemsList != null) {
					this.itemsBuffer.addAll(itemsList);
				}
			}
			finally {
				updating = false;
			}
		}
		return this.itemsBuffer;
	}

	/**
	 * Overriden to make a defensive copy of the list model contents before
	 * setting. This is not needed if the target object makes the copy; but this
	 * method assumes users are lazy and often forget to do that.
	 */
	protected void doBufferedValueCommit(Object bufferedValue) {
		List list = (List)bufferedValue;
		getWrappedModel().setValue(new ArrayList(list));
	}

	protected void fireValueChanged() {
		if (!updating) {
			super.fireValueChanged();
		}
	}

	protected void onWrappedValueChanged() {
		if (!updating) {
			super.setValue(internalGet());
		}
	}

}