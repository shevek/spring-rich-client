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
package org.springframework.richclient.progress;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.controls.ShadowBorder;
import org.springframework.richclient.util.GridBagCellConstraints;
import org.springframework.util.StringUtils;

/**
 * A StatusBar control is a component with a horizontal layout which hosts a
 * number of status indication controls. Typically it is situated below the
 * content area of the window.
 * <p>
 * By default a StatusBar has two predefined status controls: a MessageLine and
 * a JProgressBar and it provides API for easy access.
 */
public class StatusBar extends JPanel implements ProgressMonitor {
    private static Log logger = LogFactory.getLog(StatusBar.class);

    /** Progress bar creation is delayed by this ms */
    public static final int DELAY_PROGRESS = 500;

    private String taskName;

    private long startTime;

    private boolean isCanceled;

    private boolean cancelEnabled = true;

    private String message;

    private ImageIcon messageIcon;

    private boolean errorMessageShowing;

    private JLabel messageLabel;

    private JToolBar toolBar;

    private JProgressBar progressBar;

    private JButton cancelButton;

    private Icon defaultCancelIcon;

    /**
     * Create a new StatusLine as a child of the given parent.
     */
    public StatusBar() {
        super();
        initialize();
        setBorder(new ShadowBorder());
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        messageLabel = new JLabel(" ");
        defaultCancelIcon = ApplicationServices.locator().getIcon("cancel.icon");

        Border bevelBorder = BorderFactory.createBevelBorder(
                BevelBorder.LOWERED, UIManager.getColor("controlHighlight"),
                UIManager.getColor("controlShadow"));
        Border emptyBorder = BorderFactory.createEmptyBorder(1, 3, 1, 3);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(bevelBorder,
                emptyBorder));

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        cancelButton = new JButton();
        cancelButton.setBorderPainted(false);
        cancelButton.setIcon(getDefaultCancelIcon());
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.info("Requesting task cancellation...");
                setCanceled(true);
            }
        });
        toolBar.add(cancelButton);
        toolBar.setVisible(false);
        progressBar = new JProgressBar();
        GridBagCellConstraints cc = new GridBagCellConstraints();
        add(messageLabel, cc.xyf(1, 0, GridBagConstraints.BOTH));
        add(toolBar, cc.xy(2, 0));
        add(progressBar, cc.xyfi(3, 0, GridBagConstraints.NONE,
                GridBagCellConstraints.RIGHT_INSETS));
        progressBar.setPreferredSize(new Dimension(200, 17));

        hideProgress();
    }

    private Icon getDefaultCancelIcon() {
        return defaultCancelIcon;
    }

    /**
     * Notifies that the main task is beginning.
     * 
     * @param name
     *            the name (or description) of the main task
     * @param totalWork
     *            the total number of work units into which the main task is
     *            been subdivided. If the value is 0 or UNKNOWN the
     *            implemenation is free to indicate progress in a way which
     *            doesn't require the total number of work units in advance. In
     *            general users should use the UNKNOWN value if they don't know
     *            the total amount of work units.
     */
    public void taskStarted(String name, int totalWork) {
        startTime = System.currentTimeMillis();
        isCanceled = false;
        progressBar.setMaximum(totalWork);
        progressBar.setValue(0);
        if (name == null) {
            taskName = " ";
        }
        else {
            taskName = name;
        }
        setMessage(taskName);
    }

    /**
     * Notifies that a subtask of the main task is beginning. Subtasks are
     * optional; the main task might not have subtasks.
     * 
     * @param name
     *            the name (or description) of the subtask
     * @see IProgressMonitor#subTask(String)
     */
    public void subTaskStarted(String name) {
        String text;
        if (name.length() == 0) {
            text = name;
        }
        else {
            if (StringUtils.hasText(taskName)) {
                text = taskName + " - " + name;
            }
            else {
                text = name;
            }
        }
        setMessage(text);
    }

    /**
     * Notifies that the work is done; that is, either the main task is
     * completed or the user cancelled it.
     * 
     * done() can be called more than once; an implementation should be prepared
     * to handle this case.
     */
    public void done() {
        startTime = 0;
        if (progressBar != null) {
            progressBar.setValue(progressBar.getMaximum());
        }
        setMessage(null);
        hideProgress();
    }

    /**
     * Returns the status line's progress monitor
     */
    public ProgressMonitor getProgressMonitor() {
        return this;
    }

    private void showProgress() {
        if (!progressBar.isVisible()) {
            if (cancelEnabled) {
                showButton();
            }
            progressBar.setVisible(true);
            revalidate();
        }
    }

    private void showButton() {
        if (!toolBar.isVisible()) {
            toolBar.setVisible(true);
        }
        cancelButton.setEnabled(cancelEnabled);
        cancelButton.setVisible(true);
    }

    protected void hideProgress() {
        if (progressBar.isVisible()) {
            progressBar.setVisible(false);
            cancelButton.setVisible(false);
            if (toolBar.isVisible()) {
                toolBar.setVisible(false);
            }
            revalidate();
        }
    }

    public void worked(int work) {
        internalWorked(work);
    }

    public void internalWorked(double work) {
        if (!progressBar.isVisible()) {
            if ((System.currentTimeMillis() - startTime) > DELAY_PROGRESS) {
                showProgress();
            }
        }
        progressBar.setValue((int)work);
    }

    /**
     * Returns true if the user does some UI action to cancel this operation.
     * (like hitting the Cancel button on the progress dialog).
     * 
     * The long running operation typically polls isCanceled().
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Sets the cancel status. This method is usually called with the argument
     * false if a client wants to abort a cancel action.
     */
    public void setCanceled(boolean b) {
        isCanceled = b;
        cancelButton.setEnabled(!b);
    }

    public void setCancelIcon(ImageIcon icon) {
        cancelButton.setIcon(icon);
    }

    /**
     * Controls whether the ProgressIndication provides UI for canceling a long
     * running operation.
     * 
     * If the ProgressIndication is currently visible calling this method may
     * have a direct effect on the layout because it will make a cancel button
     * visible.
     */
    public void setCancelEnabled(boolean enabled) {
        this.cancelEnabled = enabled;
        if (progressBar.isVisible() && !cancelButton.isVisible()
                && cancelEnabled) {
            showButton();
            revalidate();
        }
    }

    /**
     * Returns <code>true</true> if the ProgressIndication provides UI for
     * canceling a long running operation.
     */
    public boolean isCancelEnabled() {
        return cancelEnabled;
    }

    /**
     * Sets the error message text to be displayed on the status line. The icon
     * on the status line is cleared.
     */
    public void setErrorMessage(String message) {
        setErrorMessage(null, message);
    }

    /**
     * Sets an image and error message text to be displayed on the status line.
     */
    public void setErrorMessage(ImageIcon icon, String message) {
        if (message == null) {
            clearErrorMessage();
        }
        else {
            if (!errorMessageShowing) {
                messageLabel.setForeground(SystemColor.RED);
                errorMessageShowing = true;
            }
            if (message == null) {
                message = " ";
            }
            logger.debug("Setting status bar error message to '" + message
                    + "'");
            messageLabel.setText(message);
            messageLabel.setIcon(icon);
        }
    }

    public void clearErrorMessage() {
        messageLabel.setForeground(SystemColor.controlText);
        errorMessageShowing = false;
        setMessage(messageIcon, message);
    }

    /**
     * Sets the message text to be displayed on the status line. The icon on the
     * status line is cleared.
     */
    public void setMessage(String message) {
        setMessage(null, message);
    }

    /**
     * Sets an image and a message text to be displayed on the status line.
     */
    public void setMessage(ImageIcon icon, String message) {
        this.message = message;
        this.messageIcon = icon;
        if (!errorMessageShowing) {
            if (!StringUtils.hasText(message)) {
                message = " ";
            }
            logger.debug("Setting status bar message to '" + message + "'");
            messageLabel.setText(message);
            messageLabel.setIcon(icon);
        }
    }
}