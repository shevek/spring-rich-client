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
package org.springframework.binding.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import junit.framework.TestCase;

import org.springframework.binding.value.support.ValueHolder;

/**
 * Testcase for ComboBoxModelAdapter
 * 
 * @author Peter De Bruycker
 */
public class ComboBoxModelAdapterTests extends TestCase {

    private SelectableItemsListModel listModel;

    private ValueHolder selectionHolder;

    private class StringHolder {

        public String value;
    }

    protected void setUp() throws Exception {
        selectionHolder = new ValueHolder();

        listModel = new SelectableItemsListModel(new String[] { "item0", "item1", "item2", "item3", "item4" },
                selectionHolder);
    }

    public void testSelectionChangeThroughValueModel() {
        final ComboBoxModelAdapter adapter = new ComboBoxModelAdapter(listModel);
        JComboBox comboBox = new JComboBox(adapter);

        final StringHolder holder = new StringHolder();

        comboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                assertEquals("selectedItem incorrect", holder.value, adapter.getSelectedItem());
            }
        });

        // Test selection holder
        holder.value = "item3";
        selectionHolder.setValue("item3");
    }
    
    public void testSelectionChangeThroughAdapter() {
        ComboBoxModelAdapter adapter = new ComboBoxModelAdapter(listModel);
        // try with index
        listModel.setSelectionIndex(1);
        assertEquals("selectedItem incorrect", "item1", adapter.getSelectedItem());
        
        // with item
        listModel.setSelection("item3");
        assertEquals("selectedItem incorrect", "item3", adapter.getSelectedItem());
    }

}