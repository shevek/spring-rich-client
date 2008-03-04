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
import org.springframework.rules.constraint.RegexpConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;
import org.springframework.util.StringUtils;

/**
 * Show a basic dialog with a regular expression input field and a value input
 * field. When entering a new regular expression, user can evaluate expression.
 *
 * @author Jan Hoskens
 *
 */
public class BasicRegExpConstraintDialog extends TitledApplicationDialog {

	/**
	 * This object is used as formObject. It contains a value to set, the
	 * regular expression that should be used while validating that value and
	 * the logic to get the {@link PropertyConstraint} for the value based on
	 * the regexp.
	 */
	private class RegExpValue implements PropertyConstraintProvider {
		private String regExp = "";

		private String value = "";

		public String getRegExp() {
			return regExp;
		}

		public void setRegExp(String regExp) {
			this.regExp = regExp;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public PropertyConstraint getPropertyConstraint(String propertyName) {
			if (StringUtils.hasText(regExp))
			{
				if ((propertyName == null) || ("value".equals(propertyName))) {
					return new PropertyValueConstraint("value", new RegexpConstraint(regExp, "regExpViolated"));
				}
			}
			return null;
		}

	}

	private class RegExpForm extends AbstractForm {

		public RegExpForm() {
			super(FormModelHelper.createFormModel(new RegExpValue(), false));
		}

		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("regExp");
			builder.row();
			builder.add("value");
			newSingleLineResultsReporter(BasicRegExpConstraintDialog.this);
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
		return new RegExpForm().getControl();
	}

	protected boolean onFinish() {
		return true;
	}

}