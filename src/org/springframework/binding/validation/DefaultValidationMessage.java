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

/**
 * Default implementation of ValidationMessage
 * 
 * @author  Oliver Hutchison
 */

public class DefaultValidationMessage implements ValidationMessage, Serializable {
    private String property;

    private Severity severity;

    private String message;

    public DefaultValidationMessage(String property, Severity severity, String message) {
        this.property = property;
        this.severity = severity;
        this.message = message;
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

    public String toString() {
        return new ToStringCreator(this).append("property", getProperty()).append("severity", getSeverity().getLabel()).append(
                "message", getMessage()).toString();
    }
}
