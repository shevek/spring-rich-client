package org.springframework.richclient.samples.showcase.validation;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.constraint.RelationalOperator;
import org.springframework.rules.constraint.StringLengthConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;

/**
 * Dialog showing a number of fields to manipulate the
 * {@link StringLengthConstraint} and see it in action on the value field.
 *
 * @author Jan Hoskens
 *
 */
public class StringLenghtConstraintDialog extends TitledApplicationDialog {

	public class StringLengthValue implements PropertyConstraintProvider {

		private int length = 4;

		private RelationalOperator relationalOperator = RelationalOperator.LESS_THAN_EQUAL_TO;

		private int minLength = 2;

		private int maxLength = 4;

		private boolean rangeConstraint = false;

		private String value;

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public RelationalOperator getRelationalOperator() {
			return relationalOperator;
		}

		public void setRelationalOperator(RelationalOperator relationalOperator) {
			this.relationalOperator = relationalOperator;
		}

		public int getMinLength() {
			return minLength;
		}

		public void setMinLength(int minLength) {
			this.minLength = minLength;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}

		public void setRangeConstraint(boolean rangeConstraint) {
			this.rangeConstraint = rangeConstraint;
		}

		public boolean isRangeConstraint() {
			return rangeConstraint;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public PropertyConstraint getPropertyConstraint(String propertyName) {
			if (isRangeConstraint()) {
				return new PropertyValueConstraint("value", new StringLengthConstraint(minLength, maxLength,
						"rangeConstraint"));
			}

			return new PropertyValueConstraint("value", new StringLengthConstraint(relationalOperator, length));
		}

	}

	public class StringLengthConstraintForm extends AbstractForm {

		public StringLengthConstraintForm() {
			super(FormModelHelper.createFormModel(new StringLengthValue(), false, "stringLengthValue"));
		}

		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("relationalOperator");
			builder.row();
			builder.add("length");
			builder.row();
			builder.add("minLength");
			builder.row();
			builder.add("maxLength");
			builder.row();
			builder.add("rangeConstraint");
			builder.row();
			builder.add("value");
			newSingleLineResultsReporter(StringLenghtConstraintDialog.this);
			JPanel panel = new JPanel();
			panel.add(builder.getForm());
			panel.add(createValidateCommand().createButton());
			return panel;
		}

		private ActionCommand createValidateCommand() {
			ActionCommand validateCommand = new ActionCommand("validateCommand") {

				protected void doExecuteCommand() {
					getFormModel().validate();
				}
			};
			((CommandConfigurer) ApplicationServicesLocator.services().getService(CommandConfigurer.class))
					.configure(validateCommand);
			return validateCommand;
		}

	}

	protected JComponent createTitledDialogContentPane() {

		return new StringLengthConstraintForm().getControl();
	}

	protected boolean onFinish() {
		return true;
	}

}