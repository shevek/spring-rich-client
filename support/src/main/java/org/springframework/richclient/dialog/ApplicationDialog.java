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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.support.ApplicationServicesAccessor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.WindowUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract Base Class for a dialog with standard layout, buttons, and behavior.
 * <p>
 * Use of this class will apply a standard appearance to dialogs in the application.
 * <P>
 * Subclasses implement the body of the dialog (wherein business objects are manipulated),
 * and the action taken by the <code>OK</code> button.
 * 
 * <P>
 * Services of a <code>ApplicationDialog</code> include:
 * <ul>
 * <li>centering on the parent frame</li>
 * <li>reusing the parent's icon</li>
 * <li>standard layout and border spacing, based on Java Look and Feel guidelines.</li>
 * <li>uniform naming style for dialog title</li>
 * <li><code>OK</code> and <code>Cancel</code> buttons at the bottom of the dialog -<code>OK</code>
 * is the default, and the <code>Escape</code> key activates <code>Cancel</code> (the
 * latter works only if the dialog receives the escape keystroke, and not one of its
 * components)</li>
 * <li>by default, modal</li>
 * <li>enabling & disabling of resizing</li>
 * <li>will be shown in taskbar if no parent window has been set, and no
 * applicationwindow is open</li>
 * </ul>
 */
public abstract class ApplicationDialog extends ApplicationServicesAccessor implements TitleConfigurable, Guarded {

    private static final String DEFAULT_DIALOG_TITLE = "Application Dialog";

    protected static final String DEFAULT_FINISH_COMMAND_ID = "okCommand";

    protected static final String DEFAULT_CANCEL_COMMAND_ID = "cancelCommand";

    protected static final String DEFAULT_FINISH_SUCCESS_MESSAGE_KEY = "defaultFinishSuccessMessage";
    protected static final String DEFAULT_FINISH_SUCCESS_TITLE_KEY = "defaultFinishSuccessTitle";

    protected static final String SUCCESS_FINISH_MESSAGE_KEY = "finishSuccessMessage";
    protected static final String SUCCESS_FINISH_TITLE_KEY = "finishSuccessTitle";

    protected final Log logger = LogFactory.getLog(getClass());

    private final DialogEventHandler dialogEventHandler = new DialogEventHandler();

    private String title;

    private JDialog dialog;

    private Component parentComponent;
    
    private Window parentWindow;

    private CloseAction closeAction = CloseAction.HIDE;

    private boolean defaultEnabled = true;

    private boolean modal = true;

    private boolean resizable = true;

    private Dimension preferredSize;

    private Point location;
    
    private Component locationRelativeTo;

    private ActionCommand finishCommand;

    private ActionCommand cancelCommand;

    private CommandGroup dialogCommandGroup;

    private boolean displayFinishSuccessMessage;

    private ActionCommand callingCommand;

    public ApplicationDialog() {
        init();
    }

    public ApplicationDialog(String title, Component parent) {
        setTitle(title);
        setParentComponent(parent);
        init();
    }

    /**
     * Creates a new application dialog; the actual UI is not initialized until
     * showDialog() is called.
     * 
     * @param title text which appears in the title bar after the name of the application,
     *        and satisfies
     * @param parent frame to which this dialog is attached.
     * @param closeAction sets the behaviour of the dialog upon close.
     */
    public ApplicationDialog(String title, Component parent, CloseAction closeAction) {
        setTitle(title);
        setParentComponent(parent);
        setCloseAction(closeAction);
        init();
    }

    protected void init() {

    }

    public void setTitle(String title) {
        this.title = title;
        if (dialog != null) {
            dialog.setTitle(getTitle());
        }
    }

    protected String getTitle() {
        if (!StringUtils.hasText(this.title)) {
            if (StringUtils.hasText(getCallingCommandText()))
                return getCallingCommandText();

            return DEFAULT_DIALOG_TITLE;
        }
        return this.title;
    }

    /**
	 * The parent Component that will be used to extract the Frame/Dialog owner
	 * for the JDialog at creation. You may pass a Window/Frame that will be
	 * used directly as parent for the JDialog, or you can pass the component
	 * which has one of both in it's parent hierarchy. The latter option can be
	 * handy when you're locally implementing Components without a direct
	 * -connection to/notion of- a Window/Frame.
	 * 
	 * @param parentComponent Component that is a Frame/Window or has one in its parent
	 * hierarchy.
	 */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    /**
     * Returns the parent Component.  
     * 
     * @return
     * @see #setParentComponent(Component)
     */
    public Component getParentComponent() {
		return this.parentComponent;
	}

