/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.rules.reporting;

import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;

/**
 * @author Mathias Broekelmann
 * 
 */
public class DefaultMessageTranslatorFactory implements
		MessageTranslatorFactory, MessageSourceAware, InitializingBean {

	private MessageSource messageSource;

	public MessageTranslator createTranslator(ObjectNameResolver resolver) {
		return createTranslator(resolver, Locale.getDefault());
	}

	public MessageTranslator createTranslator(ObjectNameResolver resolver,
			Locale locale) {
		return new DefaultMessageTranslator(messageSource, resolver, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(messageSource, "messageSource has not been set");
	}
}
