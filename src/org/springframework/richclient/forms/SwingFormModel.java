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
import java.beans.PropertyEditor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.springframework.enums.AbstractCodedEnum;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesAccessorSupport;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.list.DynamicComboBoxListModel;
import org.springframework.richclient.list.DynamicListModel;
import org.springframework.richclient.list.ListListModel;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.rules.values.AspectAccessStrategy;
import org.springframework.rules.values.AspectAdapter;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.CommitListener;
import org.springframework.rules.values.CompoundFormModel;
import org.springframework.rules.values.FormModel;
import org.springframework.rules.values.MetaAspectAccessStrategy;
import org.springframework.rules.values.MutableAspectAccessStrategy;
import org.springframework.rules.values.MutableFormModel;
import org.springframework.rules.values.NestingFormModel;
import org.springframework.rules.values.TypeConverter;
import org.springframework.rules.values.ValidatingFormModel;
import org.springframework.rules.values.ValidationListener;
import org.springframework.rules.values.ValueHolder;
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.comparators.BeanPropertyComparator;

/**
 * @author Keith Donald
 */
public class SwingFormModel extends ApplicationServicesAccessorSupport
        implements FormModel {

    private MutableFormModel formModel;

    private ValueCommitPolicy valueCommitPolicy = ValueCommitPolicy.AS_YOU_TYPE;

    public SwingFormModel(MutableFormModel formModel) {
        Assert.notNull(formModel);
        this.formModel = formModel;
    }

    public void setValueCommitPolicy(ValueCommitPolicy policy) {
        Assert.notNull(policy);
        this.valueCommitPolicy = policy;
    }

    public static SwingFormModel createFormModel(Object formObject) {
        return createFormModel(formObject, true);
    }

    public static SwingFormModel createUnbufferedFormModel(Object formObject) {
        return createFormModel(formObject, false);
    }

    public static SwingFormModel createFormModel(Object formObject,
            boolean bufferChanges) {
        ValidatingFormModel formModel = new ValidatingFormModel(formObject);
        formModel.setRulesSource(Application.services());
        formModel.setBufferChangesDefault(bufferChanges);
        return new SwingFormModel(formModel);
    }

    public static NestingFormModel createCompoundFormModel(Object formObject) {
        CompoundFormModel model = new CompoundFormModel(formObject);
        model.setRulesSource(Application.services());
        return model;
    }

    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String pageName) {
        return new SwingFormModel(groupingModel.createChild(pageName));
    }

    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String childPageName,
            String parentPropertyFormObjectPath) {
        return new SwingFormModel(groupingModel.createChild(childPageName,
                parentPropertyFormObjectPath));
    }

    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String childPageName,
            ValueModel childFormObjectHolder) {
        return new SwingFormModel(groupingModel.createChild(childPageName,
                childFormObjectHolder));
    }

    public void registerCustomEditor(Class clazz,
            PropertyEditor customPropertyEditor) {
        formModel.getAspectAccessStrategy().registerCustomEditor(clazz,
                customPropertyEditor);
    }

    public void registerCustomEditor(String domainObjectProperty,
            PropertyEditor customPropertyEditor) {
        formModel.getAspectAccessStrategy().registerCustomEditor(
                getMetaAspectAccessor().getAspectClass(domainObjectProperty),
                domainObjectProperty, customPropertyEditor);
    }

    public void addCommitListener(CommitListener listener) {
        formModel.addCommitListener(listener);
    }

    public void removeCommitListener(CommitListener listener) {
        formModel.removeCommitListener(listener);
    }

    public void addValidationListener(ValidationListener listener) {
        formModel.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        formModel.removeValidationListener(listener);
    }

    public void addValueListener(String formProperty, ValueListener listener) {
        formModel.addValueListener(formProperty, listener);
    }

    public void removeValueListener(String formProperty, ValueListener listener) {
        formModel.removeValueListener(formProperty, listener);
    }

    public Object getFormObject() {
        return formModel.getFormObject();
    }

    public ValueModel getFormObjectHolder() {
        return formModel.getFormObjectHolder();
    }

    public String getDisplayValue(String formProperty) {
        return formModel.getDisplayValue(formProperty);
    }

    public Object getValue(String formProperty) {
        return formModel.getValue(formProperty);
    }

    public ValueModel getDisplayValueModel(String formProperty) {
        return formModel.getDisplayValueModel(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        return formModel.getValueModel(formProperty);
    }

    private ValueModel getOrCreateValueModel(String domainObjectProperty) {
        ValueModel model = formModel.getValueModel(domainObjectProperty);
        if (model == null) {
            model = createFormValueModel(domainObjectProperty);
        }
        return model;
    }

    public ValueModel createFormValueModel(String domainObjectProperty) {
        return formModel.add(domainObjectProperty);
    }

    public void setParent(NestingFormModel parent) {
        formModel.setParent(parent);
    }

    public boolean hasErrors() {
        return formModel.hasErrors();
    }

    public Map getErrors() {
        return formModel.getErrors();
    }

    public boolean isDirty() {
        return formModel.isDirty();
    }

    public boolean getBufferChanges() {
        return formModel.getBufferChanges();
    }

    public void commit() {
        formModel.commit();
    }

    public void revert() {
        formModel.revert();
    }

    public AspectAccessStrategy getAspectAccessStrategy() {
        return formModel.getAspectAccessStrategy();
    }

    protected MetaAspectAccessStrategy getMetaAspectAccessor() {
        return formModel.getMetaAspectAccessor();
    }

    private ValueModel newNestedAspectAdapter(ValueModel parentValueHolder,
            String childProperty) {
        MutableAspectAccessStrategy strategy = (MutableAspectAccessStrategy)getAspectAccessStrategy();
        AspectAdapter adapter = new AspectAdapter(strategy
                .newNestedAccessor(parentValueHolder), childProperty);
        return adapter;
    }

    private boolean isEnumeration(String formProperty) {
        return getMetaAspectAccessor().isEnumeration(formProperty);
    }

    private boolean isBoolean(String formProperty) {
        Class aspectClass = getMetaAspectAccessor()
                .getAspectClass(formProperty);
        return aspectClass.equals(Boolean.class)
                || aspectClass.equals(Boolean.TYPE);
    }

    private boolean isWriteable(String formProperty) {
        return getMetaAspectAccessor().isWriteable(formProperty);
    }

    public JComponent createBoundControl(String formProperty) {
        if (isEnumeration(formProperty)) {
            return createBoundComboBoxFromEnum(formProperty);
        }
        else if (isBoolean(formProperty)) {
            return createBoundCheckBox(formProperty);
        }
        else {
            PropertyEditor propertyEditor = formModel.getAspectAccessStrategy()
                    .findCustomEditor(null, formProperty);
            if (propertyEditor != null && propertyEditor.supportsCustomEditor()) {
                return bindCustomEditor(propertyEditor, formProperty);
            }
            else {
                return createBoundTextField(formProperty);
            }
        }
    }

    public JFormattedTextField createBoundFormattedTextField(String formProperty) {
        Class valueClass = getMetaAspectAccessor().getAspectClass(formProperty);
        return createBoundFormattedTextField(formProperty,
                new FormatterFactory(valueClass, valueCommitPolicy));
    }

    public JFormattedTextField createBoundFormattedTextField(
            String formProperty, AbstractFormatterFactory formatterFactory) {
        ValueModel valueModel = new AspectAdapter(formModel
                .getAspectAccessStrategy(), formProperty);
        if (formModel.getBufferChanges()) {
            valueModel = new BufferedValueModel(valueModel);
        }
        JFormattedTextField textField = createNewFormattedTextField(formatterFactory);
        TypeConverter typeConverter = new TypeConverter(valueModel, textField);
        ValueModel validatingModel = formModel.add(formProperty, typeConverter);
        textField.setEditable(isWriteable(formProperty));
        textField.setValue(valueModel.get());
        if (textField.isEditable()) {
            new JFormattedTextFieldValueSetter(textField, validatingModel,
                    valueCommitPolicy);
        }
        return textField;
    }

    protected JFormattedTextField createNewFormattedTextField(
            AbstractFormatterFactory formatterFactory) {
        return getComponentFactory().createFormattedTextField(formatterFactory);
    }

    public JTextField createBoundTextField(String formProperty) {
        return (JTextField)bind(createNewTextField(), formProperty);
    }

    public JTextField createBoundTextField(String formProperty,
            ValueCommitPolicy commitPolicy) {
        return (JTextField)bind(createNewTextField(), formProperty,
                commitPolicy);
    }

    protected JTextField createNewTextField() {
        return getComponentFactory().createTextField();
    }

    public JSpinner createBoundSpinner(String formProperty) {
        final ValueModel model = getOrCreateValueModel(formProperty);
        final JSpinner spinner = createNewSpinner();
        if (getMetaAspectAccessor().isDate(formProperty)) {
            spinner.setModel(new SpinnerDateModel());
        }
        new SpinnerValueSetter(spinner, model);
        return spinner;
    }

    protected JSpinner createNewSpinner() {
        return new JSpinner();
    }

    private JComponent bindCustomEditor(PropertyEditor propertyEditor,
            String formProperty) {
        Assert
                .isTrue(propertyEditor.supportsCustomEditor(),
                        "The propertyEditor to bind must provide a customEditor component.");
        final Component customEditor = propertyEditor.getCustomEditor();
        Assert.notNull(customEditor,
                "The customEditor property cannot be null.");
        Assert.isTrue(customEditor instanceof JComponent,
                "customEditors must be JComponents; however, you have provided a "
                        + customEditor.getClass());
        ValueModel valueModel = getValueModel(formProperty);
        if (valueModel == null) {
            createFormValueModel(formProperty);
            // create above returns the display value model appyling the
            // property editor, the setter listener wants the 'wrapped' value
            // model...
            valueModel = getValueModel(formProperty);
        }
        propertyEditor.setValue(valueModel.get());
        new PropertyEditorValueSetter(propertyEditor, valueModel);
        return (JComponent)customEditor;
    }

    public JTextComponent bind(JTextComponent component, String formProperty) {
        return bind(component, formProperty, valueCommitPolicy);
    }

    public JTextComponent bind(final JTextComponent component,
            String formProperty, ValueCommitPolicy valueCommitPolicy) {
        final ValueModel valueModel = getOrCreateValueModel(formProperty);
        component.setText((String)valueModel.get());
        if (isWriteable(formProperty)) {
            component.setEditable(true);
            if (valueCommitPolicy == ValueCommitPolicy.AS_YOU_TYPE
                    && (!(component instanceof JTextArea))) {
                new AsYouTypeTextValueSetter(component, valueModel);
            }
            else {
                new FocusLostTextValueSetter(component, valueModel);
            }
        }
        else {
            component.setEditable(false);
            valueModel.addValueListener(new ValueListener() {
                public void valueChanged() {
                    component.setText((String)valueModel.get());
                }
            });
        }
        return component;
    }

    public JTextComponent createBoundLabel(String formProperty) {
        JTextArea area = createNewTextArea();
        return bindAsLabel(GuiStandardUtils.textAreaAsLabel(area), formProperty);
    }

    protected JTextArea createNewTextArea() {
        return getComponentFactory().createTextArea();
    }

    protected JTextComponent bindAsLabel(final JTextComponent component,
            String formProperty) {
        final ValueModel value = getOrCreateValueModel(formProperty);
        component.setText((String)value.get());
        component.setEditable(false);
        value.addValueListener(new ValueListener() {
            public void valueChanged() {
                component.setText((String)value.get());
            }
        });
        return component;
    }

    // @TODO better support for nested properties...
    public JTextComponent bindAsLabel(final JTextComponent component,
            String parentProperty, String childPropertyToDisplay) {
        ValueModel value = getOrCreateValueModel(parentProperty);
        final ValueModel nestedAccessor = newNestedAspectAdapter(value,
                childPropertyToDisplay);
        component.setText((String)nestedAccessor.get());
        nestedAccessor.addValueListener(new ValueListener() {
            public void valueChanged() {
                component.setText((String)nestedAccessor.get());
            }
        });
        return component;
    }

    public JCheckBox createBoundCheckBox(String formProperty) {
        return bind(createNewCheckBox(formProperty), formProperty);
    }

    public JCheckBox createBoundCheckBox(String labelKey, String formProperty) {
        return bind(createNewCheckBox(labelKey), formProperty);
    }

    protected JCheckBox createNewCheckBox(String labelKey) {
        return getComponentFactory().createCheckBox(labelKey);
    }

    public JCheckBox bind(JCheckBox checkBox, String formProperty) {
        ValueModel valueModel = getOrCreateValueModel(formProperty);
        checkBox.setModel(new SelectableButtonValueModel(valueModel));
        return checkBox;
    }

    public JComboBox createBoundComboBox(String formProperty) {
        if (isEnumeration(formProperty)) {
            return createBoundComboBoxFromEnum(formProperty);
        }
        else {
            return bind(createNewComboBox(), formProperty);
        }
    }

    public JComboBox bind(JComboBox comboBox, String selectionFormProperty) {
        ValueModel selectedValueModel = getOrCreateValueModel(selectionFormProperty);
        comboBox.setModel(new DynamicComboBoxListModel(selectedValueModel));
        return comboBox;
    }

    public JComboBox createBoundComboBox(String selectionFormProperty,
            String selectableItemsProperty, String renderedItemProperty) {
        ValueModel selectedValueModel = getOrCreateValueModel(selectionFormProperty);
        ValueModel itemsValueModel = getOrCreateValueModel(selectableItemsProperty);
        JComboBox comboBox = createBoundComboBox(selectedValueModel,
                itemsValueModel, renderedItemProperty);
        return comboBox;
    }

    public JComboBox createBoundComboBox(String selectionFormProperty,
            ValueModel selectableItemsHolder, String renderedItemProperty) {
        ValueModel selectedValueModel = getOrCreateValueModel(selectionFormProperty);
        JComboBox comboBox = createBoundComboBox(selectedValueModel,
                selectableItemsHolder, renderedItemProperty);
        return comboBox;
    }

    public JComboBox createBoundComboBox(ValueModel selectedItemHolder,
            ValueModel selectableItemsHolder, String renderedProperty) {
        Comparator comparator = (renderedProperty != null ? new BeanPropertyComparator(
                renderedProperty)
                : null);
        JComboBox comboBox = bind(createNewComboBox(), selectedItemHolder,
                selectableItemsHolder, comparator);
        if (renderedProperty != null) {
            comboBox.setRenderer(new BeanPropertyValueListRenderer(
                    renderedProperty));
        }
        return comboBox;
    }

    public JComboBox createBoundComboBoxFromEnum(String selectionFormProperty) {
        JComboBox comboBox = createNewComboBox();
        getComponentFactory().configureForEnum(comboBox,
                getEnumType(selectionFormProperty));
        return bind(comboBox, selectionFormProperty, (List)comboBox.getModel(),
                AbstractCodedEnum.DEFAULT_ORDER);
    }

    public JComboBox bind(JComboBox comboBox, String selectionFormProperty,
            List selectableItems, Comparator itemsComparator) {
        ValueModel selectedValueModel = getOrCreateValueModel(selectionFormProperty);
        return bind(comboBox, selectedValueModel, new ValueHolder(
                selectableItems), itemsComparator);
    }

    public JComboBox bind(JComboBox comboBox, ValueModel selectedItemHolder,
            ValueModel selectableItemsHolder, Comparator comparator) {
        ComboBoxListModel model;
        if (selectableItemsHolder != null) {
            model = new DynamicComboBoxListModel(selectedItemHolder,
                    selectableItemsHolder);
        }
        else {
            if (selectedItemHolder != null) {
                model = new DynamicComboBoxListModel(selectedItemHolder);
            }
            else {
                model = new ComboBoxListModel();
            }
        }
        model.setComparator(comparator);
        comboBox.setModel(model);
        return comboBox;
    }

    protected JComboBox createNewComboBox() {
        return getComponentFactory().createComboBox();
    }

    private String getEnumType(String formProperty) {
        Class enumClass = getMetaAspectAccessor().getAspectClass(formProperty);
        try {
            Class.forName(enumClass.getName());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ClassUtils.getShortNameAsProperty(enumClass);
    }

    /**
     * Bind the specified form property, which must be a backed by a
     * <code>java.util.List</code> to a ListModel. What this does is ensure
     * when items are added or removed to/from the list model, the property's
     * list is also updated. Validation also occurs against the property when it
     * changes, etc.
     * 
     * Changes to the list managed by the list model are buffered before being
     * committed to the underlying bean property. This prevents the domain
     * object from having to worry about returning a non-null List instance.
     * 
     * @param formProperty
     * @return The bound list model.
     */
    public ListModel createBoundListModel(String formProperty) {
        ValueModel valueModel = formModel.getValueModel(formProperty);
        if (valueModel == null) {
            AspectAdapter adapter = new AspectAdapter(formModel
                    .getAspectAccessStrategy(), formProperty);
            valueModel = new BufferedListValueModel(adapter);
            formModel.add(formProperty, valueModel);
        }
        return (ListModel)valueModel.get();
    }

    public JList createBoundList(String formProperty) {
        ListModel listModel = createBoundListModel(formProperty);
        JList list = createNewList();
        list.setModel(listModel);
        return list;
    }

    public JList createBoundList(String selectionFormProperty,
            List selectableItems, String renderedProperty) {
        return createBoundList(selectionFormProperty, new ValueHolder(
                selectableItems), renderedProperty);
    }

    public JList createBoundList(String selectionFormProperty,
            ValueModel selectableItemsHolder, String renderedProperty) {
        Comparator itemsComparator = (renderedProperty != null ? new BeanPropertyComparator(
                renderedProperty)
                : null);
        JList list = bind(createNewList(), selectionFormProperty,
                selectableItemsHolder, itemsComparator);
        if (renderedProperty != null) {
            list.setCellRenderer(new BeanPropertyValueListRenderer(
                    renderedProperty));
        }
        return list;
    }

    protected JList createNewList() {
        JList list = getComponentFactory().createList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }

    public JList bind(JList list, String selectionFormProperty,
            ValueModel selectableItemsHolder, Comparator itemsComparator) {
        return bind(list, getOrCreateValueModel(selectionFormProperty),
                selectableItemsHolder, itemsComparator);
    }

    public JList bind(JList list, ValueModel selectedItemHolder,
            ValueModel selectableItemsHolder, Comparator itemsComparator) {
        ListListModel model;
        if (selectableItemsHolder != null) {
            model = new DynamicListModel(selectableItemsHolder);
        }
        else {
            model = new ListListModel();
        }
        model.setComparator(itemsComparator);
        list.setModel(model);
        list.setSelectedValue(selectedItemHolder.get(), true);
        list.addListSelectionListener(new ListSelectedValueMediator(list,
                selectedItemHolder));
        return list;
    }

    private static class ListSelectedValueMediator implements
            ListSelectionListener {
        private JList list;

        private ValueModel selectedValueModel;

        private boolean updating;

        public ListSelectedValueMediator(JList list,
                ValueModel selectedValueModel) {
            this.list = list;
            this.selectedValueModel = selectedValueModel;
            subscribe();
        }

        private void subscribe() {
            selectedValueModel.addValueListener(new ValueListener() {
                public void valueChanged() {
                    if (selectedValueModel.get() != null) {
                        if (!updating) {
                            list.setSelectedValue(selectedValueModel.get(),
                                    true);
                        }
                    }
                    else {
                        list.clearSelection();
                    }
                }
            });
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                updating = true;
                selectedValueModel.set(list.getSelectedValue());
                updating = false;
            }
        }
    }

    public JTextArea createBoundTextArea(String formProperty, int rows,
            int columns) {
        if (rows <= 0) {
            rows = 5;
        }
        if (columns <= 0) {
            columns = 25;
        }
        return (JTextArea)bind(getComponentFactory().createTextArea(rows,
                columns), formProperty, valueCommitPolicy);
    }

    public ValidationListener createSingleLineResultsReporter(
            Guarded guardedComponent, MessageReceiver messageAreaPane) {
        return createSingleLineResultsReporter(this, guardedComponent,
                messageAreaPane);
    }

    public static ValidationListener createSingleLineResultsReporter(
            FormModel formModel, Guarded guardedComponent,
            MessageReceiver messageAreaPane) {
        return new SimpleValidationResultsReporter(formModel, guardedComponent,
                messageAreaPane);
    }

}