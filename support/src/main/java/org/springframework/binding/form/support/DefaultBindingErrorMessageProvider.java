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
package org.springframework.binding.form.support;

import org.springframework.beans.PropertyAccessException;
import org.springframework.binding.form.BindingErrorMessageProvider;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.support.DefaultValidationMessage;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.Severity;

/**
 * Default implementation of <code>BindingErrorMessageProvider</code>.
 * 
 * @author Oliver Hutchison
 */
public class DefaultBindingErrorMessageProvider implements BindingErrorMessageProvider {

    private MessageSourceAccessor messageSourceAccessor = null;

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    protected MessageSourceAccessor getMessageSourceAccessor() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor)ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
        }
        return messageSourceAccessor;
    }

    public ValidationMessage getErrorMessage(FormModel formModel, String propertyName, Object valueBeingSet, Exception e) {
        String messageCode = getMessageCodeForException(e);
        Object[] args = new Object[] {formModel.getFieldFace(propertyName).getDisplayName(),
                UserMetadata.isFieldProtected(formModel, propertyName) ? "***" : valueBeingSet};
        String message = getMessageSourceAccessor().getMessage(messageCode, args, messageCode);
        return new DefaultValidationMessage(propertyName, Severity.ERROR, message);
    }

    protected String getMessageCodeForException(Exception e) {
        if (e instanceof PropertyAccessException) {
            return ((PropertyAccessException)e).getErrorCode();
        }
        else if (e instanceof NullPointerException) {
            return "required";
        }
        else if (e instanceof InvalidFormatException) {
            return "typeMismatch";
        }
        else if (e instanceof IllegalArgumentException) {
            return "typeMismatch";
        }
        else if (e.getCause() instanceof Exception) {
            return getMessageCodeForException((Exception)e.getCause());
        }
        else {
            return "unknown";
        }
    }
    }