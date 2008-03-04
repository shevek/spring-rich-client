package org.springframework.richclient.samples.showcase.conversion;

import javax.swing.JComponent;

import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class NumberConversionDialog extends TitledApplicationDialog {

	private class NumberClass {
		private int primitiveInt = 3;

		public int getPrimitiveInt() {
			return primitiveInt;
		}

		public void setPrimitiveInt(int primitiveInt) {
			this.primitiveInt = primitiveInt;
		}
	}

	private class NumberClassForm extends AbstractForm {

		public NumberClassForm() {
			super(FormModelHelper.createFormModel(new NumberClass()));
		}

		@Override
		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("primitiveInt");
			newSingleLineResultsReporter(NumberConversionDialog.this);
			return builder.getForm();
		}

	}

	@Override
	protected JComponent createTitledDialogContentPane() {
		return (new NumberClassForm()).getControl();
	}

	@Override
	protected boolean onFinish() {
		return true;
	}
}