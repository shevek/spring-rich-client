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

import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.factory.ControlFactory;
import org.springframework.util.StringUtils;

/**
 * A convenience implementation of the DialogPage interface.
 * 
 * Recommended to be used as a base class for all GUI dialog pages (or panes.)
 * 
 * @author Keith Donald
 * @see DialogPage
 */
public abstract class AbstractDialogPage extends LabeledObjectSupport implements
        DialogPage, ControlFactory {

    /**
     * The page's current message; <code>null</code> if none.
     */
    private String message;

    /**
     * The page's current error message; <code>null</code> if none.
     */
    private String errorMessage;

    private AbstractControlFactory factory = new AbstractControlFactory() {
        public JComponent createControl() {
            return AbstractDialogPage.this.createControl();
        }
    };

    /**
     * Creates a new empty dialog page.
     */
    protected AbstractDialogPage() {

    }

    /**
     * Creates a new dialog page with the given title.
     * 
     * @param title
     *            the title of this dialog page, or <code>null</code> if none
     */
    protected AbstractDialogPage(String title) {
        setTitle(title);
    }

    /**
     * Creates a new dialog page with the given title and image.
     * 
     * @param title
     *            the title of this dialog page, or <code>null</code> if none
     * @param image
     *            the image for this dialog page, or <code>null</code> if none
     */
    protected AbstractDialogPage(String title, Icon icon) {
        this(title);
        setIcon(icon);
    }

    public String getTitle() {
        return getDisplayName();
    }

    public String getMessage() {
        return message;
    }

    /**
     * Sets or clears the message for this page.
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the message
     */
    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public boolean messageShowing() {
        return StringUtils.hasText(message)
                || StringUtils.hasText(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets or clears the error message for this page.
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the error message
     */
    public void setErrorMessage(String newMessage) {
        errorMessage = newMessage;
    }

    public boolean hasErrorMessage() {
        return errorMessage != null;
    }

    public void setVisible(boolean visible) {
        getControl().setVisible(visible);
    }

    public JComponent getControl() {
        return factory.getControl();
    }

    public Window getParentWindowControl() {
        return SwingUtilities.getWindowAncestor(getControl());
    }

    /**
     * This default implementation of an <code>AbstractDialogPage</code>
     * method does nothing. Subclasses should override to take some action in
     * response to a help request.
     */
    public void performHelp() {

    }

    protected abstract JComponent createControl();

}