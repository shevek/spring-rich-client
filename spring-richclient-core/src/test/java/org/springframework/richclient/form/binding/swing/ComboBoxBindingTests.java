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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.rules.constraint.Constraint;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.comparator.ComparableComparator;

public class ComboBoxBindingTests extends BindingAbstractTests {

    private static final String[] SELECTABLEITEMS = new String[] { "0", "1", "2", "3", "4" };

    private ValueModel sih;

    private ComboBoxBinding cbb;

    private JComboBox cb;

    private TestDataListener testListener;

    protected String setUpBinding() {
        cbb = new ComboBoxBinding(fm, "simpleProperty");
        cb = (JComboBox) cbb.getControl();
        sih = new ValueHolder(SELECTABLEITEMS);
        cbb.setSelectableItems(sih);
        testListener = new TestDataListener();
        cb.getModel().addListDataListener(testListener);
        return "simpleProperty";
    }

    public void testWithListModel() throws Exception {
        DefaultListModel model = new DefaultListModel();
        model.addElement("1");
        model.addElement("2");
        model.addElement("3");
        model.addElement("4");
        cbb.setSelectableItems(model);
        cbb.doBindControl();
        ComboBoxModel cbmodel = cb.getModel();
        assertEquals(model.getSize(), cbmodel.getSize());
        for (int i = 0, size = model.size(); i < size; i++) {
            assertEquals(model.getElementAt(i), cbmodel.getElementAt(i));
        }
    }

    public void testWithList() throws Exception {
        List model = Arrays.asList(SELECTABLEITEMS);
        cbb.setSelectableItems(model);
        cbb.doBindControl();
        ComboBoxModel cbmodel = cb.getModel();
        assertEquals(model.size(), cbmodel.getSize());
        for (int i = 0, size = model.size(); i < size; i++) {
            assertEquals(model.get(i), cbmodel.getElementAt(i));
        }
    }

