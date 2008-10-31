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

import javax.swing.JOptionPane;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.security.Authentication;

/**
 * Provides a command to log the current user out.
 * <p>
 * Logout handling is performed by calling {@link ApplicationSecurityManager#doLogout()}.
 * See that class for details.
 * <P>
 * No server-side call will occur to indicate logout. If this is required, you
 * should extend this class and use the {@link #onLogout} method.
 * 
 * @author Ben Alex
 * @author Larry Streepy
 */
public class LogoutCommand extends ApplicationWindowAwareCommand {
    private static final String ID = "logoutCommand";

    public LogoutCommand() {
        super(ID);
    }

    private boolean displaySuccess = true;

    /**
     * Indicates whether an information message is displayed to the user upon
     * successful logout. Defaults to true.
     * 
     * @param displaySuccess
     *            displays an information message upon successful logout if
     *            true, otherwise false
     */
    public void setDisplaySuccess(boolean displaySuccess) {
        this.displaySuccess = displaySuccess;
    }

    protected void doExecuteCommand() {
        ApplicationSecurityManager sm = (ApplicationSecurityManager)ApplicationServicesLocator.services().getService(ApplicationSecurityManager.class);
        Authentication loggedOutAuth = sm.doLogout();
        onLogout(loggedOutAuth);

        if (displaySuccess) {
            JOptionPane.showMessageDialog(getParentWindowControl(), "You have been logged out.", "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Can be extended by subclasses to perform additional logout processing,
     * such as notifying a server etc.
     */
    public void onLogout(Authentication loggedOut) {

    }

}