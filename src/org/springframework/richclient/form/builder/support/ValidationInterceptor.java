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
package org.springframework.richclient.form.builder.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidationEvent;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.form.support.FormModelAwareMessageTranslator;
import org.springframework.core.EventListenerListHelper;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.reporting.PropertyResults;
import org.springframework.rules.reporting.Severity;
import org.springframework.rules.reporting.ValidationResults;

/**
 * @author oliverh
 */
public abstract class ValidationInterceptor extends AbstractFormComponentInterceptor {

    private final SimplePropertyValidationResultsReporter propertyValidatonReporter;

    public ValidationInterceptor(FormModel formModel) {
        super(formModel);
        propertyValidatonReporter = new SimplePropertyValidationResultsReporter();
    }

    protected void registerMessageReceiver(String propertyName, Messagable messageReceiver) {
        propertyValidatonReporter.registerMessageReceiver(propertyName, messageReceiver);
    }

    protected void registerGuarded(String propertyName, Guarded guarded) {
        propertyValidatonReporter.registerGuarded(propertyName, guarded);
    }

    private class SimplePropertyValidationResultsReporter implements ValidationListener {
        private Map propertyMessages = new HashMap();

        private Map propertyGuarded = new HashMap();

        private Map propertyMessage = new HashMap();

        private final FormModelAwareMessageTranslator messageTranslator = new FormModelAwareMessageTranslator(
                getFormModel(), Application.services());

        public SimplePropertyValidationResultsReporter() {
            getFormModel().addValidationListener(this);
        }

        public void registerGuarded(String propertyName, Guarded guarded) {
            getGuards(propertyName).add(guarded);
            update(propertyName);
        }

        private EventListenerListHelper getGuards(String propertyName) {
            EventListenerListHelper guards = (EventListenerListHelper)propertyGuarded.get(propertyName);
            if (guards == null) {
                guards = new EventListenerListHelper(Guarded.class);
                propertyGuarded.put(propertyName, guards);
            }
            return guards;
        }

        public void registerMessageReceiver(String propertyName, Messagable messageReceiver) {
            getMessageReceivers(propertyName).add(messageReceiver);
            update(propertyName);
        }

        private EventListenerListHelper getMessageReceivers(String propertyName) {
            EventListenerListHelper messageReceivers = (EventListenerListHelper)propertyMessage.get(propertyName);
            if (messageReceivers == null) {
                messageReceivers = new EventListenerListHelper(Messagable.class);
                propertyMessage.put(propertyName, messageReceivers);
            }
            return messageReceivers;
        }

        public void constraintSatisfied(ValidationEvent event) {
            String propertyName = getPropertyNameFrom(event);
            if (propertyName != null) {
                remove(propertyName, event);
                update(propertyName);
            }
        }

        public void constraintViolated(ValidationEvent event) {
            String propertyName = getPropertyNameFrom(event);
            if (propertyName != null) {
                put(propertyName, event);
                update(propertyName);
            }
        }

        private String getPropertyNameFrom(ValidationEvent event) {
            return event.getConstraint() instanceof PropertyConstraint ? ((PropertyConstraint)event.getConstraint()).getPropertyName()
                    : null;
        }

        private void remove(String propertyName, ValidationEvent event) {
            getMessages(propertyName).remove(event);
        }

        private Stack getMessages(String propertyName) {
            Stack messages = (Stack)propertyMessages.get(propertyName);
            if (messages == null) {
                messages = new Stack();
                propertyMessages.put(propertyName, messages);
            }
            return messages;
        }

        private void put(String propertyName, ValidationEvent event) {
            Stack messages = getMessages(propertyName);
            int index = messages.indexOf(event);
            if (index == -1) {
                messages.push(event);
            }
            else {
                messages.remove(index);
                messages.push(event);
            }
        }

        private void update(String propertyName) {
            final Stack messages = getMessages(propertyName);
            final Severity severity;
            final String messageText;
            final boolean enabled;

            if (messages.size() > 0) {
                ValidationEvent error = (ValidationEvent)messages.peek();
                severity = error.getResults().getSeverity();
                messageText = translate(error.getResults());
                enabled = false;
            }
            else {
                severity = null;
                messageText = "";
                enabled = true;
            }
            final Message message = new Message(messageText, severity);
            for (Iterator i = getMessageReceivers(propertyName).iterator(); i.hasNext();) {
                ((Messagable)i.next()).setMessage(message);
            }
            for (Iterator i = getGuards(propertyName).iterator(); i.hasNext();) {
                ((Guarded)i.next()).setEnabled(enabled);
            }
        }

        private String translate(ValidationResults results) {
            return messageTranslator.getMessage((PropertyResults)results);
        }
    }
}