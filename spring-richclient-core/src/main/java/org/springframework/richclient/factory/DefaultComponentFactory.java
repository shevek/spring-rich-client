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

import javax.swing.AbstractButton;
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
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.value.ValueModel;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.enums.LabeledEnum;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.components.PatchedJFormattedTextField;
import org.springframework.richclient.core.LabelInfo;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.list.ComboBoxListModel;
import org.springframework.richclient.list.LabeledEnumComboBoxEditor;
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
public class DefaultComponentFactory implements ComponentFactory, MessageSourceAware {

	private final Log logger = LogFactory.getLog(getClass());

	private MessageSourceAccessor messages;

	private IconSource iconSource;

	private ButtonFactory buttonFactory;

	private MenuFactory menuFactory;

	private LabeledEnumResolver enumResolver;

	private MessageSource messageSource;

	private TableFactory tableFactory;

	private int textFieldColumns = 25;

	/**
	 * {@inheritDoc}
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		this.messages = new MessageSourceAccessor(messageSource);
	}

	/**
	 * Set the source for retrieving icons.
	 */
	public void setIconSource(IconSource iconSource) {
		this.iconSource = iconSource;
	}

	/**
	 * Set the button factory.
	 */
	public void setButtonFactory(ButtonFactory buttonFactory) {
		this.buttonFactory = buttonFactory;
	}

	/**
	 * Set the menu factory.
	 */
	public void setMenuFactory(MenuFactory menuFactory) {
		this.menuFactory = menuFactory;
	}

	/**
	 * Set the resolver used to create messages for enumerations.
	 *
	 * @see LabeledEnum
	 */
	public void setEnumResolver(LabeledEnumResolver enumResolver) {
		this.enumResolver = enumResolver;
	}

	/**
	 * Returns the resolver used for enumerations. Uses the
	 * {@link ApplicationServicesLocator} to find one if no resolver is
	 * explicitly set.
	 */
	protected LabeledEnumResolver getEnumResolver() {
		if (enumResolver == null) {
			enumResolver = (LabeledEnumResolver) ApplicationServicesLocator.services().getService(
					LabeledEnumResolver.class);
		}
		return enumResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public JLabel createLabel(String labelKey) {
		JLabel label = createNewLabel();
		getLabelInfo(getRequiredMessage(labelKey)).configureLabel(label);
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	public JLabel createLabel(String[] labelKeys) {
		JLabel label = createNewLabel();
		getLabelInfo(getRequiredMessage(labelKeys)).configureLabel(label);
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	public JLabel createLabel(String labelKey, Object[] arguments) {
		JLabel label = createNewLabel();
		getLabelInfo(getRequiredMessage(labelKey, arguments)).configureLabel(label);
		return label;
	}

	/**
	 * Parse the given label to create a {@link LabelInfo}.
	 *
	 * @param label The label to parse.
	 * @return a {@link LabelInfo} representing the label.
	 * @see LabelInfo#valueOf(String)
	 */
	protected LabelInfo getLabelInfo(String label) {
		return LabelInfo.valueOf(label);
	}

	/**
	 * Get the message for the given key. Don't throw an exception if it's not
	 * found but return a default value.
	 *
	 * @param messageKey Key to lookup the message.
	 * @return the message found in the resources or a default message.
	 */
	protected String getRequiredMessage(String messageKey) {
		return getRequiredMessage(messageKey, null);
	}

	/**
	 * Get the message for the given key. Don't throw an exception if it's not
	 * found but return a default value.
	 *
	 * @param messageKeys The keys to use when looking for the message.
	 * @return the message found in the resources or a default message.
	 */
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
				return "";
			}
		};
		return getMessages().getMessage(resolvable);
	}

