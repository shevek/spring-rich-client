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

import javax.swing.ComboBoxModel;

import org.springframework.util.closure.Constraint;

/**
 * @author kdonald
 */
public class FilteredComboBoxListModel extends FilteredListModel implements ComboBoxModel {

	private boolean matchedSelected;

	public FilteredComboBoxListModel(ComboBoxModel filteredModel, Constraint filter) {
		super(filteredModel, filter);
	}

	protected ComboBoxModel getComboBoxModel() {
		return (ComboBoxModel)getFilteredModel();
	}

	protected void onMatchingElement(Object element) {
		if (element == getSelectedItem()) {
			matchedSelected = true;
		}
	}

	protected void postConstraintApplied() {
		if (!matchedSelected) {
			if (getSize() > 0) {
				setSelectedItem(getElementAt(0));
			}
			else {
				setSelectedItem(null);
			}
		}
		matchedSelected = false;
	}

	public Object getSelectedItem() {
		if (getSize() == 0) {
			return null;
		}
		return getComboBoxModel().getSelectedItem();
	}

	public void setSelectedItem(Object anItem) {
		getComboBoxModel().setSelectedItem(anItem);
	}

}