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

import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;

/**
 * Factory for <code>CheckBoxFormComponentInterceptorFactory</code> instances.
 * 
 * <ul>
 * <li>The label can be hidden using the <code>showLabel</code> property (default is <code>true</code>)</li>
 * <li>extra descriptive text can be shown using the <code>showText</code> property (default is <code>false</code>)</li>
 * <li>The key used to fetch the extra text (default is <code>text</code>) can be changed using the
 * <code>textKey</code> property</li>
 * </ul>
 * 
 * @author Peter De Bruycker
 * 
 */
public class CheckBoxFormComponentInterceptorFactory implements FormComponentInterceptorFactory, MessageSourceAware {

	private boolean showLabel = true;
	private boolean showText = false;

	private String textKey = "text";
	private MessageSource messageSource;

	public FormComponentInterceptor getInterceptor(FormModel formModel) {
		return new CheckBoxFormComponentInterceptor(formModel, messageSource, showLabel, showText, textKey);
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public boolean isShowText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public String getTextKey() {
		return textKey;
	}

	public void setTextKey(String textKey) {
		this.textKey = textKey;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