	/**
	 * Returns the messageSourceAccessor. Uses the
	 * {@link ApplicationServicesLocator} to find one if no accessor is
	 * explicitly set.
	 */
	private MessageSourceAccessor getMessages() {
		if (messages == null) {
			messages = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
					MessageSourceAccessor.class);
		}
		return messages;
	}

	/**
	 * {@inheritDoc}
	 */
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
		component.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(getRequiredMessage(labelKey)), GuiStandardUtils
				.createEvenlySpacedBorder(UIConstants.ONE_SPACE)));
		return component;
	}

	public JLabel createLabelFor(String labelKey, JComponent component) {
		JLabel label = createNewLabel();
		getLabelInfo(getRequiredMessage(labelKey)).configureLabelFor(label, component);
		return label;
	}

	public JLabel createLabelFor(String[] labelKeys, JComponent component) {
		JLabel label = createNewLabel();
		getLabelInfo(getRequiredMessage(labelKeys)).configureLabelFor(label, component);
		return label;
	}

	protected JLabel createNewLabel() {
		return new JLabel();
	}

	public JButton createButton(String labelKey) {
		return (JButton) getButtonLabelInfo(getRequiredMessage(labelKey)).configure(getButtonFactory().createButton());
	}

	protected CommandButtonLabelInfo getButtonLabelInfo(String label) {
		return CommandButtonLabelInfo.valueOf(label);
	}

	protected ButtonFactory getButtonFactory() {
		if (buttonFactory == null) {
			buttonFactory = (ButtonFactory) ApplicationServicesLocator.services().getService(ButtonFactory.class);
		}
		return buttonFactory;
	}

	public JComponent createLabeledSeparator(String labelKey) {
		return createLabeledSeparator(labelKey, Alignment.LEFT);
	}

	public JCheckBox createCheckBox(String labelKey) {
		return (JCheckBox) getButtonLabelInfo(getRequiredMessage(labelKey)).configure(createNewCheckBox());
	}

	public JCheckBox createCheckBox(String[] labelKeys) {
		return (JCheckBox) getButtonLabelInfo(getRequiredMessage(labelKeys)).configure(createNewCheckBox());
	}

	protected JCheckBox createNewCheckBox() {
		return new JCheckBox();
	}

	public JToggleButton createToggleButton(String labelKey) {
		return (JToggleButton) getButtonLabelInfo(getRequiredMessage(labelKey)).configure(createNewToggleButton());
	}

	public JToggleButton createToggleButton(String[] labelKeys) {
		return (JToggleButton) getButtonLabelInfo(getRequiredMessage(labelKeys)).configure(createNewToggleButton());
	}

	protected AbstractButton createNewToggleButton() {
		return new JToggleButton();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.richclient.factory.ComponentFactory#createRadioButton(java.lang.String)
	 */
	public JRadioButton createRadioButton(String labelKey) {
		return (JRadioButton) getButtonLabelInfo(getRequiredMessage(labelKey)).configure(createNewRadioButton());
	}

	protected JRadioButton createNewRadioButton() {
		return new JRadioButton();
	}

	public JRadioButton createRadioButton(String[] labelKeys) {
		return (JRadioButton) getButtonLabelInfo(getRequiredMessage(labelKeys)).configure(createNewRadioButton());
	}

	public JMenuItem createMenuItem(String labelKey) {
		return (JMenuItem) getButtonLabelInfo(getRequiredMessage(labelKey))
				.configure(getMenuFactory().createMenuItem());
	}

	protected MenuFactory getMenuFactory() {
		if (menuFactory == null) {
			menuFactory = (MenuFactory) ApplicationServicesLocator.services().getService(MenuFactory.class);
		}
		return menuFactory;
	}

	public JComponent createLabeledSeparator(String labelKey, Alignment alignment) {
		return com.jgoodies.forms.factories.DefaultComponentFactory.getInstance().createSeparator(
				getRequiredMessage(labelKey), ((Number) alignment.getCode()).intValue());
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
		Collection enumValues = getEnumResolver().getLabeledEnumSet(enumType);
		if (logger.isDebugEnabled()) {
			logger.debug("Populating combo box model with enums of type '" + enumType.getName() + "'; enums are ["
					+ enumValues + "]");
		}
		CompoundComparator comparator = new CompoundComparator();
		comparator.addComparator(LabeledEnum.LABEL_ORDER);
		comparator.addComparator(new ComparableComparator());
		comboBox.setModel(new ComboBoxListModel(new ArrayList(enumValues), comparator));
		comboBox.setRenderer(new LabeledEnumListRenderer(messageSource));
		comboBox.setEditor(new LabeledEnumComboBoxEditor(messageSource, comboBox.getEditor()));
	}

	/**
	 * Returns the default column count for new text fields (including formatted
	 * text and password fields)
	 *
	 * @return the default column count. Must not be lower than 0
	 * @see JTextField
	 */
	public int getTextFieldColumns() {
		return textFieldColumns;
	}

	/**
	 * Defines the default column count for new text fields (including formatted
	 * text and password fields)
	 *
	 * @param the default column count. Must not be lower than 0
	 * @see JTextField
	 */
	public void setTextFieldColumns(int columns) {
		if (columns < 0)
			throw new IllegalArgumentException("text field columns must not be lower than 0. Value was: " + columns);
		this.textFieldColumns = columns;
	}

	public JFormattedTextField createFormattedTextField(AbstractFormatterFactory formatterFactory) {
		PatchedJFormattedTextField patchedJFormattedTextField = new PatchedJFormattedTextField(formatterFactory);
		configureTextField(patchedJFormattedTextField);
		return patchedJFormattedTextField;
	}

	public JTextField createTextField() {
		JTextField textField = new JTextField();
		configureTextField(textField);
		return textField;
	}

	/**
	 * Configures the text field.
	 *
	 * @param textField the field to configure. Must not be null
	 */
	protected void configureTextField(JTextField textField) {
		textField.setColumns(getTextFieldColumns());
	}

	public JPasswordField createPasswordField() {
		JPasswordField passwordField = new JPasswordField();
		configureTextField(passwordField);
		return passwordField;
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
		org.springframework.richclient.core.LabelInfo info = getLabelInfo(getRequiredMessage(labelKey));
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
		return getMessages().getMessage(messageKey, (String) null);
	}

	private Icon getIcon(String labelKey) {
		return getIconSource().getIcon(labelKey);
	}

	/**
	 * Returns the icon source. Uses the {@link ApplicationServicesLocator} to
	 * find one if none was set explicitly.
	 */
	private IconSource getIconSource() {
		if (iconSource == null) {
			iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
		}
		return iconSource;
	}

	/**
	 * Construct a JTable with a default model It will delegate the creation to
	 * a TableFactory if it exists.
	 *
	 * @param model the table model
	 * @return The new table.
	 */
	public JTable createTable() {
		return (tableFactory != null) ? tableFactory.createTable() : new JTable();
	}

	/**
	 * Construct a JTable with the specified table model. It will delegate the
	 * creation to a TableFactory if it exists.
	 *
	 * @param model the table model
	 * @return The new table.
	 */
	public JTable createTable(TableModel model) {
		return (tableFactory != null) ? tableFactory.createTable(model) : new JTable(model);
	}

	/**
	 * Allow configuration via XML of a table factory. A simple interface for
	 * creating JTable object, this allows the developer to create an
	 * application specific table factory where, say, each tables have a set of
	 * renderers installed, are sortable, etc.
	 *
	 * @param tableFactory the table factory to use
	 */
	public void setTableFactory(TableFactory tableFactory) {
		this.tableFactory = tableFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	public JComponent createToolBar() {
		JToolBar toolBar = new JToolBar();

		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		return toolBar;
	}
}