package org.springframework.richclient.samples.showcase.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JTextArea;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.ToggleCommand;
import org.springframework.richclient.form.AbstractForm;

/**
 * A Form with a {@link JTextArea} that can be used to show some useful
 * messages. Can be used to show events to clarify and examine how user input is
 * handled. Note that the actual TextArea should be inserted by the surrounding
 * component/dialog. This to be able to reuse the textArea for various purposes.
 *
 * @author Jan Hoskens
 */
public abstract class AbstractReporterForm extends AbstractForm implements Reporter {
	private JTextArea messageArea;

	private ActionCommand printFormObjectCommand;

	private ActionCommand printFormModelCommand;

	private ActionCommand printFieldsCommand;

	private StateSynchronizingToggleCommand enableCommand;

	private StateSynchronizingToggleCommand readOnlyCommand;

	private StateSynchronizingToggleCommand validatingCommand;

	private ToggleCommand logFormModelPropertyChangeCommand;

	private PropertyChangeListener formModelPropertyChangeListener = new LogPropertyChangeListener();

	public AbstractReporterForm(FormModel formModel) {
		super(formModel);
	}

	public AbstractReporterForm(FormModel formModel, String id) {
		super(formModel, id);
	}

	/**
	 * Set the textArea to write messages to.
	 */
	public void setMessageArea(JTextArea messageArea) {
		this.messageArea = messageArea;
	}

	/**
	 * Returns the textArea to append info.
	 */
	public JTextArea getMessageArea() {
		return messageArea;
	}

	/**
	 * Print the backing form object details. Default behaviour is to call
	 * toString() on the formObject.
	 */
	public StringBuilder getFormObjectDetails(StringBuilder builder, FormModel formModel) {
		builder.append("[FORMOBJECT " + formModel.getId() + "] ");
		builder.append(formModel.getFormObject());
		builder.append("\n");
		return builder;
	}

	/**
	 * Print all the values from the valueModels in the formModel. Default
	 * behaviour is to iterate over all fields and print their value.
	 */
	public StringBuilder getFieldsDetails(StringBuilder builder, FormModel formModel) {
		builder.append("[FIELDS " + formModel.getId() + "] ");
		Set<String> fieldNames = formModel.getFieldNames();
		for (String fieldName : fieldNames) {
			builder.append(fieldName).append(" = ");
			builder.append(formModel.getValueModel(fieldName).getValue());
			builder.append(", ");
		}
		builder.append("\n");
		return builder;
	}

	public StringBuilder getFormModelDetails(StringBuilder builder, FormModel formModel) {
		builder.append("[FORMMODEL " + formModel.getId() + "] ");
		builder.append(formModel.toString());
		builder.append("\n");
		return builder;
	}

	public ActionCommand getPrintFormObjectCommand() {
		if (printFormObjectCommand == null) {
			printFormObjectCommand = new ActionCommand(getPrintFormObjectCommandFaceDescriptorId()) {

				protected void doExecuteCommand() {
					getMessageArea().append(getFormObjectDetails(new StringBuilder(), getFormModel()).toString());
				}
			};
			getCommandConfigurer().configure(printFormObjectCommand);
		}
		return printFormObjectCommand;
	}

	public ActionCommand getPrintFieldsCommand() {
		if (printFieldsCommand == null) {
			printFieldsCommand = new ActionCommand(getPrintFieldsCommandFaceDescriptorId()) {

				protected void doExecuteCommand() {
					getMessageArea().append(getFieldsDetails(new StringBuilder(), getFormModel()).toString());
				}
			};
			getCommandConfigurer().configure(printFieldsCommand);
		}
		return printFieldsCommand;
	}

	public ActionCommand getPrintFormModelCommand() {
		if (printFormModelCommand == null) {
			printFormModelCommand = new ActionCommand(getPrintFormModelCommandFaceDescriptorId()) {

				protected void doExecuteCommand() {
					getMessageArea().append(getFormModelDetails(new StringBuilder(), getFormModel()).toString());
				}
			};
			getCommandConfigurer().configure(printFormModelCommand);
		}
		return printFormModelCommand;
	}

	public StateSynchronizingToggleCommand getReadOnlyFormModelCommand() {
		if (readOnlyCommand == null) {
			readOnlyCommand = new StateSynchronizingToggleCommand(getReadOnlyCommandFaceDescriptorId()) {
				@Override
				protected void doOnSelection() {
					getFormModel().setReadOnly(true);
				}

				@Override
				protected void doOnDeselection() {
					getFormModel().setReadOnly(false);
				}
			};
			readOnlyCommand.setSelected(getFormModel().isReadOnly());
			getFormModel().addPropertyChangeListener(FormModel.READONLY_PROPERTY,readOnlyCommand);
			getCommandConfigurer().configure(readOnlyCommand);
		}
		return readOnlyCommand;
	}

	public StateSynchronizingToggleCommand getEnableFormModelCommand() {
		if (enableCommand == null) {
			enableCommand = new StateSynchronizingToggleCommand(getEnableCommandFaceDescriptorId()) {
				@Override
				protected void doOnSelection() {
					getFormModel().setEnabled(true);
				}

				@Override
				protected void doOnDeselection() {
					getFormModel().setEnabled(false);
				}
			};
			enableCommand.setSelected(getFormModel().isEnabled());
			getFormModel().addPropertyChangeListener(FormModel.ENABLED_PROPERTY,enableCommand);
			getCommandConfigurer().configure(enableCommand);
		}
		return enableCommand;
	}

