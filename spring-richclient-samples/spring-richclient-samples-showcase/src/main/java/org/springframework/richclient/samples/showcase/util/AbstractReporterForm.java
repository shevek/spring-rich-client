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

	private ToggleCommand enableCommand;

	private ToggleCommand readOnlyCommand;

	private ToggleCommand validatingCommand;

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

	public ToggleCommand getReadOnlyFormModelCommand() {
		if (readOnlyCommand == null) {
			readOnlyCommand = new ToggleCommand(getReadOnlyCommandFaceDescriptorId()) {
				@Override
				protected void onSelection() {
					getFormModel().setReadOnly(true);
				}

				@Override
				protected void onDeselection() {
					getFormModel().setReadOnly(false);
				}
			};
			readOnlyCommand.setSelected(getFormModel().isReadOnly());
			getFormModel().addPropertyChangeListener(FormModel.READONLY_PROPERTY,
					new ToggleCommandPropertyChangeListener(readOnlyCommand));
			getCommandConfigurer().configure(readOnlyCommand);
		}
		return readOnlyCommand;
	}

	public ToggleCommand getEnableFormModelCommand() {
		if (enableCommand == null) {
			enableCommand = new ToggleCommand(getEnableCommandFaceDescriptorId()) {
				@Override
				protected void onSelection() {
					getFormModel().setEnabled(true);
				}

				@Override
				protected void onDeselection() {
					getFormModel().setEnabled(false);
				}
			};
			enableCommand.setSelected(getFormModel().isEnabled());
			getFormModel().addPropertyChangeListener(FormModel.ENABLED_PROPERTY,
					new ToggleCommandPropertyChangeListener(enableCommand));
			getCommandConfigurer().configure(enableCommand);
		}
		return enableCommand;
	}

	public ToggleCommand getValidatingFormModelCommand() {
		if (validatingCommand == null) {
			validatingCommand = new ToggleCommand(getValidatingCommandFaceDescriptorId()) {
				@Override
				protected void onSelection() {
					getFormModel().setValidating(true);
				}

				@Override
				protected void onDeselection() {
					getFormModel().setValidating(false);
				}
			};
			validatingCommand.setSelected(getFormModel().isValidating());
			getFormModel().addPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY,
					new ToggleCommandPropertyChangeListener(validatingCommand));
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

	protected void registerFormModelPropertyChangeListener() {
		ValidatingFormModel formModel = getFormModel();
		formModel.addPropertyChangeListener(FormModel.COMMITTABLE_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.DIRTY_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.ENABLED_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(FormModel.READONLY_PROPERTY, formModelPropertyChangeListener);
		formModel.addPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY, formModelPropertyChangeListener);
	}

	protected void unregisterFormModelPropertyChangeListener() {
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

	public static class ToggleCommandPropertyChangeListener implements PropertyChangeListener {

		private final ToggleCommand toggleCommand;

		public ToggleCommandPropertyChangeListener(ToggleCommand toggleCommand) {
			this.toggleCommand = toggleCommand;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			toggleCommand.setSelected((Boolean) evt.getNewValue());
		}
	}

	protected class LogPropertyChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			getMessageArea().append(evt.toString());
			getMessageArea().append("\n");
		}
	};
}
