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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.springframework.enum.AbstractCodedEnum;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageAreaPane;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.util.BeanPropertyValueListRenderer;
import org.springframework.richclient.util.ComboBoxListModel;
import org.springframework.richclient.util.DynamicComboBoxListModel;
import org.springframework.richclient.util.DynamicListModel;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.ListListModel;
import org.springframework.rules.values.AspectAccessStrategy;
import org.springframework.rules.values.AspectAdapter;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.CompoundFormModel;
import org.springframework.rules.values.FormModel;
import org.springframework.rules.values.MetaAspectAccessStrategy;
import org.springframework.rules.values.MutableAspectAccessStrategy;
import org.springframework.rules.values.MutableFormModel;
import org.springframework.rules.values.NestingFormModel;
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
public class SwingFormModel implements FormModel {

    private MutableFormModel formModel;

    private ValueCommitPolicy valueCommitPolicy = ValueCommitPolicy.AS_YOU_TYPE;

    private ComponentFactory componentFactory = ApplicationServices.locator()
            .getComponentFactory();

    public SwingFormModel(MutableFormModel formModel) {
        Assert.notNull(formModel);
        this.formModel = formModel;
    }

    protected ComponentFactory getComponentFactory() {
        return componentFactory;
    }

    public void setComponentFactory(ComponentFactory factory) {
        Assert.notNull(factory);
        this.componentFactory = factory;
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
        formModel.setRulesSource(ApplicationServices.locator());
        formModel.setBufferChanges(bufferChanges);
        return new SwingFormModel(formModel);
    }

    public static NestingFormModel createCompoundFormModel(Object formObject) {
        CompoundFormModel model = new CompoundFormModel(formObject);
        model.setRulesSource(ApplicationServices.locator());
        return model;
    }

    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String pageName) {
        SwingFormModel childPageFormModel = new SwingFormModel(groupingModel
                .createChild(pageName));
        return childPageFormModel;
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

    public Object getValue(String formProperty) {
        return formModel.getValue(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        return formModel.getValueModel(formProperty);
    }

    private ValueModel getOrCreateValueModel(String domainObjectProperty) {
        ValueModel model = getValueModel(domainObjectProperty);
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

    public JFormattedTextField createBoundTextField(String formProperty) {
        return createBoundTextField(formProperty, new FormatterFactory(
                valueCommitPolicy));
    }

    public JFormattedTextField createBoundTextField(String formProperty,
            AbstractFormatterFactory formatterFactory) {
        ValueModel valueModel = getOrCreateValueModel(formProperty);
        JFormattedTextField textField = createNewTextField(formatterFactory); 
        new JFormatedTextFieldValueSetter(textField, valueModel);
        textField.setValue(valueModel.get());
        textField.setEditable(isWriteable(formProperty));
        return textField;
    }
    
    protected JFormattedTextField createNewTextField(AbstractFormatterFactory formatterFactory) {
        return getComponentFactory().createFormattedTextField(formatterFactory);
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
        ValueModel valueModel = getOrCreateValueModel(formProperty);
        new PropertyEditorValueSetter(propertyEditor, valueModel);
        propertyEditor.setValue(valueModel.get());
        return (JComponent)customEditor;
    }

    public JComponent bind(JTextComponent component, String formProperty) {
        return bind(component, formProperty, valueCommitPolicy);
    }

    public JComponent bind(JTextComponent component, String formProperty,
            ValueCommitPolicy valueCommitPolicy) {
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
        return bind(createNewComboBox(), formProperty);
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

    private JComboBox createBoundComboBox(ValueModel selectedItemHolder,
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
        ValueModel valueModel = getValueModel(formProperty);
        if (valueModel == null) {
            AspectAdapter adapter = new AspectAdapter(formModel
                    .getAspectAccessStrategy(), formProperty);
            valueModel = new BufferedListValueModel(adapter);
            formModel.add(formProperty, valueModel);
        }
        return (ListModel)valueModel.get();
    }

    private static class BufferedListValueModel extends BufferedValueModel {
        private ListListModel items;

        public BufferedListValueModel(ValueModel wrappedModel) {
            super(wrappedModel);
        }

        public Object get() {
            if (!isChangeBuffered()) {
                super.set(internalGet());
            }
            return super.get();
        }

        protected Object internalGet() {
            if (this.items == null) {
                this.items = new ListListModel();
                this.items.addListDataListener(new ListDataListener() {
                    public void contentsChanged(ListDataEvent e) {
                        fireValueChanged();
                    }

                    public void intervalAdded(ListDataEvent e) {
                        fireValueChanged();
                    }

                    public void intervalRemoved(ListDataEvent e) {
                        fireValueChanged();
                    }
                });
            }
            else {
                this.items.clear();
            }
            List list = (List)getWrappedModel().get();
            if (list != null) {
                this.items.addAll(list);
            }
            return this.items;
        }

        protected void onWrappedValueChanged() {
            super.set(internalGet());
        }
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
            Guarded guardedComponent, MessageAreaPane messageAreaPane) {
        return createSingleLineResultsReporter(this, guardedComponent,
                messageAreaPane);
    }

    public static ValidationListener createSingleLineResultsReporter(
            FormModel formModel, Guarded guardedComponent,
            MessageAreaPane messageAreaPane) {
        return new SimpleValidationResultsReporter(formModel, guardedComponent,
                messageAreaPane);
    }

}