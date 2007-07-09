/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.validation.support;

import org.springframework.binding.validation.ValidationMessage;
import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Severity;
import org.springframework.util.ObjectUtils;

/**
 * Default implementation of ValidationMessage
 * 
 * @author  Oliver Hutchison
 */
public class DefaultValidationMessage extends DefaultMessage implements ValidationMessage {
    private final String property;

    public DefaultValidationMessage(String property, Severity severity, String message) {
        super(message, severity);
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public int hashCode() {
        return (getProperty() != null ? (getProperty().hashCode() * 27) : 0) + (getSeverity().getShortCode() * 9)
                + getMessage().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        DefaultValidationMessage m2 = (DefaultValidationMessage)o;
        return ObjectUtils.nullSafeEquals(getProperty(), m2.getProperty()) && getSeverity().equals(m2.getSeverity())
                && getMessage().equals(m2.getMessage());
    }

    public String toString() {
        return new ToStringCreator(this).append("property", getProperty())
                .append("severity", getSeverity().getLabel())
                .append("message", getMessage())
                .toString();
    }
}