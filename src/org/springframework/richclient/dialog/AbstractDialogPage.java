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

import java.awt.Image;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.factory.ControlFactory;
import org.springframework.rules.reporting.Severity;
import org.springframework.util.Assert;

/**
 * A convenience implementation of the DialogPage interface.
 * 
 * Recommended to be used as a base class for all GUI dialog pages (or panes.)
 * 
 * @author Keith Donald
 * @see DialogPage
 */
public abstract class AbstractDialogPage extends LabeledObjectSupport implements
        DialogPage, ControlFactory, Guarded {

    private String pageId;

    private boolean pageComplete = true;

    private MessageBuffer messageBuffer;

    private AbstractControlFactory factory = new AbstractControlFactory() {
        public JComponent createControl() {
            return AbstractDialogPage.this.createControl();
        }
    };

    protected AbstractDialogPage() {
        
    }

    /**
     * Creates a new dialog page. This titles of this dialog page will be
     * configured using the default ObjectConfigurer.
     * 
     * @param pageId
     *            the id of this dialog page. This will be used to configure the
     *            page.
     */
    protected AbstractDialogPage(String pageId) {
        this(pageId, true);
    }

    /**
     * Creates a new dialog page.
     * 
     * @param pageId
     *            the id of this dialog page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this dialog page using the given pageId
     */
    protected AbstractDialogPage(String pageId, boolean autoConfigure) {
        this.messageBuffer = new MessageBuffer(this);
        setId(pageId, autoConfigure);
    }

    /**
     * Creates a new dialog page with the given title.
     * 
     * @param pageId
     *            the id of this dialog page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this dialog page using the given pageId
     * @param title
     *            the title of this dialog page, or <code>null</code> if none
     */
    protected AbstractDialogPage(String pageId, boolean autoConfigure,
            String title) {
        this(pageId, autoConfigure);
        if (title != null) {
            setTitle(title);
        }
    }

    /**
     * Creates a new dialog page with the given title and image.
     * 
     * @param pageId
     *            the id of this dialog page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this dialog page using the given pageId
     * @param title
     *            the title of this dialog page, or <code>null</code> if none
     * @param icon
     *            the image for this dialog page, or <code>null</code> if none
     */
    protected AbstractDialogPage(String pageId, boolean autoConfigure,
            String title, Image icon) {
        this(pageId, autoConfigure, title);
        if (icon != null) {
            setImage(icon);
        }
    }

    public String getId() {
        return pageId;
    }

    protected void setId(String pageId, boolean autoConfigure) {
        Assert.hasText(pageId, "pageId is required");
        String oldValue = this.pageId;
        this.pageId = pageId;
        firePropertyChange("id", oldValue, pageId);
        if (autoConfigure) {
            if (logger.isDebugEnabled()) {
                logger.debug("Auto configuring dialog page with id " + pageId);
            }
            getObjectConfigurer().configure(this, pageId);
        }
    }

    public String getTitle() {
        return getDisplayName();
    }

    public String getMessage() {
        return messageBuffer.getMessage();
    }

    public Severity getSeverity() {
        return messageBuffer.getSeverity();
    }

    /**
     * Sets or clears the message for this page.
     *
     * @param newMessage
     *            the message, or <code>null</code> to clear the message
     */
    public void setMessage(String newMessage) {
        messageBuffer.setMessage(newMessage);
    }

    public void setMessage(String newMessage, Severity severity) {
        messageBuffer.setMessage(newMessage, severity);
    }

    public String getErrorMessage() {
        return hasErrorMessage() ? getMessage() : null;
    }

    public boolean hasErrorMessage() {
        return messageBuffer.getSeverity() != null
                && messageBuffer.getSeverity().equals(Severity.ERROR);
    }

    /**
     * Sets or clears the error message for this page.
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the error message
     */
    public void setErrorMessage(String newMessage) {
        messageBuffer.setErrorMessage(newMessage);
    }

    public void addMessageListener(MessageListener messageListener) {
        messageBuffer.addMessageListener(messageListener);
    }

    public void removeMessageListener(MessageListener messageListener) {
        messageBuffer.removeMessageListener(messageListener);
    }

    public void setVisible(boolean visible) {
        boolean oldValue = getControl().isVisible();
        getControl().setVisible(visible);
        firePropertyChange("visible", oldValue, visible);
    }

    public boolean isPageComplete() {
        return pageComplete;
    }

    public void setPageComplete(boolean pageComplete) {
        boolean oldValue = this.pageComplete;
        this.pageComplete = pageComplete;
        firePropertyChange("pageComplete", oldValue, pageComplete);
    }

    public boolean isEnabled() {
        return isPageComplete();
    }

    public void setEnabled(boolean enabled) {
        setPageComplete(enabled);
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
        // nothing by default
    }

    protected abstract JComponent createControl();

}
