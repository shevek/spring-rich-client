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
package org.springframework.richclient.application;

import org.springframework.richclient.core.Dirtyable;
import org.springframework.richclient.core.Saveable;

public interface Editor extends PageComponent, Dirtyable, Saveable {
	/**
	 * Sets the object for this editor to edit.
	 * 
	 * @param input the input
	 */
	void setEditorInput(Object input);

	/**
	 * Returns the object this editor is using.
	 * 
	 * @return theh input
	 */
	Object getEditorInput();
}