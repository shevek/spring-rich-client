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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.support.SortDefinition;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory.BeanPropertyEditorClosure;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author Oliver Hutchison
 */
public class SwingBindingFactoryTests extends SpringRichTestCase {

    private SwingBindingFactory sbf;

    public void doSetUp() {
        sbf = new SwingBindingFactory(FormModelHelper.createFormModel(new TestBean()));
        sbf.setBinderSelectionStrategy(new TestingBinderSelectionStrategy());
    }

    public void testSwingBindingFactory() {
        try {
            new SwingBindingFactory(null);
            fail("allowed null form model");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCreateBoundTextField() {
        TestableBinding b = (TestableBinding)sbf.createBoundTextField("name");
        assertBindingProperties(b, JTextField.class, null, "name");
        assertEquals(Collections.EMPTY_MAP, b.getContext());
    }

    public void testCreateBoundCheckBox() {
        TestableBinding b = (TestableBinding)sbf.createBoundCheckBox("name");
        assertBindingProperties(b, JCheckBox.class, null, "name");
        assertEquals(Collections.EMPTY_MAP, b.getContext());
    }

    public void testCreateBoundLabel() {
        TestableBinding b = (TestableBinding)sbf.createBoundLabel("name");
        assertBindingProperties(b, JLabel.class, null, "name");
        assertEquals(Collections.EMPTY_MAP, b.getContext());
    }

    public void testCreateBoundComboBoxString() {
        TestableBinding b = (TestableBinding)sbf.createBoundComboBox("name");
        assertBindingProperties(b, JComboBox.class, null, "name");
        assertEquals(Collections.EMPTY_MAP, b.getContext());
    }

    public void testCreateBoundComboBoxStringObjectArray() {
        Object[] items = new Object[0];
        TestableBinding b = (TestableBinding)sbf.createBoundComboBox("name", items);
        assertBindingProperties(b, JComboBox.class, null, "name");
        assertEquals(1, b.getContext().size(), 1);
        assertEquals(items, b.getContext().get(ComboBoxBinder.SELECTABLE_ITEMS_KEY));
    }

    public void testCreateBoundComboBoxStringValueModel() {
        ValueModel valueHolder = new ValueHolder();
        TestableBinding b = (TestableBinding)sbf.createBoundComboBox("name", valueHolder);
        assertBindingProperties(b, JComboBox.class, null, "name");
        assertEquals(1, b.getContext().size(), 1);
        assertEquals(valueHolder, b.getContext().get(ComboBoxBinder.SELECTABLE_ITEMS_KEY));
    }

    public void testCreateBoundComboBoxStringStringString() {
        TestableBinding b = (TestableBinding)sbf.createBoundComboBox("name", "listProperty", "displayProperty");
        assertBindingProperties(b, JComboBox.class, null, "name");
        assertEquals(4, b.getContext().size());
        assertEquals(sbf.getFormModel().getValueModel("listProperty"), b.getContext().get(
                ComboBoxBinder.SELECTABLE_ITEMS_KEY));
        assertEquals("displayProperty",
                ((BeanPropertyValueListRenderer)b.getContext().get(ComboBoxBinder.RENDERER_KEY)).getPropertyName());
        assertEquals("displayProperty",
                ((BeanPropertyEditorClosure)b.getContext().get(ComboBoxBinder.EDITOR_KEY)).getRenderedProperty());
        assertEquals("displayProperty", getComparatorProperty(b));

        try {
            b = (TestableBinding)sbf.createBoundComboBox("name", "someUnknownProperty", "displayProperty");
            fail("cant use an unknown property to provide the selectable items");
        }
        catch (InvalidPropertyException e) {
            // expected
        }
    }

    public void testCreateBoundComboBoxStringValueModelString() {
        ValueModel selectableItemsHolder = new ValueHolder(new Object());
        TestableBinding b = (TestableBinding)sbf.createBoundComboBox("name", selectableItemsHolder, "displayProperty");
        assertBindingProperties(b, JComboBox.class, null, "name");
        assertEquals(4, b.getContext().size());
        assertEquals(selectableItemsHolder, b.getContext().get(ComboBoxBinder.SELECTABLE_ITEMS_KEY));
        assertEquals("displayProperty",
                ((BeanPropertyValueListRenderer)b.getContext().get(ComboBoxBinder.RENDERER_KEY)).getPropertyName());
        assertEquals("displayProperty",
                ((BeanPropertyEditorClosure)b.getContext().get(ComboBoxBinder.EDITOR_KEY)).getRenderedProperty());
        assertEquals("displayProperty", getComparatorProperty(b));
    }

    public void testCreateBoundListModel() {
        ValueModel vm = ((DefaultFormModel)sbf.getFormModel()).getFormObjectPropertyAccessStrategy().getPropertyValueModel(
                "listProperty");
        ObservableList observableList = sbf.createBoundListModel("listProperty");

        ArrayList list = new ArrayList();
        list.add(new Integer(1));
        vm.setValue(list);
        assertEquals(new Integer(1), observableList.get(0));
        observableList.add(new Integer(2));
        assertEquals(1, ((List)vm.getValue()).size());
        sbf.getFormModel().commit();
        assertEquals(new Integer(2), ((List)vm.getValue()).get(1));
    }

    public void testCreateBoundListString() {
        TestableBinding b = (TestableBinding)sbf.createBoundList("listProperty");
        assertBindingProperties(b, JList.class, null, "listProperty");

        assertEquals(1, b.getContext().size());
    }

    public void testCreateBoundListStringObjectString() {
        Object selectableItems = new Object();
        TestableBinding b = (TestableBinding)sbf.createBoundList("listProperty", selectableItems, "displayProperty");
        assertBindingProperties(b, JList.class, null, "listProperty");

        assertEquals(3, b.getContext().size());
        assertEquals(selectableItems,
                ((ValueModel)b.getContext().get(ListBinder.SELECTABLE_ITEMS_KEY)).getValue());
        assertEquals("displayProperty",
                ((BeanPropertyValueListRenderer)b.getContext().get(ListBinder.RENDERER_KEY)).getPropertyName());
        assertEquals("displayProperty", getComparatorProperty(b));
        assertFalse(b.getContext().containsKey(ListBinder.SELECTION_MODE_KEY));
    }

    public void testCreateBoundListStringValueModelString() {
        ValueModel selectableItemsHolder = new ValueHolder(new Object());
        TestableBinding b = (TestableBinding)sbf.createBoundList("listProperty", selectableItemsHolder,
                "displayProperty");
        assertBindingProperties(b, JList.class, null, "listProperty");

        assertEquals(3, b.getContext().size());
        assertEquals(selectableItemsHolder, b.getContext().get(ListBinder.SELECTABLE_ITEMS_KEY));
        assertEquals("displayProperty",
                ((BeanPropertyValueListRenderer)b.getContext().get(ListBinder.RENDERER_KEY)).getPropertyName());
        assertEquals("displayProperty", getComparatorProperty(b));
        assertFalse(b.getContext().containsKey(ListBinder.SELECTION_MODE_KEY));
    }

    private void assertBindingProperties(TestableBinding b, Class controlType, JComponent control, String property) {
        assertEquals(b.getControlType(), controlType);
        assertEquals(b.getControl(), control);
        assertEquals(b.getFormModel(), sbf.getFormModel());
        assertEquals(b.getProperty(), property);
    }
    
    private String getComparatorProperty(TestableBinding b) {
        // TODO: remove this nasty reflection once PropertyComparator is extended with the abbility
        // to query the SortDefinition
        return ((SortDefinition)getField(b.getContext().get(ListBinder.COMPARATOR_KEY),
                "sortDefinition")).getProperty();
    }

    private Object getField(Object source, String fieldName) {
        try {
            Field field = source.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(source);
        }
        catch (Exception e) {
            fail(e.toString());
            return null;
        }
    }
}