    public void setCloseAction(CloseAction action) {
        this.closeAction = action;
    }

    /**
     * Should the finish button be enabled by default?
     * 
     * @param enabled true or false
     */
    public void setDefaultEnabled(boolean enabled) {
        this.defaultEnabled = enabled;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * Set a specific location for the JDialog to popup.
     * 
     * @param location point on screen where to place the JDialog.
     */
    public void setLocation(Point location) {
        this.location = location;
    }
    
    /**
     * @see Window#setLocationRelativeTo(Component)
     */
    public void setLocationRelativeTo(Component locationRelativeTo) {
    	this.locationRelativeTo = locationRelativeTo;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public void setEnabled(boolean enabled) {
        setFinishEnabled(enabled);
    }

    public void setDisplayFinishSuccessMessage(boolean displayFinishSuccessMessage) {
        this.displayFinishSuccessMessage = displayFinishSuccessMessage;
    }

    public void setCallingCommand(ActionCommand callingCommand) {
        this.callingCommand = callingCommand;
    }

    protected void setFinishEnabled(boolean enabled) {
        if (isControlCreated()) {
            finishCommand.setEnabled(enabled);
        }
    }

    public boolean isEnabled() {
        if (isControlCreated())
            return finishCommand.isEnabled();

        return false;
    }

    public boolean isShowing() {
        if (!isControlCreated()) {
            return false;
        }
        return dialog.isShowing();
    }

    public boolean isControlCreated() {
        return dialog != null;
    }

    public JDialog getDialog() {
        if (!isControlCreated()) {
            createDialog();
        }
        return dialog;
    }

    protected Container getDialogContentPane() {
        Assert.state(isControlCreated(), "The wrapped JDialog control has not yet been created.");
        return dialog.getContentPane();
    }

    /**
	 * <p>
	 * Show the dialog. The dialog will be created if it doesn't exist yet.
	 * Before setting the dialog visible, a hook method onAboutToShow is called
	 * and the location will be set.
	 * </p>
	 * <p>
	 * When showing the dialog several times, it will always be opened on the
	 * location that has been set, or relative to the parent. (former location
	 * will not persist)
	 * </p>
	 */
    public void showDialog() {
        if (!isControlCreated()) {
            createDialog();
        }
        if (!isShowing()) {
            onAboutToShow();
            if (getLocation() != null) {
                dialog.setLocation(getLocation());
            }
            else {
            	WindowUtils.centerOnParent(dialog, getLocationRelativeTo());
			}

            dialog.setVisible(true);
        }
    }

    /**
	 * Subclasses should call if layout of the dialog components changes.
	 */
    protected void componentsChanged() {
        if (isControlCreated()) {
            dialog.pack();
        }
    }

    /**
     * Builds/initializes the dialog and all of its components.
     * <p>
     * Follows the Java Look and Feel guidelines for spacing elements.
     */
    protected final void createDialog() {
        constructDialog();
        addDialogComponents();
        attachListeners();
        registerDefaultCommand();
        onInitialized();

        dialog.pack();
    }

    private void constructDialog() {
        if (getParentWindow() instanceof Frame) {
            dialog = new JDialog((Frame)getParentWindow(), getTitle(), modal);
        }
        else {
            dialog = new JDialog((Dialog)getParentWindow(), getTitle(), modal);
        }

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(resizable);
        
        initStandardCommands();
        addCancelByEscapeKey();
    }

    /**
	 * <p>
	 * --jh-- This method is copied from JOptionPane. I'm still trying to figure
	 * out why they chose to have a static method with package visibility for
	 * this one instead of just making it public.
	 * </p>
	 * 
	 * Returns the specified component's toplevel <code>Frame</code> or
	 * <code>Dialog</code>.
	 * 
	 * @param parentComponent the <code>Component</code> to check for a
	 * <code>Frame</code> or <code>Dialog</code>
	 * @return the <code>Frame</code> or <code>Dialog</code> that contains
	 * the component, or the default frame if the component is <code>null</code>,
	 * or does not have a valid <code>Frame</code> or <code>Dialog</code>
	 * parent
	 * @exception HeadlessException if
	 * <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
    public static Window getWindowForComponent(Component parentComponent)
        throws HeadlessException {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }
    
    private void initStandardCommands() {
        finishCommand = new ActionCommand(getFinishCommandId()) {
            public void doExecuteCommand() {
                boolean result = onFinish();
                if (result) {
                    if (getDisplayFinishSuccessMessage()) {
                        showFinishSuccessMessageDialog();
                    }
                    executeCloseAction();
                }
            }
        };
        finishCommand.setSecurityControllerId(getFinishSecurityControllerId());
        finishCommand.setEnabled(defaultEnabled);

        cancelCommand = new ActionCommand(getCancelCommandId()) {

            public void doExecuteCommand() {
                onCancel();
            }
        };
    }

    /**
     * Subclasses may override to return a custom message key, default is "okCommand",
     * corresponding to the "&OK" label.
     * 
     * @return The message key to use for the finish ("ok") button
     */
    protected String getFinishCommandId() {
        return DEFAULT_FINISH_COMMAND_ID;
    }

    /**
     * Subclasses may override to return a security controller id to be attached to the
     * finish command. The default is null, no controller.
     * 
     * @return security controller id, or null if none
     */
    protected String getFinishSecurityControllerId() {
        return null;
    }

    /**
     * Request invocation of the action taken when the user hits the <code>OK</code>
     * (finish) button.
     * 
     * @return true if action completed successfully; false otherwise.
     */
    protected abstract boolean onFinish();

    protected boolean getDisplayFinishSuccessMessage() {
        return displayFinishSuccessMessage;
    }

    protected void showFinishSuccessMessageDialog() {
        MessageDialog messageDialog = new MessageDialog(getFinishSuccessTitle(), getDialog(), getFinishSuccessMessage());
        messageDialog.showDialog();
    }

    protected String getFinishSuccessMessage() {
        ActionCommand callingCommand = getCallingCommand();
        if (callingCommand != null) {
            String[] successMessageKeys = new String[] {callingCommand.getId() + "." + SUCCESS_FINISH_MESSAGE_KEY,
                    DEFAULT_FINISH_SUCCESS_MESSAGE_KEY};
            return getMessage(successMessageKeys, getFinishSuccessMessageArguments());
        }
        return getMessage(DEFAULT_FINISH_SUCCESS_MESSAGE_KEY);
    }

    protected ActionCommand getCallingCommand() {
        return callingCommand;
    }

    protected Object[] getFinishSuccessMessageArguments() {
        return new Object[0];
    }

    protected String getFinishSuccessTitle() {
        ActionCommand callingCommand = getCallingCommand();
        if (callingCommand != null) {
            String[] successTitleKeys = new String[] {callingCommand.getId() + "." + SUCCESS_FINISH_TITLE_KEY,
                    DEFAULT_FINISH_SUCCESS_TITLE_KEY};
            return getMessage(successTitleKeys, getFinishSuccessTitleArguments());
        }
        return getMessage(DEFAULT_FINISH_SUCCESS_TITLE_KEY);
    }

    protected Object[] getFinishSuccessTitleArguments() {
        if (StringUtils.hasText(getCallingCommandText()))
            return new Object[] {getCallingCommandText()};

        return new Object[0];
    }

    private String getCallingCommandText() {
        return getCallingCommand() != null ? getCallingCommand().getText() : null;
    }

    protected String getCancelCommandId() {
        return DEFAULT_CANCEL_COMMAND_ID;
    }

    protected ActionCommand getFinishCommand() {
        return finishCommand;
    }

    protected ActionCommand getCancelCommand() {
        return cancelCommand;
    }

    /**
     * Force the escape key to call the same action as pressing the Cancel button. This
     * does not always work. See class comment.
     */
    private void addCancelByEscapeKey() {
        int noModifiers = 0;
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, noModifiers, false);
        addActionKeyBinding(escapeKey, cancelCommand.getId());
    }

    protected void addActionKeyBinding(KeyStroke key, String actionKey) {
        if (actionKey == finishCommand.getId()) {
            addActionKeyBinding(key, actionKey, finishCommand.getActionAdapter());
        }
        else if (actionKey == cancelCommand.getId()) {
            addActionKeyBinding(key, actionKey, cancelCommand.getActionAdapter());
        }
        else {
            throw new IllegalArgumentException("Unknown action key " + actionKey);
        }
    }

    protected void addActionKeyBinding(KeyStroke key, String actionKey, Action action) {
        getInputMap().put(key, actionKey);
        getActionMap().put(actionKey, action);
    }

    protected ActionMap getActionMap() {
        return getDialog().getLayeredPane().getActionMap();
    }

    protected InputMap getInputMap() {
        return getDialog().getLayeredPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * Subclasses may override to customize how this dialog is built.
     */
    protected void addDialogComponents() {
        JComponent dialogContentPane = createDialogContentPane();
        GuiStandardUtils.attachDialogBorder(dialogContentPane);
        if (getPreferredSize() != null) {
            dialogContentPane.setPreferredSize(getPreferredSize());
        }
        getDialogContentPane().add(dialogContentPane);
        getDialogContentPane().add(createButtonBar(), BorderLayout.SOUTH);
    }

    protected Point getLocation() {
        return location;
    }
    
    protected Component getLocationRelativeTo() {
    	return locationRelativeTo;
    }

    protected Dimension getPreferredSize() {
        return preferredSize;
    }

    /**
     * Return the GUI which allows the user to manipulate the business objects related to
     * this dialog; this GUI will be placed above the <code>OK</code> and
     * <code>Cancel</code> buttons, in a standard manner.
     */
    protected abstract JComponent createDialogContentPane();

    protected final void attachListeners() {
        dialog.addWindowFocusListener(dialogEventHandler);
        dialog.addWindowListener(dialogEventHandler);
    }

    /**
     * Return a standardized row of command buttons, right-justified and all of the same
     * size, with OK as the default button, and no mnemonics used, as per the Java Look
     * and Feel guidelines.
     */
    protected JComponent createButtonBar() {
        this.dialogCommandGroup = CommandGroup.createCommandGroup(null, getCommandGroupMembers());
        JComponent buttonBar = this.dialogCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }

    /**
     * Template getter method to return the commands to populate the dialog button bar.
     * 
     * @return The array of commands (may also be a separator or glue identifier)
     */
    protected Object[] getCommandGroupMembers() {
        return new AbstractCommand[] {getFinishCommand(), getCancelCommand()};
    }

    /**
     * Register the finish button as the default dialog button.
     */
    protected void registerDefaultCommand() {
        if (isControlCreated()) {
            finishCommand.setDefaultButtonIn(getDialog());
        }
    }

    /**
     * Register the cancel button as the default dialog button.
     */
    protected final void registerCancelCommandAsDefault() {
        if (isControlCreated()) {
            cancelCommand.setDefaultButtonIn(getDialog());
        }
    }

    /**
     * Register the provided button as the default dialog button. The button must be
     * present on the dialog.
     * 
     * @param button The button to become the default.
     */
    protected final void registerDefaultCommand(ActionCommand command) {
        if (isControlCreated()) {
            command.setDefaultButtonIn(getDialog());
        }
    }

    /**
     * Template lifecycle method invoked after the dialog control is initialized.
     */
    protected void onInitialized() {
    }

    /**
     * Template lifecycle method invoked right before the dialog is to become visible.
     */
    protected void onAboutToShow() {
    }

    /**
     * Template lifecycle method invoked when the dialog gains focus.
     */
    protected void onWindowGainedFocus() {
    }

    /**
     * Template lifecycle method invoked when the dialog is activated.
     */
    protected void onWindowActivated() {
    }

    /**
     * Template lifecycle method invoked when the dialog loses focus.
     */
    protected void onWindowLostFocus() {
    }

    /**
     * Template lifecycle method invoked when the dialog's window is closing.
     */
    protected void onWindowClosing() {
    }

    /**
     * Handle a dialog cancellation request.
     */
    protected void onCancel() {
        executeCloseAction();
    }

    private void executeCloseAction() {
        if (closeAction == CloseAction.HIDE) {
            hide();
        }
        else {
            dispose();
        }
    }

    /**
     * Close and dispose of the editor dialog. This forces the dialog to be re-built on
     * the next show.
     */
    protected final void dispose() {
        if (dialog != null) {
            onWindowClosing();
            this.dialog.dispose();
            this.dialog = null;
        }
    }

    /**
     * Hide the dialog. This differs from dispose in that the dialog control stays cached
     * in memory.
     */
    protected final void hide() {
    	if(dialog != null) {
    		onWindowClosing();
    		this.dialog.setVisible(false);
    	}
    }

    /**
	 * Returns the parent window based on the internal parent Component. Will
	 * search for a Window in the parent hierarchy if needed (when parent
	 * Component isn't a Window).
	 * 
	 * @return the parent window
	 */
    public Window getParentWindow() {
    	if (parentWindow == null) {
	    	if ((parentComponent == null) && (getActiveWindow() != null)) {
	    		parentWindow = getActiveWindow().getControl();
	    	}
	    	else {
	    		parentWindow = getWindowForComponent(parentComponent);
	    	}
    	}
        return parentWindow;
    }

    private class DialogEventHandler extends WindowAdapter implements WindowFocusListener {
        public void windowActivated(WindowEvent e) {
            onWindowActivated();
        }

        public void windowClosing(WindowEvent e) {
            getCancelCommand().execute();
        }

        public void windowGainedFocus(WindowEvent e) {
            onWindowGainedFocus();
        }

        public void windowLostFocus(WindowEvent e) {
            onWindowLostFocus();
        }
    }
}
