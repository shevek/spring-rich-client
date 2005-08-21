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

import net.sf.acegisecurity.Authentication;


import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

/**
 * Provides a logout interface to the user.
 * 
 * <P>
 * Upon successful logout, makes any {@link Authentication}object on the
 * {@link ContextHolder}null, and fires a {@link LogoutEvent}so other classes
 * can update toolbars, action status, views etc.
 * 
 * <P>
 * No server-side call will occur to indicate logout. If this is required, you
 * should extend this class and use the {@link #finaliseLogout}method.
 * 
 * @author Ben Alex
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
        onLogout(SessionDetails.logout());

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