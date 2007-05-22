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
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.util.Assert;

/**
 * An implementation of ValidationResultsReporter that reports only a single
 * message from the configured validation results models. If there are any
 * errors reported on this or any child's model, then the Guarded object will be
 * disabled and the associated message receiver will be given the newest message
 * posted on the results model.
 * 
 * @author Keith Donald
 */
public class SimpleValidationResultsReporter implements ValidationResultsReporter {
	private static final Log logger = LogFactory.getLog(SimpleValidationResultsReporter.class);

	private ValidationResultsModel resultsModel;

	private Messagable messageReceiver;

	/**
	 * Constructor.
	 * @param formModel ValidatingFormModel to monitor and report on.
	 * @param messageReceiver The receiver for validation messages.
	 */
	public SimpleValidationResultsReporter(ValidationResultsModel resultsModel, Messagable messageReceiver) {
		Assert.notNull(resultsModel, "resultsModel is required");
		Assert.notNull(messageReceiver, "messagePane is required");
		this.resultsModel = resultsModel;
		this.messageReceiver = messageReceiver;
		init();
	}

	private void init() {
		resultsModel.addValidationListener(this);

		// Update state based on current results model
        validationResultsChanged( null );
	}

	public void clearErrors() {
		messageReceiver.setMessage(null);
	}

	/**
	 * Handle a change in the validation results model. Update the guarded
	 * object and message receiver based on our current results model state.
	 */
    public void validationResultsChanged(ValidationResults results) {
        // If our model is clean, then we need to see if any of our children have errors.
        // If not, then we have our parent update since we may have siblings that need to
		// report there status.

		if (!resultsModel.getHasErrors()) {
			messageReceiver.setMessage(null);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Form has errors; setting error message.");
			}
			ValidationMessage message = getNewestMessage(resultsModel);
			messageReceiver.setMessage(message == null ? null
					: new Message(message.getMessage(), message.getSeverity()));
		}
	}

	/**
	 * Search the newest message in the given resultsModel.
	 * 
	 * @param resultsModel Search this model to find the newest message.
	 * @return the newest message in this resultsModel.
	 */
	protected ValidationMessage getNewestMessage(ValidationResults resultsModel) {
		ValidationMessage newestMessage = null;
		for (Iterator i = resultsModel.getMessages().iterator(); i.hasNext();) {
			ValidationMessage message = (ValidationMessage) i.next();
			if (newestMessage == null || newestMessage.getTimeStamp() < message.getTimeStamp()) {
				newestMessage = message;
			}
		}
		return newestMessage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.richclient.form.ValidationResultsReporter#hasErrors()
	 */
	public boolean hasErrors() {
		return resultsModel.getHasErrors();
	}
}
