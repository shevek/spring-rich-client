/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.richclient.samples.showcase.binding;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.form.support.ReadOnlyFieldMetadata;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.MessageFormatValueModel;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.binding.swing.date.NachoCalendarDateFieldBinder;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class CalendarBindingDialog extends TitledApplicationDialog {

	private class Values {
		private String name;

		private String surname;

		private Date birthday;

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

		public Date getBirthday() {
			return birthday;
		}

		public void setBirthday(Date birthday) {
			this.birthday = birthday;
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

			Map<String, Object> context = new HashMap<String, Object>();
			context.put(NachoCalendarDateFieldBinder.SHOW_OK_CANCEL_KEY, Boolean.TRUE);
			context.put(NachoCalendarDateFieldBinder.SHOW_WEEKNUMBERS_KEY, Boolean.TRUE);
//			context.put(NachoCalendarDateFieldBinder.DATE_FORMAT, "MM'/'yyyy");
			context.put(NachoCalendarDateFieldBinder.WORKING_DAYS_KEY, new boolean[] {true, true, true, false, false, false, false});
			final SwingBindingFactory bf = (SwingBindingFactory) getBindingFactory();
			Binding b = bf.createBinding("birthday", context);
			builder.add(b);
//			builder.add("birthday");
			builder.row();
			ConfigurableFormModel formModel = getFormModel();
			ValueModel derivedValueModel = new MessageFormatValueModel("{0} {1} was born on {2}", new ValueModel[] {
					getValueModel("name"), getValueModel("surname"), getValueModel("birthday") });
			FieldMetadata fieldMetaData = new ReadOnlyFieldMetadata(getFormModel(), String.class);
			formModel.add("derivedBirthday", derivedValueModel, fieldMetaData);
			builder.add("derivedBirthday");
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
