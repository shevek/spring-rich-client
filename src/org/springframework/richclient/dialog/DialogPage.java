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

import javax.swing.Icon;

import org.springframework.richclient.factory.ControlFactory;

public interface DialogPage extends ControlFactory, MessageAreaPane, ErrorPane {

    /**
     * Returns this dialog page's title.
     * 
     * @return the title of this dialog page, or <code>null</code> if none
     */
    public String getTitle();

    /**
     * Returns this dialog page's description text.
     * 
     * @return the description text for this dialog page, or <code>null</code>
     *         if none
     */
    public String getDescription();

    /**
     * Returns the current message for this wizard page.
     * <p>
     * A message provides instruction or information to the user, as opposed to
     * an error message which should describe some error state.
     * </p>
     * 
     * @return the message, or <code>null</code> if none
     */
    public String getMessage();

    /**
     * Returns this dialog page's image.
     * 
     * @return the image for this dialog page, or <code>null</code> if none
     */
    public Icon getIcon();

    /**
     * Notifies that help has been requested for this dialog page.
     */
    public void performHelp();

    /**
     * Sets the visibility of this dialog page.
     * 
     * @param visible
     *            <code>true</code> to make this page visible, and
     *            <code>false</code> to hide it
     */
    public void setVisible(boolean visible);

}