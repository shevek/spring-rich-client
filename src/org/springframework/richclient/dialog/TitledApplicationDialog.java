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

import org.springframework.richclient.core.Message;
import org.springframework.richclient.util.GuiStandardUtils;

public abstract class TitledApplicationDialog extends ApplicationDialog implements Messagable {
    private TitlePane titleAreaPane = new TitlePane();

    private Message description = new Message("Title area description");

    private JComponent pageControl;

    private JComponent contentPane;

    public TitledApplicationDialog() {
        super();
        init();
    }

    public TitledApplicationDialog(String title, Window parent) {
        super(title, parent);
        init();
    }

    public TitledApplicationDialog(String title, Window parent, CloseAction closeAction) {
        super(title, parent, closeAction);
        init();
    }

    private void init() {

    }

    public void setTitlePaneText(String titleAreaText) {
        titleAreaPane.setTitle(titleAreaText);
    }

    public void setTitlePaneImage(Image image) {
        titleAreaPane.setImage(image);
    }

    public void setDescription(String description) {
        this.description = new Message(description);
    }

    public boolean messageShowing() {
        return titleAreaPane.messageShowing();
    }

    public void setMessage(Message message) {
        if (message == null) {
            message = Message.EMPTY_MESSAGE;
        }
        if (!message.isEmpty()) {
            titleAreaPane.setMessage(message);
        }
        else {
            updateTitlePaneWithDescription();
        }
    }

    public void updateTitlePaneWithDescription() {
        titleAreaPane.setMessage(getDescription());
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
            throw new IllegalStateException();
        }
    }

    protected void addDialogComponents() {
        JComponent dialogContentPane = createDialogContentPane();
        getDialog().getContentPane().add(dialogContentPane, BorderLayout.CENTER);
        getDialog().getContentPane().add(createButtonBar(), BorderLayout.SOUTH);
    }

    protected JComponent createDialogContentPane() {
        pageControl = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleAreaPane.getControl());
        titlePanel.add(new JSeparator(), BorderLayout.SOUTH);
        pageControl.add(titlePanel, BorderLayout.NORTH);
        contentPane = createTitledDialogContentPane();
        if (getPreferredSize() != null) {
            contentPane.setPreferredSize(getPreferredSize());
        }
        GuiStandardUtils.attachDialogBorder(contentPane);
        updateTitlePaneWithDescription();
        pageControl.add(contentPane);
        return pageControl;
    }

    protected abstract JComponent createTitledDialogContentPane();

    protected void onAboutToShow() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        titleAreaPane.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        titleAreaPane.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        titleAreaPane.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        titleAreaPane.removePropertyChangeListener(propertyName, listener);
    }
}