	public ToggleCommand getValidatingFormModelCommand() {
		if (validatingCommand == null) {
			validatingCommand = new StateSynchronizingToggleCommand(getValidatingCommandFaceDescriptorId()) {
				@Override
				protected void doOnSelection() {
					getFormModel().setValidating(true);
				}

				@Override
				protected void doOnDeselection() {
					getFormModel().setValidating(false);
				}
			};
			validatingCommand.setSelected(getFormModel().isValidating());
			getFormModel().addPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY,validatingCommand);
			getCommandConfigurer().configure(validatingCommand);
		}
		return validatingCommand;
	}

	public ToggleCommand getLogFormModelPropertyChangeCommand() {
		if (logFormModelPropertyChangeCommand == null) {
			logFormModelPropertyChangeCommand = new ToggleCommand(
					getLogFormModelPropertyChangeCommandFaceDescriptorId()) {
				@Override
				protected void onSelection() {
					registerFormModelPropertyChangeListener();
				}

				@Override
				protected void onDeselection() {
					unregisterFormModelPropertyChangeListener();
				}
			};
			getCommandConfigurer().configure(logFormModelPropertyChangeCommand);
		}
		return logFormModelPropertyChangeCommand;
	}

	public AbstractCommand[] getReporterCommands() {
		return new AbstractCommand[] { getPrintFormObjectCommand(), getPrintFormModelCommand(),
				getPrintFieldsCommand(), getCommitCommand(), getNewFormObjectCommand(), getRevertCommand(),
				getLogFormModelPropertyChangeCommand() };
	}

	public void registerFormModelPropertyChangeListener() {
		ValidatingFormModel formModel = getFormModel();
		formModel.addPropertyChangeListener(FormModel.COMMITTABLE_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.DIRTY_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.ENABLED_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.READONLY_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY, formModelPropertyChangeListener);
	}

	public void unregisterFormModelPropertyChangeListener() {
		ValidatingFormModel formModel = getFormModel();
		formModel.removePropertyChangeListener(FormModel.COMMITTABLE_PROPERTY, formModelPropertyChangeListener);
		formModel.removePropertyChangeListener(FormModel.DIRTY_PROPERTY, formModelPropertyChangeListener);
		formModel.removePropertyChangeListener(FormModel.ENABLED_PROPERTY, formModelPropertyChangeListener);
		formModel.removePropertyChangeListener(FormModel.READONLY_PROPERTY, formModelPropertyChangeListener);
		formModel
				.removePropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY, formModelPropertyChangeListener);
	}

	protected String getPrintFormObjectCommandFaceDescriptorId() {
		return "reporterForm.printFormObjectCommand";
	}

	protected String getPrintFieldsCommandFaceDescriptorId() {
		return "reporterForm.printFieldsCommand";
	}

	protected String getPrintFormModelCommandFaceDescriptorId() {
		return "reporterForm.printFormModelCommand";
	}

	protected String getEnableCommandFaceDescriptorId() {
		return "reporterForm.enableCommand";
	}

	protected String getReadOnlyCommandFaceDescriptorId() {
		return "reporterForm.readOnlyCommand";
	}

	protected String getValidatingCommandFaceDescriptorId() {
		return "reporterForm.validatingCommand";
	}

	protected String getLogFormModelPropertyChangeCommandFaceDescriptorId() {
		return "reporterForm.logFormModelPropertyChangeCommand";
	}

	protected String getCommitCommandFaceDescriptorId() {
		return "reporterForm.commitCommand";
	}

	protected String getRevertCommandFaceDescriptorId() {
		return "reporterForm.revertCommand";
	}

	protected String getNewFormObjectCommandId() {
		return "reporterForm.newCommand";
	}

	public static abstract class StateSynchronizingToggleCommand extends ToggleCommand implements PropertyChangeListener {

		private boolean isSynchronizing = false;

		public StateSynchronizingToggleCommand(String id) {
			super(id);
		}

		@Override
		protected final void onSelection() {
			if (!isSynchronizing)
				doOnSelection();
		}

		@Override
		protected final void onDeselection() {
			if (!isSynchronizing)
				doOnDeselection();
		}

		protected abstract void doOnSelection();

		protected abstract void doOnDeselection();

		public void propertyChange(PropertyChangeEvent evt) {
			isSynchronizing = true;
			setSelected((Boolean) evt.getNewValue());
			isSynchronizing = false;
		}
	}

	protected class LogPropertyChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			getMessageArea().append("[EVENT");
			if (evt.getSource() instanceof FormModel)
				getMessageArea().append(" " + ((FormModel)evt.getSource()).getId());
			getMessageArea().append("] property = " + evt.getPropertyName());
			getMessageArea().append(", oldValue = " + evt.getOldValue());
			getMessageArea().append(", newValue = " + evt.getNewValue());
			getMessageArea().append("\n");
		}
	};
}
