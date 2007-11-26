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
package org.springframework.binding.form.support;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.form.FieldFace;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.core.LabelInfo;

/**
 * Testcase for <code>DefaultBindingErrorMessageProvider</code>
 * 
 * @author Peter De Bruycker
 */
public class DefaultBindingErrorMessageProviderTests extends TestCase {

	public void testGetErrorMessage() {
		DefaultBindingErrorMessageProvider provider = new DefaultBindingErrorMessageProvider();

		TestAbstractFormModel formModel = new TestAbstractFormModel(new Object()) {
			public FieldFace getFieldFace(String field) {
				return new DefaultFieldFace("Some Property", "", "", new LabelInfo("Some Property"), null);
			}
		};
		formModel.add("someProperty", new ValueHolder("value"));

		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("typeMismatch", Locale.getDefault(), "{0} has an invalid format \"{1}\"");
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		provider.setMessageSourceAccessor(messageSourceAccessor);

		ValidationMessage message = provider.getErrorMessage(formModel, "someProperty", "new value",
				new IllegalArgumentException());

		assertNotNull(message);
		assertEquals("someProperty", message.getProperty());
		assertEquals("Some Property has an invalid format \"new value\"", message.getMessage());
	}

	public void testGetMessageCodeForException() {
		DefaultBindingErrorMessageProvider provider = new DefaultBindingErrorMessageProvider();

		assertEquals("typeMismatch", provider.getMessageCodeForException(new TypeMismatchException(new Object(),
				String.class)));
		assertEquals("required", provider.getMessageCodeForException(new NullPointerException()));
		assertEquals("typeMismatch", provider.getMessageCodeForException(new InvalidFormatException("", "")));
		assertEquals("typeMismatch", provider.getMessageCodeForException(new IllegalArgumentException()));
		assertEquals("required", provider.getMessageCodeForException(new RuntimeException(new NullPointerException())));
		assertEquals("unknown", provider.getMessageCodeForException(new UnsupportedOperationException()));
	}

}
