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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;

import javax.swing.DefaultButtonModel;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;

final class SelectableButtonValueModel extends DefaultButtonModel implements ValueChangeListener {
	private ValueModel valueModel;

	public SelectableButtonValueModel(ValueModel valueModel) {
		this.valueModel = valueModel;
		this.valueModel.addValueChangeListener(this);
	}

	public void valueChanged() {
		fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, this.isSelected() ? ItemEvent.SELECTED
				: ItemEvent.DESELECTED));
	}

	public void setPressed(boolean b) {
		if ((isPressed() == b) || !isEnabled()) {
			return;
		}

		if (b == false && isArmed()) {
			setSelected(!this.isSelected());
		}

		if (b) {
			stateMask |= PRESSED;
		}
		else {
			stateMask &= ~PRESSED;
		}

		fireStateChanged();

		if (!isPressed() && isArmed()) {
			int modifiers = 0;
			AWTEvent currentEvent = EventQueue.getCurrentEvent();
			if (currentEvent instanceof InputEvent) {
				modifiers = ((InputEvent)currentEvent).getModifiers();
			}
			else if (currentEvent instanceof ActionEvent) {
				modifiers = ((ActionEvent)currentEvent).getModifiers();
			}
			fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), EventQueue
					.getMostRecentEventTime(), modifiers));
		}
	}

	public boolean isSelected() {
		Boolean selected = (Boolean)valueModel.getValue();
		if (selected == null) {
			return false;
		}
		return selected.booleanValue();
	}

	public void setSelected(boolean b) {
		if (isSelected() == b) {
			return;
		}

		if (b) {
			stateMask |= SELECTED;
		}
		else {
			stateMask &= ~SELECTED;
		}

		fireStateChanged();

		if (b) {
			valueModel.setValue(Boolean.TRUE);
		}
		else {
			valueModel.setValue(Boolean.FALSE);
		}

	}
}

