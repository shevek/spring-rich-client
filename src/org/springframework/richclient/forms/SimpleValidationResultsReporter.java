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
package org.springframework.richclient.forms;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidationEvent;
import org.springframework.binding.form.ValidationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.rules.reporting.DefaultMessageTranslator;
import org.springframework.rules.reporting.PropertyResults;
import org.springframework.rules.reporting.ValidationResults;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class SimpleValidationResultsReporter implements ValidationListener {
    private static final Log logger = LogFactory
            .getLog(SimpleValidationResultsReporter.class);

    private FormModel formModel;

    private Guarded guarded;

    private MessageReceiver messageAreaPane;

    private Stack messages = new Stack();

    public SimpleValidationResultsReporter(FormModel formModel,
            Guarded guarded, MessageReceiver messagePane) {
        Assert.notNull(formModel);
        Assert.notNull(guarded);
        Assert.notNull(messagePane);
        this.formModel = formModel;
        this.guarded = guarded;
        this.messageAreaPane = messagePane;
        initialize();
    }

    private void initialize() {
        if (formModel.getHasErrors()) {
            guarded.setEnabled(false);
        }
        else {
            guarded.setEnabled(true);
        }
        formModel.addValidationListener(this);
    }

    public void constraintSatisfied(ValidationEvent event) {
        remove(event);
        update(event);
    }

    public void constraintViolated(ValidationEvent event) {
        put(event);
        update(event);
    }

    private void remove(ValidationEvent event) {
        messages.remove(event);
    }

    private void put(ValidationEvent event) {
        int index = messages.indexOf(event);
        if (index == -1) {
            messages.push(event);
        }
        else {
            messages.remove(index);
            messages.push(event);
        }
    }

    public void clearErrors() {
        messages.clear();
        messageAreaPane.setMessage("", null);
        guarded.setEnabled(true);
    }
    
    private void update(ValidationEvent event) {
        if (!event.getFormModel().getHasErrors()) {
            messageAreaPane.setMessage("", null);
            guarded.setEnabled(true);
            if (logger.isDebugEnabled()) {
                logger
                        .debug("Form has no errors; enabling guarded component and clearing error message.");
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("Form has errors; disabling guarded component and setting error message.");
            }
            guarded.setEnabled(false);
            if (messages.size() > 0) {
                ValidationEvent error = (ValidationEvent)messages.peek();
                messageAreaPane.setMessage(translate(error.getResults()), error
                        .getResults().getSeverity());
            }
            else {
                messageAreaPane.setMessage("", null);
            }
        }
    }

    private String translate(ValidationResults results) {
        DefaultMessageTranslator messageTranslator = new DefaultMessageTranslator(
                Application.services());
        return messageTranslator.getMessage((PropertyResults)results);
    }

}