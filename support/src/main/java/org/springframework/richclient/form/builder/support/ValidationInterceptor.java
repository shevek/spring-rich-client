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

import java.util.Iterator;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;

/**
 * @author oliverh
 */
public abstract class ValidationInterceptor extends AbstractFormComponentInterceptor {

    private final ValidationResultsModel validationResults;

    public ValidationInterceptor(FormModel formModel) {
        super(formModel);
        validationResults = ((ValidatingFormModel)formModel).getValidationResults();
    }

    protected ValidationListener registerMessageReceiver(String propertyName, Messagable messageReceiver) {
        MessagableValidationListener messagableValidationListener = new MessagableValidationListener(propertyName,
                messageReceiver);
        validationResults.addValidationListener(propertyName, messagableValidationListener);
        return messagableValidationListener;
    }

    protected ValidationListener registerGuarded(String propertyName, Guarded guarded) {
        GuardedValidationListener guardedValidationListener = new GuardedValidationListener(propertyName, guarded);
        validationResults.addValidationListener(propertyName, guardedValidationListener);
        return guardedValidationListener;
    }

    private static class MessagableValidationListener implements ValidationListener {
        private final String propertyName;

        private final Messagable messageReceiver;

        public MessagableValidationListener(String propertyName, Messagable messageReceiver) {
            this.propertyName = propertyName;
            this.messageReceiver = messageReceiver;
        }

        public void validationResultsChanged(ValidationResults results) {
            if (results.getMessageCount(propertyName) > 0) {
                ValidationMessage message = getNewestMessage(results);
                messageReceiver.setMessage(new Message(message.getMessage(), message.getSeverity()));
            }
            else {
                messageReceiver.setMessage(null);
            }
        }

        protected ValidationMessage getNewestMessage(ValidationResults results) {
            ValidationMessage newestMessage = null;
            for (Iterator i = results.getMessages(propertyName).iterator(); i.hasNext();) {
                ValidationMessage message = (ValidationMessage)i.next();
                if (newestMessage == null || newestMessage.getTimeStamp() < message.getTimeStamp()) {
                    newestMessage = message;
                }
            }
            return newestMessage;
        }
    }

    private static class GuardedValidationListener implements ValidationListener {
        private final String propertyName;

        private final Guarded guarded;

        public GuardedValidationListener(String propertyName, Guarded guarded) {
            this.propertyName = propertyName;
            this.guarded = guarded;
        }

        public void validationResultsChanged(ValidationResults results) {
            guarded.setEnabled(results.getMessageCount(propertyName) == 0);
        }
    }
}