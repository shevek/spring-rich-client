/*
 * Copyright 2005 the original author or authors.
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
package org.springframework.richclient.form.binding.jide;

import com.jidesoft.swing.CheckBoxListWithSelectable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.swing.AbstractListBinding;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A binding for the JIDE check box list with selectable component.
 * 
 * This is a copy, paste and modification from the Spring RCP ListBinding class.
 * 
 * @author Jonny Wray
 * 
 */
public class CheckBoxListSelectableBinding extends AbstractListBinding {
	private static final Log logger = LogFactory.getLog(CheckBoxListSelectableBinding.class);
	private static final Object[] EMPTY_VALUES = new Object[0];

	private final PropertyChangeListener valueModelListener = new ValueModelListener();
	private final ItemListener checkBoxListener = new CheckBoxListener();

	private ConversionExecutor conversionExecutor;

	boolean selectingValues;

	public CheckBoxListSelectableBinding(CheckBoxListWithSelectable list,
			FormModel formModel, String formFieldPath, Class requiredSourceClass) {
		super(list, formModel, formFieldPath, requiredSourceClass);
	}

	public CheckBoxListWithSelectable getList() {
		return (CheckBoxListWithSelectable) getComponent();
	}

	public void setSelectionMode(int selectionMode) {
		Assert.isTrue(ListSelectionModel.SINGLE_SELECTION == selectionMode
				|| isPropertyConversionExecutorAvailable());
		getList().setSelectionMode(selectionMode);
	}

	public int getSelectionMode() {
		return getList().getSelectionMode();
	}

	/**
	 * Returns a conversion executor which converts a value of the given
	 * sourceType into the fieldType
	 * 
	 * @see #getPropertyType()
	 */
	protected ConversionExecutor getPropertyConversionExecutor() {
		if (conversionExecutor == null) {
			conversionExecutor = getConversionService().getConversionExecutor(
					Object[].class, getPropertyType());
		}
		return conversionExecutor;
	}

	protected boolean isPropertyConversionExecutorAvailable() {
		try {
			getConversionService().getConversionExecutor(Object[].class,
					getPropertyType());
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	protected void updateSelectedItemsFromSelectionModel() {
		if (getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
			// JIDE CheckBoxList difference: get selected from CheckBoxList
			// method
			Object singleValue = getList().getSelectedObjects()[0];
			Class propertyType = getPropertyType();
			if (singleValue == null
					|| propertyType.isAssignableFrom(singleValue.getClass())) {
				getValueModel().setValueSilently(singleValue,
						valueModelListener);
			} else {
				getValueModel().setValueSilently(
						convertValue(singleValue, propertyType),
						valueModelListener);
			}
		} else {
			// JIDE CheckBoxList difference: get selected from CheckBoxList
			// method
			Object[] values = getList().getSelectedObjects();
			getValueModel().setValueSilently(convertSelectedValues(values),
					valueModelListener);
		}
	}

	/**
	 * Converts the given values into a type that matches the fieldType
	 * 
	 * @param selectedValues
	 *            the selected values
	 * @return the value which can be assigned to the type of the field
	 */
	protected Object convertSelectedValues(Object[] selectedValues) {
		return getPropertyConversionExecutor().execute(selectedValues);
	}

	protected void doBindControl(ListModel bindingModel) {
		CheckBoxListWithSelectable list = getList();
		list.setModel(bindingModel);
		// JIDE CheckBoxListdifference: check box listener rather than list
		// selection
		list.addItemListener(checkBoxListener);
		getValueModel().addValueChangeListener(valueModelListener);
		if (!isPropertyConversionExecutorAvailable()
				&& getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
			logger.warn("Selection mode for list field "
								+ getProperty()
								+ " forced to single selection."
								+ " If multiple selection is needed use a collection type (List, Collection, Object[])"
								+ " or provide a suitable converter to convert Object[] instances to property type "
								+ getPropertyType());
			
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		updateSelectedItemsFromValueModel();
	}

	/**
	 * Updates the selection model with the selected values from the value
	 * model.
	 */
	protected void updateSelectedItemsFromValueModel() {
		Object value = getValue();
		Object[] selectedValues = EMPTY_VALUES;
		if (value != null) {
			selectedValues = (Object[]) convertValue(value, Object[].class);
		}

		// flag is used to avoid a round trip while we are selecting the values
		selectingValues = true;
		try {
			ListSelectionModel selectionModel = getList().getSelectionModel();
			selectionModel.setValueIsAdjusting(true);
			try {
				int[] valueIndexes = determineValueIndexes(selectedValues);
				int selectionMode = getSelectionMode();
				if (selectionMode == ListSelectionModel.SINGLE_SELECTION
						&& valueIndexes.length > 1) {
					getList().setSelectedIndex(valueIndexes[0]);
				} else {
					getList().setSelectedIndices(valueIndexes);
				}

				// update value model if selectedValues contain elements which
				// where not found in the list model
				// elements
				if (valueIndexes.length != selectedValues.length
						&& !isReadOnly()
						&& isEnabled()
						|| (selectionMode == ListSelectionModel.SINGLE_SELECTION && valueIndexes.length > 1)) {
					updateSelectedItemsFromSelectionModel();
				}
			} finally {
				selectionModel.setValueIsAdjusting(false);
			}
		} finally {
			selectingValues = false;
		}
	}

	/**
	 * @param values
	 * @return
	 */
	protected int[] determineValueIndexes(Object[] values) {
		int[] indexes = new int[values.length];
		if (values.length == 0)
			return indexes;

		Collection lookupValues = new HashSet(Arrays.asList(values));
		ListModel model = getList().getModel();
		int i = 0;
		for (int index = 0, size = model.getSize(); index < size
				&& !lookupValues.isEmpty(); index++) {
			if (lookupValues.remove(model.getElementAt(index))) {
				indexes[i++] = index;
			}
		}
		int[] result;
		if (i != values.length) {
			result = new int[i];
			System.arraycopy(indexes, 0, result, 0, i);
		} else {
			result = indexes;
		}
		return result;
	}

	public void setRenderer(ListCellRenderer renderer) {
		getList().setCellRenderer(renderer);
	}

	public ListCellRenderer getRenderer() {
		return getList().getCellRenderer();
	}

	// JIDE CheckBoxList difference. Change based on check boxes changing, not
	// list selection
	private class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (!selectingValues) {
				updateSelectedItemsFromSelectionModel();
			}
		}
	}

	protected class ValueModelListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			updateSelectedItemsFromValueModel();
		}

	}

	protected ListModel getDefaultModel() {
		return getList().getModel();
	}
}