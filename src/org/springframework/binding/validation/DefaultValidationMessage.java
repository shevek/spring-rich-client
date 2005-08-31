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
package org.springframework.binding.validation;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Default implementation of ValidationMessage
 * 
 * @author  Oliver Hutchison
 */
public class DefaultValidationMessage implements ValidationMessage, Serializable {
    private final long timeStamp;

    private final String property;

    private final Severity severity;

    private final String message;

    public DefaultValidationMessage(String property, Severity severity, String message) {
        Assert.required(severity, "severity");
        Assert.required(message, "message");
        this.timeStamp = System.currentTimeMillis();
        this.property = property;
        this.severity = severity;
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getProperty() {
        return property;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
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