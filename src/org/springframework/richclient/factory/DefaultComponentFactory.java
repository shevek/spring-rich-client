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
package org.springframework.richclient.factory;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.enums.AbstractCodedEnum;
import org.springframework.enums.CodedEnumResolver;
import org.springframework.enums.support.StaticCodedEnumResolver;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.control.PatchedJFormattedTextField;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.list.CodedEnumListRenderer;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.util.Alignment;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.comparator.ComparableComparator;
import org.springframework.util.comparator.CompoundComparator;

/**
 * Default component factory implementation that delegates to JGoodies component
 * factory.
 * 
 * @author Keith Donald
 */
public class DefaultComponentFactory extends ApplicationObjectSupport implements
        ComponentFactory {
    private CommandServices commandServices;

    private IconSource iconSource;

    private CodedEnumResolver enumResolver = StaticCodedEnumResolver.instance();

    public void setCommandServices(CommandServices commandServices) {
        this.commandServices = commandServices;
    }

    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    public void setEnumResolver(CodedEnumResolver enumResolver) {
        this.enumResolver = enumResolver;
    }

    protected ButtonFactory getButtonFactory() {
        if (commandServices == null) { return DefaultButtonFactory.instance(); }
        return commandServices.getButtonFactory();
    }

    protected MenuFactory getMenuFactory() {
        if (commandServices == null) { return DefaultMenuFactory.instance(); }
        return commandServices.getMenuFactory();
    }

    public JLabel createLabel(String labelKey) {
        return getLabelInfo(getRequiredMessage(labelKey)).configureLabel(
                createNewLabel());
    }

    public JLabel createLabel(String[] labelKeys) {
        return getLabelInfo(getRequiredMessage(labelKeys)).configureLabel(
                createNewLabel());
    }

    public JLabel createLabel(String labelKey, Object[] arguments) {
        return getLabelInfo(getRequiredMessage(labelKey, arguments))
                .configureLabel(createNewLabel());
    }

    public JLabel createLabel(String labelKey, ValueModel[] argumentValueHolders) {
        return new LabelTextRefresher(labelKey, argumentValueHolders)
                .getLabel();
    }

    private class LabelTextRefresher implements ValueChangeListener {
        private String labelKey;

        private JLabel label;

        private ValueModel[] argumentHolders;

        public LabelTextRefresher(String labelKey, ValueModel[] argumentHolders) {
            this.labelKey = labelKey;
            this.argumentHolders = argumentHolders;
            this.label = createNewLabel();
            updateLabel();
        }

        public JLabel getLabel() {
            return label;
        }

        public void valueChanged() {
            updateLabel();
        }

        private void updateLabel() {
            Object[] argValues = new Object[argumentHolders.length];
            for (int i = 0; i < argumentHolders.length; i++) {
                ValueModel argHolder = argumentHolders[i];
                argValues[i] = argHolder.getValue();
            }
            getLabelInfo(getRequiredMessage(labelKey, argValues))
                    .configureLabel(label);
        }
    }

    public JLabel createTitleLabel(String labelKey) {
        return com.jgoodies.forms.factories.DefaultComponentFactory
                .getInstance().createTitle(getRequiredMessage(labelKey));
    }

    public JComponent createTitledBorderFor(String labelKey,
            JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(getRequiredMessage(labelKey)),
                GuiStandardUtils
                        .createEvenlySpacedBorder(UIConstants.ONE_SPACE)));
        return component;
    }

    public JLabel createLabelFor(String labelKey, JComponent component) {
        return getLabelInfo(getRequiredMessage(labelKey)).configureLabelFor(
                createNewLabel(), component);
    }

    public JLabel createLabelFor(String[] labelKeys, JComponent component) {
        return getLabelInfo(getRequiredMessage(labelKeys)).configureLabelFor(
                createNewLabel(), component);
    }

    protected JLabel createNewLabel() {
        return new JLabel();
    }

    public JButton createButton(String labelKey) {
        return (JButton)getButtonLabelInfo(getRequiredMessage(labelKey))
                .configure(getButtonFactory().createButton());
    }

    public JComponent createLabeledSeparator(String labelKey) {
        return createLabeledSeparator(labelKey, Alignment.LEFT);
    }

    public JCheckBox createCheckBox(String labelKey) {
        return (JCheckBox)getButtonLabelInfo(getRequiredMessage(labelKey))
                .configure(createNewCheckBox());
    }

    public JCheckBox createCheckBox(String[] labelKeys) {
        return (JCheckBox)getButtonLabelInfo(getRequiredMessage(labelKeys))
                .configure(createNewCheckBox());
    }

    protected JCheckBox createNewCheckBox() {
        return new JCheckBox();
    }

    public JMenuItem createMenuItem(String labelKey) {
        return (JMenuItem)getButtonLabelInfo(getRequiredMessage(labelKey))
                .configure(getMenuFactory().createMenuItem());
    }

    public JComponent createLabeledSeparator(String labelKey,
            Alignment alignment) {
        return com.jgoodies.forms.factories.DefaultComponentFactory
                .getInstance().createSeparator(getRequiredMessage(labelKey),
                        ((Number)alignment.getCode()).intValue());
    }

    public JList createList() {
        return new JList();
    }

    public JComboBox createComboBox() {
        return new JComboBox();
    }

    public JComboBox createComboBox(String enumType) {
        JComboBox comboBox = createComboBox();
        configureForEnum(comboBox, enumType);
        return comboBox;
    }

    public JComboBox createListValueModelComboBox(
            ValueModel selectedItemValueModel,
            ValueModel selectableItemsListHolder, String renderedPropertyPath) {
        return null;
    }

    public void configureForEnum(JComboBox comboBox, Class enumClass) {
        configureForEnum(comboBox, ClassUtils.getShortNameAsProperty(enumClass));
    }

    public void configureForEnum(JComboBox comboBox, String enumType) {
        List enumValues = enumResolver.getEnumsAsList(enumType, null);
        if (logger.isDebugEnabled()) {
            logger.debug("Populating combo box model with enums of type '"
                    + enumType + "'; enums are [" + enumValues + "]");
        }
        CompoundComparator comparator = new CompoundComparator();
        comparator.addComparator(AbstractCodedEnum.LABEL_ORDER);
        comparator.addComparator(ComparableComparator.instance());
        comboBox.setModel(new ComboBoxListModel(enumValues, comparator));
        comboBox
                .setRenderer(new CodedEnumListRenderer(getApplicationContext()));
    }

    public JFormattedTextField createFormattedTextField(
            AbstractFormatterFactory formatterFactory) {
        return new PatchedJFormattedTextField(formatterFactory);
    }

    public JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setColumns(25);
        return tf;
    }

    public JTextArea createTextArea() {
        return new JTextArea();
    }

    public JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = createTextArea();
        textArea.setRows(rows);
        textArea.setColumns(columns);
        return textArea;
    }

    public JTextArea createTextAreaAsLabel() {
        return GuiStandardUtils.textAreaAsLabel(createTextArea());
    }

    public JTabbedPane createTabbedPane() {
        return new JTabbedPane();
    }

    public void addConfiguredTab(JTabbedPane tabbedPane, String labelKey,
            JComponent tabComponent) {
        LabelInfo info = getLabelInfo(getRequiredMessage(labelKey));
        tabbedPane.addTab(info.getText(), tabComponent);
        int tabIndex = tabbedPane.getTabCount() - 1;
        tabbedPane.setMnemonicAt(tabIndex, info.getMnemonic());
        tabbedPane.setDisplayedMnemonicIndexAt(tabIndex, info
                .getMnemonicIndex());
        tabbedPane.setIconAt(tabIndex, getIcon(labelKey));
        tabbedPane.setToolTipTextAt(tabIndex, getCaption(labelKey));
    }

    protected LabelInfo getLabelInfo(String label) {
        return new LabelInfoFactory(label).createLabelInfo();
    }

    protected CommandButtonLabelInfo getButtonLabelInfo(String label) {
        return new LabelInfoFactory(label).createButtonLabelInfo();
    }

    protected String getCaption(String labelKey) {
        return getOptionalMessage(labelKey + ".caption");
    }

    protected Icon getIcon(String labelKey) {
        if (iconSource != null) {
            return iconSource.getIcon(labelKey + ".icon");
        }
        else {
            return null;
        }
    }

    protected String getOptionalMessage(String messageKey) {
        if (getMessageSourceAccessor() != null) {
            return getMessageSourceAccessor().getMessage(messageKey,
                    (String)null);
        }
        else {
            return null;
        }
    }

    protected String getRequiredMessage(final String[] messageKeys) {
        MessageSourceResolvable resolvable = new MessageSourceResolvable() {
            public String[] getCodes() {
                return messageKeys;
            }

            public Object[] getArguments() {
                return null;
            }

            public String getDefaultMessage() {
                if (messageKeys.length > 0) {
                    return messageKeys[0];
                }
                else {
                    return "";
                }
            }
        };
        if (getMessageSourceAccessor() != null) {
            return getMessageSourceAccessor().getMessage(resolvable);
        }
        else {
            logger.warn("No message source is set; returning key "
                    + messageKeys[0]);
            return messageKeys[0];
        }
    }

    protected String getRequiredMessage(String messageKey) {
        return getRequiredMessage(messageKey, null);
    }

    protected String getRequiredMessage(String messageKey, Object[] args) {
        if (getMessageSourceAccessor() != null) {
            try {
                String message = getMessageSourceAccessor().getMessage(
                        messageKey, args);
                return message;
            }
            catch (NoSuchMessageException e) {
                return messageKey;
            }
        }
        else {
            logger
                    .warn("No message source is set; returning key "
                            + messageKey);
            return messageKey;
        }
    }

}