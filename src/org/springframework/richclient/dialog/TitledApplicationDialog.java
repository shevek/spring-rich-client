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

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Window;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.ObjectUtils;

public abstract class TitledApplicationDialog extends ApplicationDialog implements Messagable, ImageConfigurable,
        DescriptionConfigurable {
    private TitlePane titlePane = new TitlePane();

    private Message description = new Message("Title pane description");

    private JComponent pageControl;

    private JComponent contentPane;

    public TitledApplicationDialog() {
        super();
    }

    public TitledApplicationDialog(String title, Window parent) {
        super(title, parent);
    }

    public TitledApplicationDialog(String title, Window parent, CloseAction closeAction) {
        super(title, parent, closeAction);
    }

    public void setCaption(String shortDescription) {
        throw new UnsupportedOperationException("What can I do with a caption?");
    }

    public void setDescription(String description) {
        Message old = this.description;
        Message message = new Message(description);
        if (!ObjectUtils.nullSafeEquals(old, message)) {
            this.description = message;
            if (titlePane.getMessage().equals(old)) {
                titlePane.setMessage(this.description);
            }
        }
    }

    public void setTitlePaneTitle(String titleAreaText) {
        titlePane.setTitle(titleAreaText);
    }

    public void setTitlePaneImage(Image image) {
        titlePane.setImage(image);
    }

    public void setImage(Image image) {
        setTitlePaneImage(image);
    }

    public Message getMessage() {
        return titlePane.getMessage();
    }

    public void setMessage(Message message) {
        if (message == null) {
            message = Message.EMPTY_MESSAGE;
        }
        if (!message.isEmpty()) {
            titlePane.setMessage(message);
        }
        else {
            titlePane.setMessage(getDescription());
        }
    }

    public boolean isMessageShowing() {
        return titlePane.isMessageShowing();
    }

    protected Message getDescription() {
        return description;
    }

    protected void setContentPane(JComponent c) {
        if (isControlCreated()) {
            pageControl.remove(contentPane);
            this.contentPane = c;
            pageControl.add(contentPane);
            pageControl.revalidate();
            pageControl.repaint();
        }
        else {
            throw new IllegalStateException("Cannot set content pane until control is created");
        }
    }

    protected void addDialogComponents() {
        JComponent dialogContentPane = createDialogContentPane();
        getDialog().getContentPane().add(dialogContentPane, BorderLayout.CENTER);
        getDialog().getContentPane().add(createButtonBar(), BorderLayout.SOUTH);
    }

    protected JComponent createDialogContentPane() {
        pageControl = new JPanel(new BorderLayout());
        JPanel titlePaneContainer = new JPanel(new BorderLayout());
        setMessage(getDescription());
        titlePaneContainer.add(titlePane.getControl());
        titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
        pageControl.add(titlePaneContainer, BorderLayout.NORTH);
        contentPane = createTitledDialogContentPane();
        if (getPreferredSize() != null) {
            contentPane.setPreferredSize(getPreferredSize());
        }
        GuiStandardUtils.attachDialogBorder(contentPane);
        pageControl.add(contentPane);
        return pageControl;
    }

    protected abstract JComponent createTitledDialogContentPane();

    protected void onAboutToShow() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        titlePane.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        titlePane.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        titlePane.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        titlePane.removePropertyChangeListener(propertyName, listener);
    }
}