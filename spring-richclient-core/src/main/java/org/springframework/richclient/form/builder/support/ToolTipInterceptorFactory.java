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
package org.springframework.richclient.form.builder.support;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;

/**
 * If a form property has a <code>caption</code> defined in the
 * messages.properties file it will be used as the tooltip for the form
 * component.
 * 
 * @author Peter De Bruycker
 */
public class ToolTipInterceptorFactory implements FormComponentInterceptorFactory {
	private boolean processComponent = true;

	private boolean processLabel = true;

	public FormComponentInterceptor getInterceptor(FormModel formModel) {
		return new ToolTipInterceptor(formModel);
	}

	private class ToolTipInterceptor extends AbstractFormComponentInterceptor {
		private FormModel formModel;

		public ToolTipInterceptor(FormModel formModel) {
			this.formModel = formModel;
		}

		String getCaption(String propertyName) {
			return formModel.getFieldFace(propertyName).getCaption();
		}

		public void processComponent(final String propertyName, final JComponent component) {
			if (processComponent) {
				component.setToolTipText(getCaption(propertyName));
			}
		}

		public void processLabel(String propertyName, JComponent label) {
			if (processLabel) {
				label.setToolTipText(getCaption(propertyName));
			}
		}
	}
	
	public void setProcessComponent(boolean processComponent) {
		this.processComponent = processComponent;
	}
	
	public void setProcessLabel(boolean processLabel) {
		this.processLabel = processLabel;
	}
}
