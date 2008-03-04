package org.springframework.richclient.samples.showcase.binding;

import javax.swing.JComponent;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.form.support.DefaultFieldMetadata;
import org.springframework.binding.form.support.ReadOnlyFieldMetadata;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.MessageFormatValueModel;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class DerivedValueModelDialog extends TitledApplicationDialog {

	private class Values {
		private String name;

		private String surname;

		private String title;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSurname() {
			return surname;
		}

		public void setSurname(String surname) {
			this.surname = surname;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	private class MessageValueModelForm extends AbstractForm {

		public MessageValueModelForm() {
			super(FormModelHelper.createFormModel(new Values()));
		}

		@Override
		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("name");
			builder.row();
			builder.add("surname");
			builder.row();
			builder.add("title");
			builder.row();
			ConfigurableFormModel formModel = getFormModel();
			ValueModel derivedValueModel = new MessageFormatValueModel("{2} {1} {0}", new ValueModel[] {
					getValueModel("name"), getValueModel("surname"), getValueModel("title") });
			FieldMetadata fieldMetaData = new ReadOnlyFieldMetadata(getFormModel(), String.class);
			formModel.add("derivedValue", derivedValueModel, fieldMetaData);
			builder.add("derivedValue");
			return builder.getForm();
		}

	}

	@Override
	protected JComponent createTitledDialogContentPane() {
		return (new MessageValueModelForm()).getControl();
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

}
