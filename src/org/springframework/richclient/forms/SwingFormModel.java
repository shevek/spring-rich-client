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

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
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

import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.SingleConfigurableFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.form.support.CompoundFormModel;
import org.springframework.binding.form.support.ValidatingFormModel;
import org.springframework.binding.swing.ComboBoxModelAdapter;
import org.springframework.binding.swing.SelectableItemsListModel;
import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.PropertyAdapter;
import org.springframework.binding.value.support.TypeConverter;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.enums.AbstractCodedEnum;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesAccessorSupport;
import org.springframework.richclient.application.PropertyEditorRegistry;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.list.DynamicComboBoxListModel;
import org.springframework.richclient.list.DynamicListModel;
import org.springframework.richclient.list.FilteredComboBoxListModel;
import org.springframework.richclient.list.ListListModel;
import org.springframework.richclient.list.ObservableList;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.rules.Constraint;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.comparator.BeanPropertyComparator;

/**
 * @author Keith Donald
 */
public class SwingFormModel extends ApplicationServicesAccessorSupport
        implements FormModel, PropertyChangePublisher {

    private static final String CHECK_BOX_LABEL_SUFFIX = "checkBox";

    private SingleConfigurableFormModel formModel;

    private ValueCommitPolicy valueCommitPolicy = ValueCommitPolicy.AS_YOU_TYPE;

    public SwingFormModel(SingleConfigurableFormModel formModel) {
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

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the value model at the specified parent property path.
     * 
     * @param groupingModel
     *            the model to create the SwingFormModel in
     * @param childPageName
     *            the name to associate the created SwingFormModel with in the
     *            groupingModel
     * @param childFormObjectPropertyPath
     *            the path into the groupingModel that the SwingFormModel is for
     * @return The child form model
     */
    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String childPageName,
            String childFormObjectPropertyPath) {
        return new SwingFormModel(groupingModel.createChild(childPageName,
                childFormObjectPropertyPath));
    }

    public static SwingFormModel createChildPageFormModel(
            NestingFormModel groupingModel, String childPageName,
            ValueModel childFormObjectHolder) {
        return new SwingFormModel(groupingModel.createChild(childPageName,
                childFormObjectHolder));
    }

    public static NestingFormModel createChildCompoundFormModel(
            NestingFormModel groupingModel, String childPageName,
            String parentPropertyFormObjectPath) {
        return groupingModel.createCompoundChild(childPageName,
                parentPropertyFormObjectPath);
    }

    public void registerCustomEditor(Class clazz,
            PropertyEditor customPropertyEditor) {
        formModel.getPropertyAccessStrategy().registerCustomEditor(clazz,
                customPropertyEditor);
    }

    public void registerCustomEditor(String domainObjectProperty,
            PropertyEditor customPropertyEditor) {
        formModel.getPropertyAccessStrategy().registerCustomEditor(
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        formModel.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        formModel.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        formModel.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        formModel.removePropertyChangeListener(propertyName, listener);
    }

    public void addFormObjectChangeListener(ValueChangeListener listener) {
        formModel.addFormObjectChangeListener(listener);
    }

    public void removeFormObjectChangeListener(ValueChangeListener listener) {
        formModel.removeFormObjectChangeListener(listener);
    }

    public void addFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        formModel.addFormValueChangeListener(formPropertyPath, listener);
    }

    public void removeFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        formModel.removeFormValueChangeListener(formPropertyPath, listener);
    }

    public void addFormPropertyChangeListener(String formPropertyPath,
            PropertyChangeListener listener) {
        formModel.addFormPropertyChangeListener(formPropertyPath, listener);
    }

    public void removeFormPropertyChangeListener(String formPropertyPath,
            PropertyChangeListener listener) {
        formModel.removeFormPropertyChangeListener(formPropertyPath, listener);
    }

    public Object getFormObject() {
        return formModel.getFormObject();
    }

    public void setFormObject(Object formObject) {
        formModel.setFormObject(formObject);
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

    private ValueModel getOrCreateDisplayValueModel(String domainObjectProperty) {
        ValueModel model = formModel.getDisplayValueModel(domainObjectProperty);
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

    public boolean getHasErrors() {
        return formModel.getHasErrors();
    }

    public Map getErrors() {
        return formModel.getErrors();
    }

    public boolean isDirty() {
        return formModel.isDirty();
    }

    public boolean isEnabled() {
        return formModel.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        formModel.setEnabled(enabled);
    }

    public boolean getBufferChangesDefault() {
        return formModel.getBufferChangesDefault();
    }

    public void commit() {
        formModel.commit();
    }

    public void revert() {
        formModel.revert();
    }

    public PropertyAccessStrategy getPropertyAccessStrategy() {
        return formModel.getPropertyAccessStrategy();
    }

    public PropertyMetadataAccessStrategy getMetadataAccessStrategy() {
        return formModel.getMetadataAccessStrategy();
    }

    private ValueModel newNestedAspectAdapter(ValueModel parentValueHolder,
            String childProperty) {
        MutablePropertyAccessStrategy strategy = (MutablePropertyAccessStrategy)getPropertyAccessStrategy();
        PropertyAdapter adapter = new PropertyAdapter(strategy
                .newPropertyAccessStrategy(parentValueHolder), childProperty);
        return adapter;
    }

    protected boolean isEnumeration(String formProperty) {
        return getMetadataAccessStrategy().isEnumeration(formProperty);
    }

    protected boolean isBoolean(String formProperty) {
        Class aspectClass = getMetadataAccessStrategy().getPropertyType(
                formProperty);
        return aspectClass.equals(Boolean.class)
                || aspectClass.equals(Boolean.TYPE);
    }

    protected boolean isWriteable(String formProperty) {
        return getMetadataAccessStrategy().isWriteable(formProperty);
    }

    /**
     * Create a bound control for the given form property.
     * <p />
     * The strategy used for determining the control to bind to is:
     * <ol>
     * <li>See if one is registered specificly against this FormModel
     * <li>See if one is registered in the global registry
     * <li>Try some hard-coded defaults if all else fails
     * </ol>
     * 
     * @param formProperty
     *            the property to get the control for
     * @return a bound control; never null
     * @see PropertyEditorRegistry#setPropertyEditor(Class, Class)
     * @see PropertyEditorRegistry#setPropertyEditor(Class, String, Class)
     * @see SwingFormModel#registerCustomEditor(Class, PropertyEditor)
     * @see SwingFormModel#registerCustomEditor(String, PropertyEditor)
     */
    public JComponent createBoundControl(String formProperty) {
        PropertyEditor propertyEditor = formModel.getPropertyAccessStrategy()
                .findCustomEditor(formProperty);
        if (propertyEditor != null && propertyEditor.supportsCustomEditor()) { return bindCustomEditor(
                propertyEditor, formProperty); }

        final ApplicationServices applicationServices = Application.services();
        final PropertyEditorRegistry propertyEditorRegistry = applicationServices
                .getPropertyEditorRegistry();
        propertyEditor = propertyEditorRegistry.getPropertyEditor(
                getFormObject().getClass(), formProperty);
        if (propertyEditor != null && propertyEditor.supportsCustomEditor()) {
            return bindCustomEditor(propertyEditor, formProperty);
        }
        else {
            if (isEnumeration(formProperty)) {
                return createBoundEnumComboBox(formProperty);
            }
            else if (isBoolean(formProperty)) {
                return createBoundCheckBox(formProperty);
            }
            else {
                return createBoundTextField(formProperty);
            }
        }
    }

    /**
     * Does binding that's common to <u>all </u> component types.
     * 
     * @param component
     *            the compoenent to bind
     * @param formProperty
     *            the name of the component's property
     * @return the component that was passed in
     */
    protected JComponent bindControl(JComponent component, String formProperty) {
        component.setName(formProperty);
        return component;
    }

    public JFormattedTextField createBoundFormattedTextField(String formProperty) {
        Class valueClass = getMetadataAccessStrategy().getPropertyType(
                formProperty);
        return createBoundFormattedTextField(formProperty,
                new FormatterFactory(valueClass, valueCommitPolicy));
    }

    public JFormattedTextField createBoundFormattedTextField(
            String formProperty, AbstractFormatterFactory formatterFactory) {
        ValueModel valueModel = new PropertyAdapter(formModel
                .getPropertyAccessStrategy(), formProperty);
        if (formModel.getBufferChangesDefault()) {
            valueModel = new BufferedValueModel(valueModel);
        }
        JFormattedTextField textField = createNewFormattedTextField(formatterFactory);
        TypeConverter typeConverter = new TypeConverter(valueModel, textField);
        ValueModel validatingModel = formModel.add(formProperty, typeConverter);
        textField.setEditable(isWriteable(formProperty));
        textField.setValue(valueModel.getValue());
        if (textField.isEditable()) {
            new JFormattedTextFieldValueSetter(textField, validatingModel,
                    valueCommitPolicy);
        }
        return (JFormattedTextField)bindControl(textField, formProperty);
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
        final ValueModel model = getOrCreateDisplayValueModel(formProperty);
        final JSpinner spinner = createNewSpinner();
        if (getMetadataAccessStrategy().isDate(formProperty)) {
            spinner.setModel(new SpinnerDateModel());
        }
        new SpinnerValueSetter(spinner, model);
        return (JSpinner)bindControl(spinner, formProperty);
    }

    protected JSpinner createNewSpinner() {
        return new JSpinner();
    }

    private JComponent bindCustomEditor(PropertyEditor propertyEditor,
            String formProperty) {
        Assert
                .isTrue(propertyEditor.supportsCustomEditor(),
                        "The propertyEditor to bind must provide a customEditor component.");
        final JComponent customEditor = (JComponent)propertyEditor
                .getCustomEditor();
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
        propertyEditor.setValue(valueModel.getValue());
        new PropertyEditorValueSetter(propertyEditor, valueModel);
        return bindControl(customEditor, formProperty);
    }

    public JTextComponent bind(JTextComponent component, String formProperty) {
        return bind(component, formProperty, valueCommitPolicy);
    }

    public JTextComponent bind(final JTextComponent component,
            String formProperty, ValueCommitPolicy valueCommitPolicy) {
        final ValueModel valueModel = getOrCreateDisplayValueModel(formProperty);
        try {
            component.setText((String)valueModel.getValue());
        }
        catch (ClassCastException e) {
            IllegalArgumentException ex = new IllegalArgumentException(
                    "Class cast exception converting value to string - did you install a type converter?");
            ex.initCause(e);
            throw ex;
        }
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
            valueModel.addValueChangeListener(new ValueChangeListener() {
                public void valueChanged() {
                    component.setText((String)valueModel.getValue());
                }
            });
        }
        return (JTextComponent)bindControl(component, formProperty);
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
        final ValueModel value = getOrCreateDisplayValueModel(formProperty);
        component.setText((String)value.getValue());
        component.setEditable(false);
        value.addValueChangeListener(new ValueChangeListener() {
            public void valueChanged() {
                component.setText((String)value.getValue());
            }
        });
        return (JTextComponent)bindControl(component, formProperty);
    }

    // @TODO better support for nested properties...
    public JTextComponent bindAsLabel(final JTextComponent component,
            String parentProperty, String childPropertyToDisplay) {
        ValueModel value = getOrCreateDisplayValueModel(parentProperty);
        final ValueModel nestedAccessor = newNestedAspectAdapter(value,
                childPropertyToDisplay);
        component.setText((String)nestedAccessor.getValue());
        nestedAccessor.addValueChangeListener(new ValueChangeListener() {
            public void valueChanged() {
                component.setText((String)nestedAccessor.getValue());
            }
        });
        return (JTextComponent)bindControl(component, parentProperty);
    }

    public JCheckBox createBoundCheckBox(String formProperty) {
        return bind(createNewCheckBox(formProperty), formProperty);
    }

    public JCheckBox createBoundCheckBox(String labelKey, String formProperty) {
        return bind(createNewCheckBox(labelKey), formProperty);
    }

    protected JCheckBox createNewCheckBox(String labelKey) {
        return getComponentFactory().createCheckBox(
                new String[] { labelKey + "." + CHECK_BOX_LABEL_SUFFIX,
                        labelKey });
    }

    public JCheckBox bind(JCheckBox checkBox, String formProperty) {
        ValueModel valueModel = getOrCreateDisplayValueModel(formProperty);
        checkBox.setModel(new SelectableButtonValueModel(valueModel));
        return (JCheckBox)bindControl(checkBox, formProperty);
    }

    public JComboBox createBoundComboBox(String formProperty) {
        if (isEnumeration(formProperty)) {
            return createBoundEnumComboBox(formProperty);
        }
        else {
            return bind(createNewComboBox(), formProperty);
        }
    }

    public JComboBox createBoundComboBox(String selectionFormProperty,
            Object[] selectableItems) {
        ValueModel selectionValueModel = getOrCreateDisplayValueModel(selectionFormProperty);
        ComboBoxModelAdapter comboBoxModel = new ComboBoxModelAdapter(
                new SelectableItemsListModel(selectableItems,
                        selectionValueModel));
        return createNewComboBox(comboBoxModel);
    }

    public JComboBox bind(JComboBox comboBox, String selectionFormProperty) {
        ValueModel selectedValueModel = getOrCreateDisplayValueModel(selectionFormProperty);
        comboBox.setModel(new DynamicComboBoxListModel(selectedValueModel));
        return (JComboBox)bindControl(comboBox, selectionFormProperty);
    }

    public JComboBox createBoundComboBox(String selectionFormProperty,
            String selectableItemsProperty, String renderedItemProperty) {
        ValueModel selectedValueModel = getOrCreateDisplayValueModel(selectionFormProperty);
        ValueModel itemsValueModel = getOrCreateDisplayValueModel(selectableItemsProperty);
        JComboBox comboBox = createBoundComboBox(selectedValueModel,
                itemsValueModel, renderedItemProperty);
        return (JComboBox)bindControl(comboBox, selectionFormProperty);
    }

    public JComboBox createBoundComboBox(String selectionFormProperty,
            ValueModel selectableItemsHolder, String renderedItemProperty) {
        ValueModel selectedValueModel = getOrCreateDisplayValueModel(selectionFormProperty);
        JComboBox comboBox = createBoundComboBox(selectedValueModel,
                selectableItemsHolder, renderedItemProperty);
        return (JComboBox)bindControl(comboBox, selectionFormProperty);
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

    public JComboBox createBoundEnumComboBox(String selectionEnumProperty) {
        JComboBox comboBox = createNewComboBox();
        getComponentFactory().configureForEnum(comboBox,
                getEnumType(selectionEnumProperty));
        return bind(comboBox, selectionEnumProperty, (List)comboBox.getModel(),
                AbstractCodedEnum.DEFAULT_ORDER);
    }

    public JComboBox createBoundEnumComboBox(String selectionFormProperty,
            Constraint filter) {
        JComboBox comboBox = createBoundEnumComboBox(selectionFormProperty);
        return (JComboBox)bindControl(installFilter(comboBox, filter),
                selectionFormProperty);
    }

    private JComboBox installFilter(JComboBox comboBox, Constraint filter) {
        FilteredComboBoxListModel model = new FilteredComboBoxListModel(
                comboBox.getModel(), filter);
        comboBox.setModel(model);
        return comboBox;
    }

    public JComboBox bind(JComboBox comboBox, String selectionFormProperty,
            List selectableItems, Comparator itemsComparator) {
        ValueModel selectedValueModel = getOrCreateDisplayValueModel(selectionFormProperty);
        final ValueHolder valueHolder = new ValueHolder(selectableItems);
        final JComboBox boundCombo = bind(comboBox, selectedValueModel,
                valueHolder, itemsComparator);
        return (JComboBox)bindControl(boundCombo, selectionFormProperty);
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

    protected JComboBox createNewComboBox(ComboBoxModel model) {
        JComboBox box = createNewComboBox();
        box.setModel(model);
        return box;
    }

    protected JComboBox createNewComboBox() {
        return getComponentFactory().createComboBox();
    }

    private String getEnumType(String formProperty) {
        Class enumClass = getMetadataAccessStrategy().getPropertyType(
                formProperty);
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
     * <code>java.util.Collection</code> or an array to a ListModel. What this
     * does is ensure when items are added or removed to/from the list model,
     * the property's collection is also updated. Validation also occurs against
     * the property when it changes, etc. Changes to the collection managed by
     * the list model are buffered before being committed to the underlying bean
     * property. This prevents the domain object from having to worry about
     * returning a non-null instance.
     * 
     * @param formProperty
     * @return The bound list model.
     */
    public ObservableList createBoundListModel(String formProperty) {
        ValueModel valueModel = formModel.getValueModel(formProperty);
        if (valueModel == null) {
            PropertyAdapter adapter = new PropertyAdapter(formModel
                    .getPropertyAccessStrategy(), formProperty);
            valueModel = new BufferedCollectionValueModel(adapter,
                    getMetadataAccessStrategy().getPropertyType(formProperty));
            formModel.add(formProperty, valueModel);
        }
        return (ObservableList)valueModel.getValue();
    }

    public JList createBoundList(String formProperty) {
        ListModel listModel = createBoundListModel(formProperty);
        JList list = createNewList();
        list.setModel(listModel);
        return (JList)bindControl(list, formProperty);
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
        return (JList)bindControl(bind(list,
                getOrCreateDisplayValueModel(selectionFormProperty),
                selectableItemsHolder, itemsComparator), selectionFormProperty);
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
        list.setSelectedValue(selectedItemHolder.getValue(), true);
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
            selectedValueModel
                    .addValueChangeListener(new ValueChangeListener() {
                        public void valueChanged() {
                            if (selectedValueModel.getValue() != null) {
                                if (!updating) {
                                    list.setSelectedValue(selectedValueModel
                                            .getValue(), true);
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
                selectedValueModel.setValue(list.getSelectedValue());
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