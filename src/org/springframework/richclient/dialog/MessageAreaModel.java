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

import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.rules.reporting.Severity;

/**
 * Interface to be implemented by object capable of receiving messages to the
 * user.
 * 
 * @author Keith Donald
 */
public interface MessageAreaModel extends PropertyChangePublisher {
    
    public static final String MESSAGE_PROPERTY = "message";

    /**
     * Set the message text. If the message line currently displays an error,
     * the message is stored and will be shown after a call to
     * setErrorMessage(null)
     */
    public void setMessage(String newMessage);

    /**
     * Display the given error message of the provided severity.
     * 
     * @param newMessage
     * @param severity
     */
    public void setMessage(String newMessage, Severity severity);
}