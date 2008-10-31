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
package org.springframework.richclient.dialog.support;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.util.GuiStandardUtils;

/**
 * Useful methods for working with {@link DialogPage}s. These methods make it easier to
 * place dialog pages in arbitrary containers, wire in message listeners, wire actions
 * to the page complete status, etc.
 * <p>
 * These helpers can be used to quickly construct a standard layout (like that found in
 * the {@link TitledPageApplicationDialog}, or you can use these methods to construct the
 * various components (title area and button bar) and then lay things out as you like.
 * <p>
 * The standard layout is obtained using the
 * {@link #createStandardView(DialogPage, ActionCommand, ActionCommand)} or
 * {@link #createStandardView(DialogPage, Object[])} methods. To create the pieces and do
 * the layout independently, use {@link #createTitlePane(DialogPage)} to get the title
 * pane and wire your OK command into the page complete status using
 * {@link #adaptPageCompletetoGuarded(DialogPage, Guarded)}. If you don't want to use the
 * standard title pane, you can easily arrange for the messages coming from the dialog
 * page to be sent to a {@link Messagable} of your choice using
 * {@link #addMessageMonitor(DialogPage, Messagable)}.
 * 
 * @author Larry Streepy
 * 
 */
public class DialogPageUtils {

    /**
     * Create a standard {@link TitlePane} wired to receive messages from the given dialog
     * page. The title pane will also be configured from the dialog page's title and icon.
     * 
     * @param dialogPage to process
     */
    public static TitlePane createTitlePane( DialogPage dialogPage ) {
        TitlePane titlePane = new TitlePane();
        titlePane.setTitle(dialogPage.getTitle());
        titlePane.setImage(dialogPage.getImage());
        addMessageMonitor(dialogPage, titlePane);

        return titlePane;
    }

    /**
     * Construct a complete standard layout for a dialog page. This is a panel with the
     * title/message area at the top, the dialog page control in the center, and the
     * command button bar (using the provided ok and cancel commands) on the bottom. The
     * finishCommand provided will automatically be wired into the page complete status of
     * the dialog page.
     * 
     * @param dialogPage to process
     * @param okCommand Action command to wire into dialogPage's page complete status
     * @param cancelCommand to add to the command button bar
     * @return created component
     * @see #createTitlePane(DialogPage)
     * @see #adaptPageCompletetoGuarded(DialogPage, Guarded)
     */
    public static JComponent createStandardView( DialogPage dialogPage, ActionCommand okCommand,
            ActionCommand cancelCommand ) {
        adaptPageCompletetoGuarded(dialogPage, okCommand);
        return createStandardView(dialogPage, new Object[] { okCommand, cancelCommand });
    }

    /**
     * Construct a complete standard layout for a dialog page. This is a panel with the
     * title/message area at the top, the dialog page control in the center, and the
     * command button bar (using the provided group of commands) on the bottom. You should
     * have already wired any commands to the page complete status as needed.
     * 
     * @param dialogPage to process
     * @param commandGroupMembers Array of commands to place in the button bar
     * @return created component
     * @see #createTitlePane(DialogPage)
     * @see #adaptPageCompletetoGuarded(DialogPage, Guarded)
     */
    public static JComponent createStandardView( DialogPage dialogPage, Object[] commandGroupMembers ) {
        JPanel viewPanel = new JPanel(new BorderLayout());

        JPanel titlePaneContainer = new JPanel(new BorderLayout());
        titlePaneContainer.add(createTitlePane(dialogPage).getControl());
        titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
        viewPanel.add(titlePaneContainer, BorderLayout.NORTH);

        JComponent pageControl = dialogPage.getControl();
        GuiStandardUtils.attachDialogBorder(pageControl);
        viewPanel.add(pageControl);

        viewPanel.add(createButtonBar(commandGroupMembers), BorderLayout.SOUTH);

        return viewPanel;
    }

    /**
     * Return a standardized row of command buttons.
     * 
     * @param groupMembers
     * @return button bar
     */
    public static JComponent createButtonBar( Object[] groupMembers ) {
        CommandGroup dialogCommandGroup = CommandGroup.createCommandGroup(null, groupMembers);
        JComponent buttonBar = dialogCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }

    /**
     * Add a message monitor. Each monitor will have its
     * {@link Messagable#setMessage(Message)} method called whenever the MESSAGE property
     * on the dialog page changes.
     * 
     * @param dialogPage to monitor
     * @param monitor to add
     */
    public static void addMessageMonitor( DialogPage dialogPage, Messagable monitor ) {
        dialogPage.addPropertyChangeListener(Messagable.MESSAGE_PROPERTY, new MessageHandler(monitor));
    }

    /**
     * Create an adapter that will monitor the page complete status of the dialog page and
     * adapt it to operations on the provided Guarded object. If the page is complete,
     * then the guarded object will be enabled. If this page is not complete, then the
     * guarded object will be disabled.
     * 
     * @param dialogPage to monitor
     * @param guarded object to adapt
     */
    public static void adaptPageCompletetoGuarded( DialogPage dialogPage, Guarded guarded ) {
        dialogPage.addPropertyChangeListener(DialogPage.PAGE_COMPLETE_PROPERTY, new PageCompleteAdapter(guarded));
    }

    /**
     * Internal class to handle the PAGE_COMPLETE property changes in the dialog page and
     * adapt them to operations on a Guarded object.
     */
    protected static class PageCompleteAdapter implements PropertyChangeListener {
        private Guarded guarded;

        /**
         * Construct a handler on the given guarded object.
         * 
         * @param guarded object to manage
         */
        protected PageCompleteAdapter( Guarded guarded ) {
            this.guarded = guarded;
        }

        /**
         * Handle a change in the page complete state of the dialog page
         * 
         * @param e
         */
        public void propertyChange( PropertyChangeEvent e ) {
            if( DialogPage.PAGE_COMPLETE_PROPERTY.equals(e.getPropertyName()) ) {
                guarded.setEnabled(((Boolean) e.getNewValue()).booleanValue());
            }
        }
    }

    /**
     * Internal class to handle the MESSAGE_PROPERTY property changes in a dialog page.
     */
    private static class MessageHandler implements PropertyChangeListener {
        private Messagable monitor;

        /**
         * Construct a handler on the given message monitor.
         * 
         * @param monitor to send messages to
         */
        public MessageHandler( Messagable monitor ) {
            this.monitor = monitor;
        }

        /**
         * Handle a change in the message or page complete state of the dialog page.
         * 
         * @param e
         */
        public void propertyChange( PropertyChangeEvent e ) {
            if( Messagable.MESSAGE_PROPERTY.equals(e.getPropertyName()) ) {
                monitor.setMessage((Message) e.getNewValue());
            }
        }
    }
}