    public void testWithArray() throws Exception {
        Object[] model = SELECTABLEITEMS;
        cbb.setSelectableItems(model);
        cbb.doBindControl();
        ComboBoxModel cbmodel = cb.getModel();
        assertEquals(model.length, cbmodel.getSize());
        for (int i = 0, size = model.length; i < size; i++) {
            assertEquals(model[i], cbmodel.getElementAt(i));
        }
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

        cb.setSelectedItem(null);
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
        try {
            binding.setSelectableItems(valueHolder);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testExistingModel() {
        JComboBox cb = new JComboBox(new DefaultComboBoxModel(new Object[] { "1", "2", "3" }));
        ComboBoxBinder binder = new ComboBoxBinder();
        Binding binding = binder.bind(cb, fm, "simpleProperty", Collections.EMPTY_MAP);
        assertEquals(3, ((JComboBox) binding.getControl()).getModel().getSize());
    }

    public void testFilter() {
        setUpBinding();
        ListModel model = cb.getModel();
        cbb.setFilter(new Constraint() {
            public boolean test(Object argument) {
                return "1".equals(argument) || "4".equals(argument);
            }
        });
        assertEquals(2, model.getSize());
        assertEquals("1", model.getElementAt(0));
        assertEquals("4", model.getElementAt(1));
    }

    public void testUpdatingFilter() {
        setUpBinding();
        ListModel model = cb.getModel();
        TestConstraint testConstraint = new TestConstraint();
        cbb.setFilter(testConstraint);
        assertEquals(2, model.getSize());
        assertEquals("1", model.getElementAt(0));
        assertEquals("4", model.getElementAt(1));

        testConstraint.testCalled = 0;
        testConstraint.setFilterValues(new Object[] { "2" });
        // assertEquals(SELECTABLEITEMS.length, testConstraint.testCalled);
        assertEquals(testConstraint.filterValues.length, model.getSize());
        assertEquals("2", model.getElementAt(0));
    }

    public void testFilterWithContext() {
        ComboBoxBinder binder = new ComboBoxBinder();
        binder.setSelectableItems(SELECTABLEITEMS);
        Map context = new HashMap();
        Constraint filter = new Constraint() {
            public boolean test(Object argument) {
                return "1".equals(argument) || "4".equals(argument);
            }
        };
        context.put(ComboBoxBinder.FILTER_KEY, filter);
        ComboBoxBinding binding = (ComboBoxBinding) binder.bind(fm, "simpleProperty", context);
        ListModel bindingModel = binding.getBindingModel();
        assertEquals(filter, binding.getFilter());
        assertEquals(2, bindingModel.getSize());
        assertEquals("1", bindingModel.getElementAt(0));
        assertEquals("4", bindingModel.getElementAt(1));
    }

    public void testComparator() {
        ComboBoxBinder binder = new ComboBoxBinder();
        binder.setSelectableItems(new Object[] { "2", "4", "1", "2", "3" });
        binder.setComparator(new ComparableComparator());
        ComboBoxBinding binding = (ComboBoxBinding) binder.bind(fm, "simpleProperty", Collections.EMPTY_MAP);
        ListModel bindingModel = binding.getBindingModel();
        assertEquals(5, bindingModel.getSize());
        assertEquals("1", bindingModel.getElementAt(0));
        assertEquals("2", bindingModel.getElementAt(1));
        assertEquals("2", bindingModel.getElementAt(2));
        assertEquals("3", bindingModel.getElementAt(3));
        assertEquals("4", bindingModel.getElementAt(4));
    }

    public void testEmptySelectionValue() throws Exception {
        ComboBoxModel model = cb.getModel();
        int modelSize = model.getSize();
        testListener.contentsChanged = null;
        testListener.intervalAdded = null;
        testListener.intervalRemoved = null;
        String emptyValue = "select a Value";
        cbb.setEmptySelectionValue(emptyValue);
        assertEquals(modelSize + 1, model.getSize());
        assertEquals(emptyValue, model.getElementAt(0));
        assertNotNull(testListener.contentsChanged);
        assertNull(testListener.intervalAdded);
        assertNull(testListener.intervalRemoved);
        cb.setSelectedItem(SELECTABLEITEMS[0]);
        assertEquals(SELECTABLEITEMS[0], model.getSelectedItem());
        assertEquals(SELECTABLEITEMS[0], vm.getValue());
        cb.setSelectedItem(emptyValue);
        assertEquals(emptyValue, model.getSelectedItem());
        assertEquals(null, vm.getValue());
        cb.setSelectedItem(null);
        assertEquals(emptyValue, model.getSelectedItem());
        assertEquals(null, vm.getValue());
        
        cb.setSelectedItem(emptyValue);
        testListener.contentsChanged = null;
        testListener.intervalAdded = null;
        testListener.intervalRemoved = null;
        cbb.setEmptySelectionValue(null);
        assertNotNull(testListener.contentsChanged);
        assertNull(testListener.intervalAdded);
        assertNull(testListener.intervalRemoved);
        assertEquals(modelSize, model.getSize());
        assertEquals(SELECTABLEITEMS[0], model.getElementAt(0));
        assertNull(vm.getValue());
    }

    private static class TestConstraint extends Observable implements Constraint {
        Object[] filterValues = new Object[] { "1", "4" };

        int testCalled = 0;

        public boolean test(Object argument) {
            testCalled++;
            for (int i = 0; i < filterValues.length; i++) {
                if (filterValues[i].equals(argument))
                    return true;
            }
            return false;
        }

        public void setFilterValues(Object[] objects) {
            filterValues = objects;
            setChanged();
            notifyObservers();
        }
    }

    private class TestDataListener implements ListDataListener {

        private ListDataEvent contentsChanged;

        private ListDataEvent intervalAdded;

        private ListDataEvent intervalRemoved;

        public void contentsChanged(ListDataEvent e) {
            contentsChanged = e;
        }

        public void intervalAdded(ListDataEvent e) {
            intervalAdded = e;
        }

        public void intervalRemoved(ListDataEvent e) {
            intervalRemoved = e;
        }

    }
}