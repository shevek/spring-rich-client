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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.form.binding.Binding;

public class ComboBoxBindingTests extends BindingAbstractTests {

    private static final Object[] SELECTABLEITEMS = new Object[] { "0", "1", "2", "3", "4" };

    private ValueModel sih;

    private ComboBoxBinding cbb;

    private JComboBox cb;

    protected String setUpBinding() {
        cbb = new ComboBoxBinding(fm, "simpleProperty");
        cb = (JComboBox) cbb.getControl();
        sih = new ValueHolder(SELECTABLEITEMS);
        cbb.setSelectableItemsHolder(sih);
        return "simpleProperty";
    }

    public void testValueModelUpdatesComponent() {
        TestListDataListener tldl = new TestListDataListener();
        cb.getModel().addListDataListener(tldl);

        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());
        tldl.assertCalls(0);

        vm.setValue("1");
        assertEquals("1", cb.getSelectedItem());
        assertEquals(1, cb.getSelectedIndex());
        tldl.assertEvent(1, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue("2");
        assertEquals("2", cb.getSelectedItem());
        assertEquals(2, cb.getSelectedIndex());
        tldl.assertEvent(2, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue(null);
        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());
        tldl.assertEvent(3, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue(null);
        tldl.assertCalls(3);
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

        sih.setValue(new Object[] { "1" });
        assertEquals("1", cb.getModel().getElementAt(0));
    }

    public void testComponentTracksEnabledChanges() {
        assertTrue(cb.isEnabled());

        fm.getFieldMetadata("simpleProperty").setEnabled(false);
        assertFalse(cb.isEnabled());

        fm.getFieldMetadata("simpleProperty").setEnabled(true);
        assertTrue(cb.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        assertTrue(cb.isEnabled());

        fm.getFieldMetadata("simpleProperty").setReadOnly(true);
        assertFalse(cb.isEnabled());

        fm.getFieldMetadata("simpleProperty").setReadOnly(false);
        assertTrue(cb.isEnabled());
    }

    public void testSelectableItemHolderNullValue() {
        ComboBoxBinding binding = new ComboBoxBinding(fm, "simpleProperty");
        binding.getControl();
        ValueHolder valueHolder = new ValueHolder();
        binding.setSelectableItemsHolder(valueHolder);
        assertEquals(binding.getSelectableItems(), Collections.EMPTY_LIST);
    }

    public void testExistingModel() {
        JComboBox cb = new JComboBox(new DefaultComboBoxModel(new Object[] { "1", "2", "3" }));
        ComboBoxBinder binder = new ComboBoxBinder();
        Binding binding = binder.bind(cb, fm, "simpleProperty", Collections.EMPTY_MAP);
        assertEquals(3, ((JComboBox) binding.getControl()).getModel().getSize());
    }

    public void testFilter() {
        setUpBinding();
        Collection selectableItems = cbb.getSelectableItems();
        assertEquals(Arrays.asList(SELECTABLEITEMS), selectableItems);
        cbb.setFilter(new Constraint() {
            public boolean test(Object argument) {
                return "1".equals(argument) || "4".equals(argument);
            }
        });
        selectableItems = cbb.getSelectableItems();
        assertEquals(Arrays.asList(new Object[] { "1", "4" }), selectableItems);
    }

    public void testFilterWithContext() {
        ComboBoxBinder binder = new ComboBoxBinder();
        Map context = new HashMap();
        Constraint filter = new Constraint() {
            public boolean test(Object argument) {
                return true;
            }
        };
        context.put(ComboBoxBinder.FILTER_KEY, filter);
        ComboBoxBinding binding = (ComboBoxBinding) binder.bind(fm, "simpleProperty", context);
        assertEquals(filter, binding.getFilter());
    }
}