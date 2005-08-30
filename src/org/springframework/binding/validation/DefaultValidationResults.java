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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.CachingMapDecorator;
import org.springframework.util.ObjectUtils;

public class DefaultValidationResults implements ValidationResults {

    private final List messages = new ArrayList();

    private CachingMapDecorator messagesSubLists = new CachingMapDecorator() {

        protected Object create(Object key) {
            List messagesSubList = new ArrayList();
            for (Iterator i = messages.iterator(); i.hasNext();) {
                ValidationMessage message = (ValidationMessage)i.next();
                if (key instanceof Severity && message.getSeverity().equals((Severity)key)) {
                    messagesSubList.add(message);
                }
                else if (ObjectUtils.nullSafeEquals(message.getProperty(), key)) {
                    messagesSubList.add(message);
                }
            }
            return Collections.unmodifiableList(messagesSubList);
        }

    };

    public DefaultValidationResults() {
    }

    public DefaultValidationResults(ValidationResults validationResults) {
        addAllMessages(validationResults);
    }

    public DefaultValidationResults(Collection validationMessages) {
        addAllMessages(validationMessages);
    }

    public void addAllMessages(ValidationResults validationResults) {
        addAllMessages(validationResults.getMessages());
    }

    public void addAllMessages(Collection validationMessages) {
        messages.addAll(validationMessages);
        messagesSubLists.clear();
    }

    public void addMessage(ValidationMessage validationMessage) {
        messages.add(validationMessage);
        messagesSubLists.clear();
    }

    public void addMessage(String field, Severity severity, String message) {
        addMessage(new DefaultValidationMessage(field, severity, message));
    }
    
    public void removeMessage(ValidationMessage message) {
        messages.remove(message);
        messagesSubLists.clear();        
    }

    public boolean getHasErrors() {
        return getMessageCount(Severity.ERROR) > 0;
    }

    public boolean getHasWarnings() {
        return getMessageCount(Severity.WARNING) > 0;
    }

    public boolean getHasInfo() {
        return getMessageCount(Severity.INFO) > 0;
    }

    public int getMessageCount() {
        return messages.size();
    }

    public int getMessageCount(Severity severity) {
        return getMessages(severity).size();
    }

    public int getMessageCount(String fieldName) {
        return getMessages(fieldName).size();
    }

    public List getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List getMessages(Severity severity) {
        return (List)messagesSubLists.get(severity);
    }

    public List getMessages(String fieldName) {
        return (List)messagesSubLists.get(fieldName);
    }

    public String toString() {
        return new ToStringCreator(this).append("messages", getMessages()).toString();
    }


}