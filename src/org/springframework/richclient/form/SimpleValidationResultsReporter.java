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
package org.springframework.richclient.form;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class SimpleValidationResultsReporter implements ValidationListener {
    private static final Log logger = LogFactory.getLog(SimpleValidationResultsReporter.class);

    private ValidationResultsModel resultsModel;

    private Guarded guarded;

    private Messagable messageReceiver;

    public SimpleValidationResultsReporter(ValidationResultsModel resultsModel, Guarded guarded,
            Messagable messageReceiver) {
        Assert.notNull(resultsModel, "resultsModel is required");
        Assert.notNull(guarded, "guarded is required");
        Assert.notNull(messageReceiver, "messagePane is required");
        this.resultsModel = resultsModel;
        this.guarded = guarded;
        this.messageReceiver = messageReceiver;
        init();
    }

    private void init() {
        if (resultsModel.getHasErrors()) {
            guarded.setEnabled(false);
        }
        else {
            guarded.setEnabled(true);
        }
        resultsModel.addValidationListener(this);
    }

    public void clearErrors() {
        messageReceiver.setMessage(null);
        guarded.setEnabled(true);
    }

    public void validationResultsChanged(ValidationResults results) {
        if (!results.getHasErrors()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Form has no errors; enabling guarded component and clearing error message.");
            }
            messageReceiver.setMessage(null);
            guarded.setEnabled(true);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Form has errors; disabling guarded component and setting error message.");
            }
            guarded.setEnabled(false);
            if (results.getMessageCount() > 0) {
                ValidationMessage message = getNewestMessage(results);
                messageReceiver.setMessage(new Message(message.getMessage(), message.getSeverity()));
            }
            else {
                messageReceiver.setMessage(null);
            }
        }
    }
    
    protected ValidationMessage getNewestMessage(ValidationResults results) {
        ValidationMessage newestMessage = null;
        for (Iterator i =  results.getMessages().iterator(); i.hasNext();) {
            ValidationMessage message =(ValidationMessage)i.next();
            if (newestMessage == null || newestMessage.getTimeStamp() < message.getTimeStamp()) {
                newestMessage = message;
            }
        }
        return newestMessage;
    }
}