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
package org.springframework.richclient.dialog;

import java.util.Arrays;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.springframework.rules.reporting.Severity;
import org.springframework.util.ObjectUtils;

/**
 * A concrete implementation of the MessageReceiver interface. Primarily
 * intended to be used as a delegate for the MessageReceiver functionality of
 * more complex classes.
 * 
 * @author oliverh
 * @see DefaultMessageAreaPane
 */
public class DefaultMessageAreaModel implements MessageAreaModel {

	private MessageAreaModel delegate;

	private String message;

	private Severity severity;

	private EventListenerList listenerList = new EventListenerList();

	public DefaultMessageAreaModel() {
		this.delegate = this;
	}

	public DefaultMessageAreaModel(MessageAreaModel delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return Returns the delegateFor.
	 */
	protected MessageAreaModel getDelegateFor() {
		return delegate;
	}

	public String getMessage() {
		return message;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setMessage(String newMessage) {
		setMessage(newMessage, Severity.INFO);
	}

	public void setErrorMessage(String errorMessage) {
		setMessage(errorMessage, Severity.ERROR);
	}

	public void setMessage(String message, Severity severity) {
		if (ObjectUtils.nullSafeEquals(this.message, message) && ObjectUtils.nullSafeEquals(this.severity, severity)) {
			return;
		}
		this.message = message;
		this.severity = severity;
		fireMessageUpdated();
	}

	public void addMessageAreaChangeListener(MessageAreaChangeListener messageListener) {
		listenerList.add(MessageAreaChangeListener.class, messageListener);
	}

	public void removeMessageAreaChangeListener(MessageAreaChangeListener messageListener) {
		listenerList.remove(MessageAreaChangeListener.class, messageListener);
	}

	protected void fireMessageUpdated() {
		MessageAreaChangeListener[] listeners = (MessageAreaChangeListener[])listenerList
				.getListeners(MessageAreaChangeListener.class);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].messageUpdated(delegate);
		}
	}

	protected List getMessageListeners() {
		return Arrays.asList(listenerList.getListeners(MessageAreaChangeListener.class));
	}
}