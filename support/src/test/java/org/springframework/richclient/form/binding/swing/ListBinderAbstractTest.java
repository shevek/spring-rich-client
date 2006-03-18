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
package org.springframework.richclient.form.binding.swing;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.PropertyMetadata;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
/**
 * Tests for ListBinder and ListBinding
 * 
 * @author  Oliver Hutchison
 * @author  Andy DePue 
 */
public class ListBinderAbstractTest extends BindingAbstractTest {
    private ListBinder lb;

    private Map context;

    private List selectableItems;

    private ValueModel selectableItemsHolder;

    private ListBinding b;

    private JList c;

    protected String setUpBinding() {
        Application.load(null);
        new Application(new DefaultApplicationLifecycleAdvisor());
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        Application.services().setApplicationContext(applicationContext);
        applicationContext.refresh();

        lb = new ListBinder();
        context = new HashMap();

        selectableItems = Arrays.asList(new Object[] {new Item("A"), new Item("B"), new Item("C"), new Item("D"),
                new Item("E")});

        selectableItemsHolder = new ValueHolder(selectableItems);

        context.put(ListBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
        context.put(ListBinder.RENDERER_KEY, new BeanPropertyValueListRenderer("name"));
        context.put(ListBinder.COMPARATOR_KEY, new PropertyComparator("name", true, false));

        return "listProperty";
    }

    protected void setupBinding(final String formPropertyPath) {
        vm = fm.getValueModel(formPropertyPath);
        context.put(ListBinder.SELECTED_ITEM_HOLDER_KEY, vm);
        final Class selectionPropertyType = fm.getPropertyMetadata(formPropertyPath).getPropertyType();
        if (selectionPropertyType != null) {
            context.put(ListBinder.SELECTED_ITEM_TYPE_KEY, selectionPropertyType);
        }
    }

    protected void setupMultipleSelectionBinding() {
        setupBinding("listProperty");
    }

    protected void doBinding(final String formPropertyPath) {
        b = (ListBinding)lb.bind(fm, formPropertyPath, context);
        c = (JList)b.getControl();
    }

    protected void doMultipleSelectionBinding() {
        doBinding("listProperty");
    }

    protected void multipleSelectionBinding() {
        setupMultipleSelectionBinding();
        doMultipleSelectionBinding();
    }

    protected void setupSingleSelectionBinding() {
        setupBinding("singleSelectListProperty");
    }

    protected void doSingleSelectionBinding() {
        doBinding("singleSelectListProperty");
    }

    protected void singleSelectionBinding() {
        setupSingleSelectionBinding();
        doSingleSelectionBinding();
    }

    public void testComponentTracksEnabledChanges() {
        multipleSelectionBinding();
        assertEquals(true, c.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, c.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, c.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        multipleSelectionBinding();
        PropertyMetadata state = fm.getPropertyMetadata("listProperty");
        assertEquals(true, c.isEnabled());
        state.setReadOnly(true);
        assertEquals(false, c.isEnabled());
        state.setReadOnly(false);
        assertEquals(true, c.isEnabled());
    }

    public void testParseSingleIntervalSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, "SINGLE_INTERVAL_SELECTION");
        doMultipleSelectionBinding();
        assertEquals(ListSelectionModel.SINGLE_INTERVAL_SELECTION, c.getSelectionMode());
    }

