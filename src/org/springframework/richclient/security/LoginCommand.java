/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.security;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.context.ContextHolder;

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.TitledApplicationDialog;

/**
 * Provides a login interface to the user.
 * 
 * <P>
 * Upon successful login, updates the {@link ContextHolder}with the new,
 * populated {@link Authentication}object, and fires a {@link LoginEvent}so
 * other classes can update toolbars, action status, views etc.
 * 
 * <P>
 * If a login is unsuccessful, any existing <code>Authentication</code> object
 * on the <code>ContextHolder</code> is preserved and no event is fired.
 * 
 * @author Ben Alex
 */
public class LoginCommand extends ApplicationWindowAwareCommand {
    private static final String ID = "loginCommand";

    private AuthenticationManager authenticationManager;

    private boolean displaySuccess = true;

    public LoginCommand() {
        super(ID);
    }

    /**
     * The command requires an authentication manager which can attempt to
     * authenticate the user.
     * 
     * @param authenticationManager
     *            the authentication manager to use to authenticate the user
     */
    public void setAuthenticationManager(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public boolean isDisplaySuccess() {
        return displaySuccess;
    }

    /**
     * Indicates whether an information message is displayed to the user upon
     * successful authentication. Defaults to true.
     * 
     * @param displaySuccess
     *            displays an information message upon successful login if true,
     *            otherwise false
     */
    public void setDisplaySuccess(boolean displaySuccess) {
        this.displaySuccess = displaySuccess;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (this.authenticationManager == null) { throw new IllegalArgumentException(
                "authenticationManager must be defined"); }

    }

    protected void doExecuteCommand() {
        TitledApplicationDialog dialog = new TitledApplicationDialog("Login",
                getParentWindowControl()) {

            private LoginPanel loginGeneralPanel;

            public JComponent createTitledDialogContentPane() {
                // Construct a login dialog
                setTitleAreaText("User Login");
                setDescription("Please login with your username and password.");

                JTabbedPane tabbedPane = getComponentFactory()
                        .createTabbedPane();
                getComponentFactory().addConfiguredTab(
                        tabbedPane,
                        "General",
                        new LoginPanel(authenticationManager)
                                .getControl());
                return tabbedPane;
            }

            protected void onWindowGainedFocus() {
                loginGeneralPanel.requestFocusInWindow();
            }

            protected boolean onFinish() {
                // todo EXCEPTION workflow! this joption pane stuff has got to go...:-)
                try {
                    loginGeneralPanel.commit();
                }
                catch (AuthenticationException authentication) {
                    JOptionPane
                            .showMessageDialog(getParentWindowControl(),
                                    authentication.getMessage(),
                                    "Authentication Failure",
                                    JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                if (displaySuccess)
                    JOptionPane.showMessageDialog(getParentWindowControl(),
                            "You have logged in as '"
                                    + loginGeneralPanel.getValue("userName")
                                    + "'.", "Authentication Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                return true;
            }
        };
        dialog.showDialog();
    }

}