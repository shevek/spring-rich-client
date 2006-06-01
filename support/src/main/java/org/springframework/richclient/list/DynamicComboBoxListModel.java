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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;

/**
 * A combobox whose contents are dynamically refreshable.
 * 
 * @author Keith Donald
 */
public class DynamicComboBoxListModel extends ComboBoxListModel implements PropertyChangeListener {
    
    private static final Log logger = LogFactory.getLog(DynamicComboBoxListModel.class);
    
    private final SelectedItemChangeHandler selectedItemChangeHandler = new SelectedItemChangeHandler();

    private final ValueModel selectedItemHolder;

    private ValueModel selectableItemsHolder;

    public DynamicComboBoxListModel(ValueModel selectedItemHolder) {
        this(selectedItemHolder, (ValueModel)null);
    }

    public DynamicComboBoxListModel(ValueModel selectedItemHolder, List items) {
        this(selectedItemHolder, new ValueHolder(items));
    }

    public DynamicComboBoxListModel(ValueModel selectedItemHolder, ValueModel selectableItemsHolder) {
        this.selectedItemHolder = selectedItemHolder;
        if (selectedItemHolder != null) {
            selectedItemHolder.addValueChangeListener(selectedItemChangeHandler);
        }
        setSelectableItemsHolder(selectableItemsHolder);
    }

    public void setSelectableItemsHolder(ValueModel holder) {
        if (this.selectableItemsHolder == holder) {
            return;
        }
        if (this.selectableItemsHolder != null) {
            holder.removeValueChangeListener(this);
        }
        this.selectableItemsHolder = holder;
        if (this.selectableItemsHolder != null) {
            doAdd(holder.getValue());
            this.selectableItemsHolder.addValueChangeListener(this);
        }
    }

    public Object getSelectedItem() {
        if (selectedItemHolder != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Returning selected item " + selectedItemHolder.getValue());
            }
            return selectedItemHolder.getValue();
        }

        return super.getSelectedItem();
    }

    public void setSelectedItem(Object selectedItem) {
        if (selectedItemHolder != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Setting newly selected item on value holder to " + selectedItem);
            }
            selectedItemHolder.setValue(selectedItem);
        }
        else {
            super.setSelectedItem(selectedItem);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (logger.isDebugEnabled()) {
            logger.debug("Backing collection of selectable items changed; "
                    + "refreshing combo box with contents of new collection.");
        }
        doAdd(selectableItemsHolder.getValue());
    }

    private void doAdd(Object items) {
        clear();
        if (items != null) {
            if (items instanceof Collection) {
                addAll((Collection)items);
            }
            else if (items instanceof Object[]) {
                Object[] itemsArray = (Object[])items;
                for (int i = 0; i < itemsArray.length; i++) {
                    add(itemsArray[i]);
                }
            }
            else {
                throw new IllegalArgumentException("selectableItemsHolder must hold a Collection or array");
            }
        }
        sort();
    }
    
    private class SelectedItemChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (logger.isDebugEnabled()) {
                logger.debug("Notifying combo box view selected value changed; new value is '"
                        + selectedItemHolder.getValue() + "'");
            }
            /*
            if (selectedItemHolder.getValue() == null) {
                if (size() > 0 && get(0) != null) {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Backing value model is null; Pre-setting initial value to first combo-box element "
                                        + get(0));
                    }
                    setSelectedItem(get(0));
                }
            }
            else {
            */
                if (logger.isDebugEnabled()) {
                    logger.debug("Firing contents change event; selected item may have changed");
                }
                fireContentsChanged(this, -1, -1);
                if (logger.isDebugEnabled()) {
                    logger.debug("Fired contents change event!");
                }
            //}
        }
    }

}