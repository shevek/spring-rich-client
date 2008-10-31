/*
 * Copyright 2002-2004 the original author or authors.
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

import org.springframework.context.MessageSource;
import org.springframework.rules.constraint.Constraint;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.Severity;

/**
 * @author Keith Donald
 */
public class PropertyResults implements ValidationResults {

    private String propertyName;
    private Object rejectedValue;
    private Constraint violatedConstraint;
    private Severity severity = Severity.ERROR;
    private MessageTranslatorFactory messageTranslatorFactory;

    public PropertyResults(String propertyName, Object rejectedValue,
            Constraint violatedConstraint) {
        this.propertyName = propertyName;
        this.rejectedValue = rejectedValue;
        this.violatedConstraint = violatedConstraint;
        this.messageTranslatorFactory = (MessageTranslatorFactory) ApplicationServicesLocator.services().getService(MessageTranslatorFactory.class);
    }

    /**
     * @deprecated MessageSource is configured by MessageTranslator. use <code>buildMessage(Locale)</code>
     * @see #buildMessage(Locale)
     */
    public String buildMessage(MessageSource messages, Locale locale) {
		return buildMessage(locale);
    }

    public String buildMessage(Locale locale) {
    	MessageTranslator messageTranslator = messageTranslatorFactory.createTranslator(null, locale);    	
        return messageTranslator.getMessage(this);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public Constraint getViolatedConstraint() {
        return violatedConstraint;
    }

    public int getViolatedCount() {
        return new SummingVisitor(getViolatedConstraint()).sum();
    }

    public Severity getSeverity() {
        return severity;
    }

}
