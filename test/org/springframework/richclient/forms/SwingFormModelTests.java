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

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import junit.framework.TestCase;

import org.apache.commons.collections.ComparatorUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.enums.StringCodedEnum;
import org.springframework.enums.support.StaticCodedEnumResolver;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.list.ListListModel;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.closure.Constraint;

/**
 * TODO test commit
 * 
 * @author Peter De Bruycker
 */
public class SwingFormModelTests extends TestCase {

    private class CustomEditor extends CustomDateEditor {

        private boolean beSilent;

        private JTextField component = new JTextField();

        public CustomEditor() {
            super(new SimpleDateFormat("dd/MM/yyyy"), true);
            component.getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(DocumentEvent e) {
                    if (!beSilent) {
                        setAsText(component.getText());
                    }
                }

                public void insertUpdate(DocumentEvent e) {
                    if (!beSilent) {
                        setAsText(component.getText());
                    }
                }

                public void removeUpdate(DocumentEvent e) {
                    if (!beSilent) {
                        setAsText(component.getText());
                    }
                }
            });
        }

        public Component getCustomEditor() {
            return component;
        }

        public void setValue(Object value) {
            super.setValue(value);
            beSilent = true;
            component.setText(getAsText());
            beSilent = false;
        }

        public boolean supportsCustomEditor() {
            return true;
        }

    }

    private class TestBean {

        private Date date = new Date();

        private TestEnum enumProperty;

        private boolean flag;

        private TestItemBean currentItem;

        private List items = new ArrayList();

        private String stringProperty;

        public List getPossibleEnumSelection() {
            return Arrays.asList(new TestEnum[] { TestEnum.ENUM1, TestEnum.ENUM3, TestEnum.ENUM5 });
        }

        public TestBean(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public Date getDate() {
            return date;
        }

        public TestEnum getEnumProperty() {
            return enumProperty;
        }

        public List getItems() {
            return items;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setEnumProperty(TestEnum enumProperty) {
            this.enumProperty = enumProperty;
        }

        public void setFlag(boolean b) {
            flag = b;
        }

        public void setItems(List list) {
            items = list;
        }

        public void setStringProperty(String string) {
            stringProperty = string;
        }

        public TestItemBean getCurrentItem() {
            return currentItem;
        }

        public void setCurrentItem(TestItemBean bean) {
            currentItem = bean;
        }
    }

    public static class TestEnum extends StringCodedEnum {

        public static final TestEnum ENUM1 = new TestEnum("1", "enum 1");

        public static final TestEnum ENUM2 = new TestEnum("2", "enum 2");

        public static final TestEnum ENUM3 = new TestEnum("3", "enum 3");

        public static final TestEnum ENUM4 = new TestEnum("4", "enum 4");

        public static final TestEnum ENUM5 = new TestEnum("5", "enum 5");

        static {
            StaticCodedEnumResolver.instance().registerStaticEnums(TestEnum.class);
        }

        private String name;

        public TestEnum(String code, String name) {
            super(code);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private class TestItemBean {

        private String code;

        private String description;

        public TestItemBean(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private static String[] STRING_ARRAY = new String[] { "item0", "item1", "item2", "item3", "item4" };

    private static String[] STRING_ARRAY_UNSORTED = new String[] { "item3", "item4", "item1", "item0", "item2" };

    private StaticApplicationContext applicationContext;

    private SwingFormModel formModel;

    private TestableFormComponentInterceptor interceptor;

    private TestBean testBean;

    private void assertCheckBoxBinding(JCheckBox checkBox, String formProperty, boolean initialValue) {
        assertEquals("name not set on component", formProperty, checkBox.getName());

        assertEquals("interceptor not called", 1, interceptor.getComponentCount());
        assertEquals("interceptor not called", formProperty, interceptor.getComponentProperty());
        assertEquals("interceptor not called", checkBox, interceptor.getComponent());

        assertEquals(0, interceptor.getLabelCount());

        assertEquals("checkBox selection value not set", initialValue, checkBox.isSelected());

        formModel.getValueModel(formProperty).setValue(Boolean.valueOf(!initialValue));
        assertEquals("change in valuemodel not propagated to the component", !initialValue, checkBox.isSelected());
    }

    private void assertComboBoxBinding(JComboBox comboBox, String formProperty, Object initialValue, Object newValue) {
        assertFalse("initialValue and newValue cannot be the same", ObjectUtils.nullSafeEquals(initialValue, newValue));

        assertEquals("name not set on component", formProperty, comboBox.getName());

        assertEquals("interceptor not called", 1, interceptor.getComponentCount());
        assertEquals("interceptor not called", formProperty, interceptor.getComponentProperty());
        assertEquals("interceptor not called", comboBox, interceptor.getComponent());

        assertEquals(0, interceptor.getLabelCount());

        assertEquals("initial combobox item not selected", initialValue, comboBox.getSelectedItem());

        formModel.getValueModel(formProperty).setValue(newValue);
        assertEquals("change in valuemodel not propagated to the component", newValue, comboBox.getSelectedItem());

        comboBox.setSelectedItem(initialValue);
        assertEquals("change in component not propagated to valuemodel", initialValue, formModel.getValue(formProperty));
    }

    private void assertTextComponentBinding(JTextComponent textComponent, String formProperty, String initialValue,
            String newValue, boolean commitOnFocusLost) {
        assertFalse("initialValue and newValue cannot be the same", ObjectUtils.nullSafeEquals(initialValue, newValue));

        assertEquals("name not set on component", formProperty, textComponent.getName());

        assertEquals("interceptor not called", 1, interceptor.getComponentCount());
        assertEquals("interceptor not called", formProperty, interceptor.getComponentProperty());
        assertEquals("interceptor not called", textComponent, interceptor.getComponent());

        assertEquals(0, interceptor.getLabelCount());

        assertEquals("label doesn't have text", initialValue, textComponent.getText());

        formModel.getValueModel(formProperty).setValue(newValue);
        assertEquals("change in valuemodel not propagated to the component", newValue, textComponent.getText());

        if (textComponent.isEditable()) {
            textComponent.requestFocusInWindow();
            textComponent.setText(initialValue);

            if (commitOnFocusLost) {
                assertFalse("change was already commited, should wait until focus lost", ObjectUtils.nullSafeEquals(
                        initialValue, formModel.getValue(formProperty)));

                // simulate focus lost
                FocusEvent event = new FocusEvent(textComponent, FocusEvent.FOCUS_LOST);
                FocusListener[] focusListeners = textComponent.getFocusListeners();
                for (int i = 0; i < focusListeners.length; i++) {
                    focusListeners[i].focusLost(event);
                }
            }

            assertEquals("change in component not propagated to valuemodel", initialValue, formModel
                    .getValue(formProperty));
        }
    }

    private void pass() {
        // test passes
    }

    //    public void testCreateBoundTableModel() {
    //        SwingFormModel formModel = SwingFormModel.createFormModel(testBean);
    //        TableModel tableModel =
    //            formModel.createBoundTableModel(
    //                "items",
    //                TestItemBean.class,
    //                new String[] { "code", "description" },
    //                new Class[] { String.class, String.class });
    //		assertEquals(4, tableModel.getRowCount());
    //		assertEquals(2, tableModel.getColumnCount());
    //    }

    protected void setUp() throws Exception {
        //		load application
        Application.load(null);
        new Application(new DefaultApplicationLifecycleAdvisor());
        applicationContext = new StaticApplicationContext();
        Application.services().setApplicationContext(applicationContext);
        applicationContext.refresh();

        testBean = new TestBean("testBean");
        testBean.getItems().add(new TestItemBean("1", "item1"));
        testBean.getItems().add(new TestItemBean("2", "item2"));
        testBean.getItems().add(new TestItemBean("3", "item3"));
        testBean.getItems().add(new TestItemBean("4", "item4"));

        interceptor = new TestableFormComponentInterceptor();

        formModel = SwingFormModel.createFormModel(testBean);
        formModel.setInterceptor(interceptor);
    }

    public void testBindAsLabel() {
        JTextField textField = new JTextField();
        formModel.bindAsLabel(textField, "stringProperty");
        assertFalse(textField.isEditable());
        assertTextComponentBinding(textField, "stringProperty", testBean.getStringProperty(), "new text", false);
    }

    public void testBindCheckBox() {
        try {
            formModel.bind(new JCheckBox(), "stringProperty");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }

        JCheckBox checkBox = new JCheckBox();
        checkBox = formModel.bind(checkBox, "flag");

        assertCheckBoxBinding(checkBox, "flag", testBean.isFlag());
    }

    public void testBindComboBox() {
        testBean.setStringProperty(STRING_ARRAY[0]);

        JComboBox comboBox = new JComboBox(STRING_ARRAY);
        formModel.bind(comboBox, "stringProperty");
        assertEquals(STRING_ARRAY.length, comboBox.getItemCount());

        assertComboBoxBinding(comboBox, "stringProperty", testBean.getStringProperty(), STRING_ARRAY[2]);
    }

    public void testBindComboBoxWithSelectableItemsAndComparator() {
        testBean.setStringProperty(STRING_ARRAY_UNSORTED[0]);

        List items = Arrays.asList(STRING_ARRAY_UNSORTED);

        JComboBox comboBox = new JComboBox();
        formModel.bind(comboBox, "stringProperty", items, ComparatorUtils.naturalComparator());
        assertEquals(STRING_ARRAY_UNSORTED.length, comboBox.getItemCount());
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            assertEquals("comboBox items not sorted", "item" + i, comboBox.getItemAt(i));
        }

        assertComboBoxBinding(comboBox, "stringProperty", testBean.getStringProperty(), STRING_ARRAY[2]);
    }

    public void testBindCustomEditor() {
        CustomEditor customEditor = new CustomEditor();
        JComponent component = formModel.bindCustomEditor(customEditor, "date");
        assertEquals(customEditor.component, component);

        assertEquals("name not set on component", "date", component.getName());

        assertEquals("interceptor not called", 1, interceptor.getComponentCount());
        assertEquals("interceptor not called", "date", interceptor.getComponentProperty());
        assertEquals("interceptor not called", component, interceptor.getComponent());

        assertEquals(0, interceptor.getLabelCount());

        assertEquals("checkBox selection value not set", testBean.getDate(), customEditor.getValue());

        Calendar calendar = new GregorianCalendar(1977, Calendar.JANUARY, 10);
        formModel.getValueModel("date").setValue(calendar.getTime());
        assertEquals("change in valuemodel not propagated to the component", calendar.getTime(), customEditor
                .getValue());
    }

    public void testBindTextComponentWithTextArea() {
        JTextArea textArea = new JTextArea();
        formModel.bind(textArea, "stringProperty");

        assertTextComponentBinding(textArea, "stringProperty", testBean.getStringProperty(), "new text", true);
    }

    public void testBindTextComponentWithTextAreaValueCommitPolicyAsYouType() {
        JTextArea textArea = new JTextArea();
        formModel.bind(textArea, "stringProperty", ValueCommitPolicy.AS_YOU_TYPE);

        assertTextComponentBinding(textArea, "stringProperty", testBean.getStringProperty(), "new text", false);
    }

    public void testBindTextComponentWithTextField() {
        JTextField textField = new JTextField();
        formModel.bind(textField, "stringProperty");

        assertTextComponentBinding(textField, "stringProperty", testBean.getStringProperty(), "new text", false);
    }

    public void testBindTextComponentWithTextFieldValueCommitPolicyFocusLost() {
        JTextField textField = new JTextField();
        formModel.bind(textField, "stringProperty", ValueCommitPolicy.FOCUS_LOST);

        assertTextComponentBinding(textField, "stringProperty", testBean.getStringProperty(), "new text", true);
    }

    public void testCreateBoundCheckBox() {
        String checkBoxText = "flag checkbox text";
        applicationContext.addMessage("checkBox.flag", Locale.getDefault(), checkBoxText);

        JCheckBox checkBox = formModel.createBoundCheckBox("flag");

        assertCheckBoxBinding(checkBox, "flag", testBean.isFlag());
        assertEquals("incorrect key for label", checkBoxText, checkBox.getText());
    }

    public void testCreateBoundCheckBoxWithLabelKey() {
        String checkBoxText = "custom";
        applicationContext.addMessage("checkBox.customFlag", Locale.getDefault(), checkBoxText);

        JCheckBox checkBox = formModel.createBoundCheckBox("customFlag", "flag");

        assertCheckBoxBinding(checkBox, "flag", testBean.isFlag());
        assertEquals("incorrect key for label", checkBoxText, checkBox.getText());
    }

    public void testCreateBoundComboBox() {
        testBean.setStringProperty("item1");

        JComboBox comboBox = formModel.createBoundComboBox("stringProperty");

        // if we add items like this, it fails, because no action is taken when
        // the model of the combobox changes
        // comboBox.setModel(new
        // DefaultComboBoxModel(Arrays.asList(STRING_ARRAY)));

        ComboBoxListModel model = (ComboBoxListModel) comboBox.getModel();
        model.addAll(Arrays.asList(STRING_ARRAY));
        assertComboBoxBinding(comboBox, "stringProperty", testBean.getStringProperty(), "item3");
    }

    public void testCreateBoundComboBoxWithEnumType() {
        testBean.setEnumProperty(TestEnum.ENUM3);

        JComboBox comboBox = formModel.createBoundComboBox("enumProperty");
        assertEquals(5, comboBox.getItemCount());
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            assertEquals(ClassUtils.getFieldValue(TestEnum.class.getName() + ".ENUM" + (i + 1)), comboBox.getItemAt(i));
        }

        assertComboBoxBinding(comboBox, "enumProperty", testBean.getEnumProperty(), TestEnum.ENUM1);
    }

    public void testCreateBoundEnumComboBox() {
        testBean.setEnumProperty(TestEnum.ENUM3);

        JComboBox comboBox = formModel.createBoundEnumComboBox("enumProperty");
        assertEquals(5, comboBox.getItemCount());
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            assertEquals(ClassUtils.getFieldValue(TestEnum.class.getName() + ".ENUM" + (i + 1)), comboBox.getItemAt(i));
        }

        assertComboBoxBinding(comboBox, "enumProperty", testBean.getEnumProperty(), TestEnum.ENUM1);
    }

    public void testCreateBoundEnumComboBoxWithFilter() {
        testBean.setEnumProperty(TestEnum.ENUM3);

        final List validObjects = testBean.getPossibleEnumSelection();
        final List testedObjects = new ArrayList();
        Constraint filter = new Constraint() {

            public boolean test(Object obj) {
                testedObjects.add(obj);
                return validObjects.contains(obj);
            }
        };

        JComboBox comboBox = formModel.createBoundEnumComboBox("enumProperty", filter);
        assertEquals(validObjects.size(), comboBox.getItemCount());
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            assertEquals(validObjects.get(i), comboBox.getItemAt(i));
        }
        assertEquals(5, testedObjects.size());
        assertTrue(testedObjects.contains(TestEnum.ENUM1));
        assertTrue(testedObjects.contains(TestEnum.ENUM2));
        assertTrue(testedObjects.contains(TestEnum.ENUM3));
        assertTrue(testedObjects.contains(TestEnum.ENUM4));
        assertTrue(testedObjects.contains(TestEnum.ENUM5));

        assertComboBoxBinding(comboBox, "enumProperty", testBean.getEnumProperty(), TestEnum.ENUM1);
    }

    public void testCreateBoundComboWithSelectableItemsProperty() {
        testBean.setEnumProperty((TestEnum) testBean.getPossibleEnumSelection().get(0));

        JComboBox comboBox = formModel.createBoundComboBox("enumProperty", "possibleEnumSelection", null);
        assertEquals(3, comboBox.getItemCount());
        for (int i = 0; i < 3; i++) {
            assertEquals(testBean.getPossibleEnumSelection().get(i), comboBox.getItemAt(i));
        }

        assertComboBoxBinding(comboBox, "enumProperty", testBean.getEnumProperty(), TestEnum.ENUM5);
    }

    public void testCreateBoundComboBoxWithItemArray() {
        testBean.setStringProperty(STRING_ARRAY[0]);
        JComboBox comboBox = formModel.createBoundComboBox("stringProperty", STRING_ARRAY);
        assertEquals(STRING_ARRAY.length, comboBox.getItemCount());

        assertComboBoxBinding(comboBox, "stringProperty", testBean.getStringProperty(), STRING_ARRAY[2]);
    }

    public void testCreateBoundLabel() {
        JTextComponent component = formModel.createBoundLabel("stringProperty");
        assertFalse(component.isEditable());
        assertTextComponentBinding(component, "stringProperty", testBean.getStringProperty(), "new text", false);
    }

    public void testCreateBoundTextArea() {
        JTextArea textArea = formModel.createBoundTextArea("stringProperty", 10, 40);
        assertEquals(10, textArea.getRows());
        assertEquals(40, textArea.getColumns());

        assertTextComponentBinding(textArea, "stringProperty", testBean.getStringProperty(), "new text", true);
    }

    public void testCreateBoundTextField() {
        JTextField field = formModel.createBoundTextField("stringProperty");

        assertTextComponentBinding(field, "stringProperty", testBean.getStringProperty(), "new text", false);
    }

    public void testCreateLabel() {
        String labelText = "date label";
        applicationContext.addMessage("label.date", Locale.getDefault(), labelText);

        JLabel label = formModel.createLabel("date");
        assertNotNull("label not created", label);
        assertEquals("wrong key for finding label text", labelText, label.getText());

        // test interception
        assertEquals(0, interceptor.getComponentCount());
        assertEquals(1, interceptor.getLabelCount());
        assertEquals("date", interceptor.getLabelProperty());
        assertEquals(label, interceptor.getLabel());
    }

    private void assertListBinding(JList list, String formProperty, List initialList, Object item) {
        assertEquals("name not set on component", formProperty, list.getName());

        assertEquals("interceptor not called", 1, interceptor.getComponentCount());
        assertEquals("interceptor not called", formProperty, interceptor.getComponentProperty());
        assertEquals("interceptor not called", list, interceptor.getComponent());

        assertEquals(0, interceptor.getLabelCount());

        assertListModelBinding((ListListModel) list.getModel(), formProperty, initialList, item);
    }

    private void assertListModelBinding(ListListModel listModel, String formProperty, List initialList, Object item) {
        List valueModelList = (List) formModel.getValueModel(formProperty).getValue();
        assertEquals(valueModelList, listModel);

        valueModelList.add(item);
        assertEquals("item added to valuemodel but not to list", initialList.size() + 1, listModel.size());
        assertEquals("item added to valuemodel but not to list", item, listModel.get(listModel.size() - 1));

        valueModelList.remove(item);
        assertEquals("item removed from valuemodel but not from list", initialList.size(), listModel.size());

        listModel.add(item);
        assertEquals("item added to listmodel but not to valuemodel", initialList.size() + 1, valueModelList.size());
        assertTrue("item added to listmodel but not to valuemodel", valueModelList.contains(item));

        valueModelList.remove(item);
        assertEquals("item removed from listmodel but not from valuemodel", initialList.size(), valueModelList.size());
        assertFalse("item removed from listmodel but not from valuemodel", valueModelList.contains(item));
    }

    public void testCreateBoundList() {
        JList list = formModel.createBoundList("items");
        assertNotNull("list not created", list);
        assertEquals(testBean.getItems().size(), list.getModel().getSize());

        assertListBinding(list, "items", testBean.getItems(), new TestItemBean("new", "new item"));
    }

    public void testCreateBoundListModel() {
        ListListModel listModel = (ListListModel) formModel.createBoundListModel("items");

        assertListModelBinding(listModel, "items", testBean.getItems(), new TestItemBean("new", "new item"));
    }

    // TODO test with renderedProperty not null
    public void testCreateBoundListToFormProperty() {
        testBean.setCurrentItem((TestItemBean) testBean.getItems().get(1));
        JList list = formModel.createBoundList("currentItem", testBean.getItems(), null);

        assertListBindingToFormProperty(list, "currentItem", testBean.getItems(), testBean.getCurrentItem(), testBean
                .getItems().get(0));
    }

    private void assertListBindingToFormProperty(JList list, String formProperty, List selectableItems,
            Object initialValue, Object newValue) {
        assertEquals(selectableItems.size(), list.getModel().getSize());
        for (int i = 0; i < list.getModel().getSize(); i++) {
            assertEquals(selectableItems.get(i), list.getModel().getElementAt(i));
        }

        assertEquals(initialValue, list.getSelectedValue());
        assertEquals(formModel.getValue(formProperty), list.getSelectedValue());

        list.setSelectedIndex(0);
        assertEquals("selection in list changed, but valuemodel not updated", list.getSelectedValue(), formModel
                .getValue(formProperty));
        list.clearSelection();
        assertNull("selection in list cleared, but valuemodel not updated", formModel.getValue(formProperty));

        formModel.getValueModel(formProperty).setValue(newValue);
        assertEquals("selection in valuemodel changed, but list not updated", formModel.getValue(formProperty), list
                .getSelectedValue());
        formModel.getValueModel(formProperty).setValue(null);
        assertNull("valuemodel cleared, but list selection not cleared", list.getSelectedValue());

    }

    public void testBindList() {
        testBean.setCurrentItem((TestItemBean) testBean.getItems().get(1));

        JList list = new JList();
        formModel.bind(list, "currentItem", new ValueHolder(testBean.getItems()), null);

        assertListBindingToFormProperty(list, "currentItem", testBean.getItems(), testBean.getCurrentItem(), testBean
                .getItems().get(0));
    }
}