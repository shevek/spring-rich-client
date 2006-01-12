/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import javax.swing.JComboBox;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;

public class ComboBoxBindingTests extends AbstractBindingTests {

    private ValueModel sih;

    private ComboBoxBinding cbb;

    private JComboBox cb;

    protected String setUpBinding() {
        cbb = new ComboBoxBinding(fm, "simpleProperty");
        cb = (JComboBox)cbb.getControl();
        sih = new ValueHolder(new Object[] {"0", "1", "2", "3", "4"});
        cbb.setSelectableItemsHolder(sih);
        return "simpleProperty";
    }

    public void testValueModelUpdatesComponent() {
        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());

        vm.setValue("1");
        assertEquals("1", cb.getSelectedItem());
        assertEquals(1, cb.getSelectedIndex());

        vm.setValue("2");
        assertEquals("2", cb.getSelectedItem());
        assertEquals(2, cb.getSelectedIndex());

        vm.setValue(null);
        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());
    }

    public void testComponentUpdatesValueModel() {
        cb.setSelectedIndex(1);
        assertEquals("1", vm.getValue());

        cb.setSelectedItem("2");
        assertEquals("2", vm.getValue());

        cb.setSelectedIndex(-1);
        assertEquals(null, vm.getValue());
    }

    public void testSelectableValueChangeUpdatesComboBoxModel() {
        assertEquals("0", cb.getModel().getElementAt(0));

        sih.setValue(new Object[] {"1"});
        assertEquals("1", cb.getModel().getElementAt(0));
    }

    public void testComponentTracksEnabledChanges() {
        assertTrue(cb.isEnabled());

        fm.getPropertyMetadata("simpleProperty").setEnabled(false);
        assertFalse(cb.isEnabled());

        fm.getPropertyMetadata("simpleProperty").setEnabled(true);
        assertTrue(cb.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        assertTrue(cb.isEnabled());

        fm.getPropertyMetadata("simpleProperty").setReadOnly(true);
        assertFalse(cb.isEnabled());

        fm.getPropertyMetadata("simpleProperty").setReadOnly(false);
        assertTrue(cb.isEnabled());
    }
}