    public void testParseMultipleIntervalSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, "MULTIPLE_INTERVAL_SELECTION");
        doMultipleSelectionBinding();
        assertEquals(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, c.getSelectionMode());
    }

    public void testParseSingleSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, "SINGLE_SELECTION");
        doMultipleSelectionBinding();
        assertEquals(ListSelectionModel.SINGLE_SELECTION, c.getSelectionMode());
    }

    public void testParseIntegerSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, new Integer(ListSelectionModel.SINGLE_INTERVAL_SELECTION));
        doMultipleSelectionBinding();
        assertEquals(ListSelectionModel.SINGLE_INTERVAL_SELECTION, c.getSelectionMode());
    }

    public void testInvalidSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, "INVALID_SELECTION");
        try {
            doMultipleSelectionBinding();
            fail("INVALID_SELECTION should have caused IllegalArgumentException");
        }
        catch (IllegalArgumentException iae) {
            // Test passed
        }
    }

    public void testDefaultSingleSelection() {
        singleSelectionBinding();
        assertEquals(ListSelectionModel.SINGLE_SELECTION, c.getSelectionMode());
    }

    public void testDefaultMultipleSelection() {
        multipleSelectionBinding();
        assertEquals(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, c.getSelectionMode());
    }

    public void testNoInitialMultipleSelection() {
        multipleSelectionBinding();
        assertEquals(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, c.getSelectionMode());
        assertTrue("Expected empty selection in list.", c.getSelectedValues() == null
                || c.getSelectedValues().length == 0);
    }

    public void testNoInitialSingleSelection() {
        singleSelectionBinding();
        assertEquals(ListSelectionModel.SINGLE_SELECTION, c.getSelectionMode());
        assertEquals("Expected empty selection in list.", null, c.getSelectedValue());
    }

    public void testInitialMultipleSelection() {
        setupMultipleSelectionBinding();
        vm.setValue(Arrays.asList(new Object[] {new Item("E"), new Item("B")}));
        doMultipleSelectionBinding();

        // Make sure original list is intact
        assertNotNull(vm.getValue());
        assertEquals(2, ((List)vm.getValue()).size());

        final Object[] selection = c.getSelectedValues();
        assertNotNull(selection);
        assertEquals(2, selection.length);
        assertTrue(selection[0] == selectableItems.get(1));
        assertTrue(selection[1] == selectableItems.get(4));
    }

    public void testInitialForcedSingleSelection() {
        setupMultipleSelectionBinding();
        context.put(ListBinder.SELECTION_MODE_KEY, "SINGLE_SELECTION");
        final List originalList = Arrays.asList(new Object[] {new Item("E"), new Item("B")});
        vm.setValue(originalList);
        doMultipleSelectionBinding();

        // The original list should now be modified to contain only the first
        // selection.  Also, the code currently creates a new list instead of
        // reusing the original list.
        assertNotNull(vm.getValue());
        assertFalse(vm.getValue() == originalList);
        assertEquals(1, ((List)vm.getValue()).size());
        assertEquals(new Item("E"), ((List)vm.getValue()).get(0));

        final Object[] selection = c.getSelectedValues();
        assertNotNull(selection);
        assertEquals(1, selection.length);
        assertTrue(selection[0] == selectableItems.get(4));
    }

    public void testInitialSingleSelection() {
        setupSingleSelectionBinding();
        final Item testItem = new Item("C");
        vm.setValue(testItem);
        doSingleSelectionBinding();

        assertTrue(vm.getValue() == testItem);

        assertEquals(testItem, c.getSelectedValue());
        assertTrue(selectableItems.get(2) == c.getSelectedValue());
        final Object[] selection = c.getSelectedValues();
        assertNotNull(selection);
        assertEquals(1, selection.length);
        assertTrue(selectableItems.get(2) == selection[0]);
    }

    public void testSingleSelectionTracksSelectionHolder() {
        singleSelectionBinding();

        assertNull(vm.getValue());
        assertNull(c.getSelectedValue());
        Item selected = new Item("D");
        vm.setValue(selected);
        assertTrue(selectableItems.get(3) == c.getSelectedValue());
        assertEquals(selected, vm.getValue());

        selected = new Item("A");
        vm.setValue(selected);
        assertTrue(selectableItems.get(0) == c.getSelectedValue());
        assertEquals(selected, vm.getValue());
    }

    public void testSelectionHolderTracksSingleSelection() {
        singleSelectionBinding();

        assertNull(vm.getValue());
        assertNull(c.getSelectedValue());

        Item selected = new Item("D");
        c.setSelectedValue(selected, true);
        assertTrue(selectableItems.get(3) == c.getSelectedValue());
        assertEquals(selected, vm.getValue());

        selected = new Item("A");
        c.setSelectedValue(selected, true);
        assertTrue(selectableItems.get(0) == c.getSelectedValue());
        assertEquals(selected, vm.getValue());

        c.clearSelection();
        assertNull(c.getSelectedValue());
        assertNull(vm.getValue());
    }

    protected void assertValidMultipleSelection(final Collection original) {
        final Collection selectedValue = (Collection)vm.getValue();
        final Object[] selected = c.getSelectedValues();
        if (original == null || original.size() == 0) {
            assertTrue(selected == null || selected.length == 0);
            assertTrue(selectedValue == null || selectedValue.size() == 0);
        }
        else {
            assertNotNull(selected);
            assertNotNull(selectedValue);
            assertEquals(original.size(), selected.length);
            assertEquals(original.size(), selectedValue.size());
            int i = 0;
            final Iterator iSelectedValue = selectedValue.iterator();
            for (final Iterator iOriginal = original.iterator(); iOriginal.hasNext(); i++) {
                final Object object = iOriginal.next();
                final Object selectionHolderValue = iSelectedValue.next();
                final Object sourceObject = selectableItems.get(selectableItems.indexOf(object));
                assertTrue(selected[i] == sourceObject);
                assertEquals(sourceObject, selectionHolderValue);
            }
            assertFalse(iSelectedValue.hasNext());
        }
    }

    public void testMultipleSelectionTracksSelectionHolder() {
        multipleSelectionBinding();

        assertNull(vm.getValue());
        assertTrue(c.getSelectedValues() == null || c.getSelectedValues().length == 0);

        List original = Arrays.asList(new Object[] {new Item("B"), new Item("C")});
        vm.setValue(original);
        assertValidMultipleSelection(original);

        original = Arrays.asList(new Object[] {new Item("A"), new Item("B"), new Item("D"), new Item("E")});
        vm.setValue(original);
        assertValidMultipleSelection(original);

        vm.setValue(null);
        assertValidMultipleSelection(null);
    }

    protected void performMultipleSelection(final Collection selection) {
        final int[] indices = new int[selection.size()];
        int i = 0;
        for (final Iterator iter = selection.iterator(); iter.hasNext(); i++) {
            indices[i] = indexOf(iter.next());
        }
        c.setSelectedIndices(indices);
    }

    protected int indexOf(final Object o) {
        final ListModel model = c.getModel();
        final int size = model.getSize();
        for (int i = 0; i < size; i++) {
            if (o.equals(model.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public void testSelectionHolderTracksMultipleSelection() {
        multipleSelectionBinding();

        assertNull(vm.getValue());
        assertTrue(c.getSelectedValues() == null || c.getSelectedValues().length == 0);

        List original = Arrays.asList(new Object[] {new Item("A")});

        performMultipleSelection(original);
        assertValidMultipleSelection(original);

        original = Arrays.asList(new Object[] {new Item("B"), new Item("D"), new Item("E")});
        performMultipleSelection(original);
        assertValidMultipleSelection(original);

        c.clearSelection();
        assertValidMultipleSelection(null);
    }

    public void testComponentUpdatesValueModel() {
        // TODO Auto-generated method stub
    }

    public void testValueModelUpdatesComponent() {
        // TODO Auto-generated method stub
    }

    static class Item {
        private String name;

        public Item(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public boolean equals(final Object frameKey) {
            if (this == frameKey)
                return true;
            if (frameKey == null || getClass() != frameKey.getClass())
                return false;

            final Item item = (Item)frameKey;

            if (name != null ? !name.equals(item.name) : item.name != null)
                return false;

            return true;
        }

        public int hashCode() {
            return (name != null ? name.hashCode() : 0);
        }

        public String toString() {
            return "Item{" + "name='" + name + "'" + "}";
        }
    }
}