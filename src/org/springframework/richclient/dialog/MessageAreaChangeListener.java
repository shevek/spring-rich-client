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

import java.util.EventListener;

/**
 * The listener interface for reciving notification of changes to the messages
 * of a MessageReceiver.
 * 
 * @author oliverh
 */
public interface MessageAreaChangeListener extends EventListener {

	/**
	 * Invoked when the messages have been updated.
	 * 
	 * @param source
	 *            the MessageReceiver that has been updated
	 */
	public void messageUpdated(MessageAreaModel source);
}