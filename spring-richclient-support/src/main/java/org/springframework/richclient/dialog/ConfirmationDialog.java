/*
 * Copyright 2002-2006 the original author or authors.
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
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.springframework.richclient.core.DefaultMessage;
import org.springframework.util.Assert;

/**
 * Dialog for asking confirmation to the user. The <code>onConfirm</code> is
 * called when the user presses the yes button.
 */
public abstract class ConfirmationDialog extends ApplicationDialog {

    private static final String YES_FACE_DESCRIPTOR_ID = "yesCommand";

    private static final String NO_FACE_DESCRIPTOR_ID = "noCommand";

    private static final String CONFIRMATION_DIALOG_ICON = "confirmationDialog.icon";

    private DefaultMessageAreaPane messageAreaPane;

    private String confirmationMessage;

    public ConfirmationDialog() {
        this("Confirmation Required", null, "Are you sure you wish to perform this action?");
    }

    public ConfirmationDialog(String title, String message) {
        this(title, null, message);
    }

    public ConfirmationDialog(String title, Window parent, String message) {
        super(title, parent);
        setConfirmationMessage(message);
    }

    public void setConfirmationMessage(String message) {
        Assert.hasText(message, "The confirmation message is required");
        this.confirmationMessage = message;
        if(this.messageAreaPane != null) {
            messageAreaPane.setMessage(new DefaultMessage(message));
        }
    }

    protected String getFinishCommandId() {
        return YES_FACE_DESCRIPTOR_ID;
    }

    protected String getCancelCommandId() {
        return NO_FACE_DESCRIPTOR_ID;
    }

    protected void registerDefaultCommand() {
        registerCancelCommandAsDefault();
    }

    protected void onInitialized() {
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        addActionKeyBinding(KeyStroke.getKeyStroke(getYesKey(), 0), getFinishCommand().getId());
        addActionKeyBinding(KeyStroke.getKeyStroke(getNoKey(), 0), getCancelCommand().getId());
    }

    protected int getYesKey() {
        return getFinishCommand().getMnemonic();
    }

    protected int getNoKey() {
        return getCancelCommand().getMnemonic();
    }

    protected JComponent createDialogContentPane() {
        this.messageAreaPane = new DefaultMessageAreaPane();
        Icon icon = getIconSource().getIcon(CONFIRMATION_DIALOG_ICON);
        if (icon == null) {
            icon = UIManager.getIcon("OptionPane.questionIcon");
        }
        this.messageAreaPane.setDefaultIcon(icon);
        this.messageAreaPane.setMessage(new DefaultMessage(confirmationMessage));
        return messageAreaPane.getControl();
    }

    protected void disposeDialogContentPane() {
    	messageAreaPane = null;
    }

    protected final boolean onFinish() {
        onConfirm();
        return true;
    }

    protected abstract void onConfirm();

}