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

public interface ViewListener {

    /**
     * Notifies this listener that the given part has been opened.
     * 
     * @param part
     *            the part that was opened
     * @see IWorkbenchPage#showView
     */
    public void viewActivated(View view);

    /**
     * Notifies this listener that the given part has been closed.
     * 
     * @param part
     *            the part that was closed
     * @see IWorkbenchPage#hideView
     */
    public void viewDeactivated(View view);

}

