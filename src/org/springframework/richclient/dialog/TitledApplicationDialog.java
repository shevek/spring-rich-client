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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.rules.reporting.Severity;
import org.springframework.util.StringUtils;

public abstract class TitledApplicationDialog extends ApplicationDialog
        implements MessageAreaPane {

    private TitleAreaPane titleAreaPane = new TitleAreaPane();

    private String description = "Title area description";

    private JComponent pageControl;

    private JComponent contentPane;

    private boolean resetMessagePaneOnDisplay;

    public TitledApplicationDialog() {
        super();
    }

    public TitledApplicationDialog(String title, Window parent) {
        super(title, parent);
    }

    public TitledApplicationDialog(String title, Window parent,
            CloseAction closeAction) {
        super(title, parent, closeAction);
    }

    public void setTitleAreaText(String titleAreaText) {
        titleAreaPane.setTitle(titleAreaText);
    }

    public void setTitleAreaImage(Image image) {
        titleAreaPane.setImage(image);
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public JComponent getControl() {
        return titleAreaPane.getControl();
    }

    public boolean messageShowing() {
        return true;
    }

    public void setMessage(String message) {
        setMessage(message, null);
    }

    public void setMessage(String message, Severity severity) {
        if (StringUtils.hasText(message)) {
            titleAreaPane.setMessage(message, severity);
        }
        else {
            updateDescription();
        }
    }

    public void setErrorMessage(String message) {
        setMessage(message, Severity.ERROR);
    }
    
    public void addMessageListener(MessageListener messageListener) {
        titleAreaPane.addMessageListener(messageListener);        
    }
    
    public void removeMessageListener(MessageListener messageListener) {
        titleAreaPane.removeMessageListener(messageListener);        
    }  

    public void updateDescription() {
        titleAreaPane.setMessage(getDescription());
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
        getDialog().getContentPane().add(createDialogContentPane(),
                BorderLayout.CENTER);
        getDialog().getContentPane().add(createButtonBar(), BorderLayout.SOUTH);
    }

    protected JComponent createDialogContentPane() {
        pageControl = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleAreaPane.getControl());
        titlePanel.add(new JSeparator(), BorderLayout.SOUTH);
        pageControl.add(titlePanel, BorderLayout.NORTH);
        contentPane = createTitledDialogContentPane();
        GuiStandardUtils.attachDialogBorder(contentPane);
        updateDescription();
        pageControl.add(contentPane);
        return pageControl;
    }

    protected abstract JComponent createTitledDialogContentPane();

    protected void onAboutToShow() {
        if (resetMessagePaneOnDisplay) {
            titleAreaPane.setMessage(description);
        }
    }

    public void setResetMessagePaneOnDisplay(boolean reset) {
        this.resetMessagePaneOnDisplay = reset;
    }
}