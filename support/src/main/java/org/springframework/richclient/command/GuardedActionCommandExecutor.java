/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.command;

import java.beans.PropertyChangeListener;

import org.springframework.richclient.core.Guarded;

/**
 * An {@link ActionCommandExecutor} that can be enabled or disabled, with optional listeners
 * for these state changes.
 *  
 * @author Keith Donald
 */
public interface GuardedActionCommandExecutor extends Guarded, ActionCommandExecutor {
	
	/**
	 * Adds the given listener to the collection of listeners that will be notified 
	 * when the command executor's enabled state changes.
	 *
	 * @param listener The listener to be added.
	 */
    public void addEnabledListener(PropertyChangeListener listener);

    /**
     * Removes the given listener from the collection of listeners that will be 
     * notified when the command executor's enabled state changes.
     *
     * @param listener The listener to be removed.
     */
    public void removeEnabledListener(PropertyChangeListener listener);
    
}
