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

import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.value.ValueModel;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.enums.AbstractLabeledEnum;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.control.PatchedJFormattedTextField;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.list.LabeledEnumListRenderer;
import org.springframework.richclient.util.Alignment;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.comparator.ComparableComparator;
import org.springframework.util.comparator.CompoundComparator;

/**
 * Default component factory implementation that delegates to JGoodies component
 * factory.
 * 
 * @author Keith Donald
 */
public class DefaultComponentFactory implements ComponentFactory {

    private final Log logger = LogFactory.getLog(getClass());

    private MessageSourceAccessor messages;

    private IconSource iconSource;

    private ButtonFactory buttonFactory;

    private MenuFactory menuFactory;

    private LabeledEnumResolver enumResolver = Application.services().getLabeledEnumResolver();

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    public void setButtonFactory(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public void setEnumResolver(LabeledEnumResolver enumResolver) {
        this.enumResolver = enumResolver;
    }

    public JLabel createLabel(String labelKey) {
        return getLabelInfo(getRequiredMessage(labelKey)).configureLabel(createNewLabel());
    }

    public JLabel createLabel(String[] labelKeys) {
        return getLabelInfo(getRequiredMessage(labelKeys)).configureLabel(createNewLabel());
    }

    public JLabel createLabel(String labelKey, Object[] arguments) {
        return getLabelInfo(getRequiredMessage(labelKey, arguments)).configureLabel(createNewLabel());
    }

    protected LabelInfo getLabelInfo(String label) {
        return new LabelInfoFactory(label).createLabelInfo();
    }

    protected String getRequiredMessage(String messageKey) {
        return getRequiredMessage(messageKey, null);
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
        return getMessages().getMessage(resolvable);
    }

    private MessageSourceAccessor getMessages() {
        if (messages == null) {
            return Application.services().getMessages();
        }
        return messages;
    }

    public JLabel createLabel(String labelKey, ValueModel[] argumentValueHolders) {
        return new LabelTextRefresher(labelKey, argumentValueHolders).getLabel();
    }

    private class LabelTextRefresher implements PropertyChangeListener {

        private String labelKey;

        private JLabel label;

        private ValueModel[] argumentHolders;

        public LabelTextRefresher(String labelKey, ValueModel[] argumentHolders) {
            this.labelKey = labelKey;
            this.argumentHolders = argumentHolders;
            this.label = createNewLabel();
            subscribe();
            updateLabel();
        }

        private void subscribe() {
            for (int i = 0; i < argumentHolders.length; i++) {
                ValueModel argHolder = argumentHolders[i];
                argHolder.addValueChangeListener(this);
            }
        }

        public JLabel getLabel() {
            return label;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            updateLabel();
        }

        private void updateLabel() {
            Object[] argValues = new Object[argumentHolders.length];
            for (int i = 0; i < argumentHolders.length; i++) {
                ValueModel argHolder = argumentHolders[i];
                argValues[i] = argHolder.getValue();
            }
            getLabelInfo(getRequiredMessage(labelKey, argValues)).configureLabel(label);
        }
    }

    private String getRequiredMessage(String messageKey, Object[] args) {
        try {
            String message = getMessages().getMessage(messageKey, args);
            return message;
        }
        catch (NoSuchMessageException e) {
            return messageKey;
        }
    }

    public JLabel createTitleLabel(String labelKey) {
        return com.jgoodies.forms.factories.DefaultComponentFactory.getInstance().createTitle(
                getRequiredMessage(labelKey));
    }

    public JComponent createTitledBorderFor(String labelKey, JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(getRequiredMessage(labelKey)),
                GuiStandardUtils.createEvenlySpacedBorder(UIConstants.ONE_SPACE)));
        return component;
    }

    public JLabel createLabelFor(String labelKey, JComponent component) {
        return getLabelInfo(getRequiredMessage(labelKey)).configureLabelFor(createNewLabel(), component);
    }

    public JLabel createLabelFor(String[] labelKeys, JComponent component) {
        return getLabelInfo(getRequiredMessage(labelKeys)).configureLabelFor(createNewLabel(), component);
    }

    protected JLabel createNewLabel() {
        return new JLabel();
    }

    public JButton createButton(String labelKey) {
        return (JButton)getButtonLabelInfo(getRequiredMessage(labelKey)).configure(getButtonFactory().createButton());
    }

    protected CommandButtonLabelInfo getButtonLabelInfo(String label) {
        return new LabelInfoFactory(label).createButtonLabelInfo();
    }

    protected ButtonFactory getButtonFactory() {
        if (buttonFactory == null) {
            return DefaultButtonFactory.instance();
        }
        return buttonFactory;
    }

    public JComponent createLabeledSeparator(String labelKey) {
        return createLabeledSeparator(labelKey, Alignment.LEFT);
    }

    public JCheckBox createCheckBox(String labelKey) {
        return (JCheckBox)getButtonLabelInfo(getRequiredMessage(labelKey)).configure(createNewCheckBox());
    }

    public JCheckBox createCheckBox(String[] labelKeys) {
        return (JCheckBox)getButtonLabelInfo(getRequiredMessage(labelKeys)).configure(createNewCheckBox());
    }

    protected JCheckBox createNewCheckBox() {
        return new JCheckBox();
    }

    public JMenuItem createMenuItem(String labelKey) {
        return (JMenuItem)getButtonLabelInfo(getRequiredMessage(labelKey)).configure(getMenuFactory().createMenuItem());
    }

    protected MenuFactory getMenuFactory() {
        if (menuFactory == null) {
            return DefaultMenuFactory.instance();
        }
        return menuFactory;
    }

    public JComponent createLabeledSeparator(String labelKey, Alignment alignment) {
        return com.jgoodies.forms.factories.DefaultComponentFactory.getInstance().createSeparator(
                getRequiredMessage(labelKey), ((Number)alignment.getCode()).intValue());
    }

    public JList createList() {
        return new JList();
    }

    public JComboBox createComboBox() {
        return new JComboBox();
    }

    public JComboBox createComboBox(Class enumType) {
        JComboBox comboBox = createComboBox();
        configureForEnum(comboBox, enumType);
        return comboBox;
    }

    public JComboBox createListValueModelComboBox(ValueModel selectedItemValueModel,
            ValueModel selectableItemsListHolder, String renderedPropertyPath) {
        return null;
    }

    public void configureForEnum(JComboBox comboBox, Class enumType) {
        Collection enumValues = enumResolver.getLabeledEnumSet(enumType);
        if (logger.isDebugEnabled()) {
            logger.debug("Populating combo box model with enums of type '" + enumType.getName() + "'; enums are ["
                    + enumValues + "]");
        }
        CompoundComparator comparator = new CompoundComparator();
        comparator.addComparator(AbstractLabeledEnum.LABEL_ORDER);
        comparator.addComparator(new ComparableComparator());
        comboBox.setModel(new ComboBoxListModel(new ArrayList(enumValues), comparator));
        comboBox.setRenderer(new LabeledEnumListRenderer(messageSource));
    }

    public JFormattedTextField createFormattedTextField(AbstractFormatterFactory formatterFactory) {
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

    public void addConfiguredTab(JTabbedPane tabbedPane, String labelKey, JComponent tabComponent) {
        LabelInfo info = getLabelInfo(getRequiredMessage(labelKey));
        tabbedPane.addTab(info.getText(), tabComponent);
        int tabIndex = tabbedPane.getTabCount() - 1;
        tabbedPane.setMnemonicAt(tabIndex, info.getMnemonic());
        tabbedPane.setDisplayedMnemonicIndexAt(tabIndex, info.getMnemonicIndex());
        tabbedPane.setIconAt(tabIndex, getIcon(labelKey));
        tabbedPane.setToolTipTextAt(tabIndex, getCaption(labelKey));
    }

    public JScrollPane createScrollPane() {
        return new JScrollPane();
    }

    public JScrollPane createScrollPane(Component view) {
        return new JScrollPane(view);
    }

    public JScrollPane createScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        return new JScrollPane(view, vsbPolicy, hsbPolicy);
    }

    public JPanel createPanel() {
        return new JPanel();
    }

    public JPanel createPanel(LayoutManager layoutManager) {
        return new JPanel(layoutManager);
    }

    private String getCaption(String labelKey) {
        return getOptionalMessage(labelKey + ".caption");
    }

    protected String getOptionalMessage(String messageKey) {
        return getMessages().getMessage(messageKey, (String)null);
    }

    private Icon getIcon(String labelKey) {
        return getIconSource().getIcon(labelKey);
    }

    private IconSource getIconSource() {
        if (iconSource == null) {
            return Application.services().getIconSource();
        }
        return iconSource;
    }

}