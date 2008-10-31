/*
 * Copyright (c) 2002-2005 the original author or authors.
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
package org.springframework.richclient.security;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CompositeDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.security.Authentication;

/**
 * Provides a login interface to the user.
 * <P>
 * Presents a dialog to the user to collect login credentials. It then invokes the
 * {@link ApplicationSecurityManager#doLogin} method to validate the credentials. The
 * ApplicationSecurityManager is responsible for updating the security context and firing
 * appropriate security events.
 * <p>
 * The default user name can be specified as a configuration parameter. This is useful
 * when combined with
 * {@link org.springframework.beans.factory.config.PropertyPlaceholderConfigurer} to
 * install the current (system) username.
 * <p>
 * If the login is unsuccesful, a message is presented to the user and they are offered
 * another chance to login.
 * <p>
 * The <code>closeOnCancel</code> property controls what happens if the user cancels the
 * login dialog. If closeOnCancel is true (the default), if there is no valid
 * authentication in place (from a previous login) then the application is closed. If it
 * is false or an authentication token is available, then no action is taken other than
 * closing the dialog.
 * <p>
 * The <code>clearPasswordOnFailure</code> controls the handling of the password field
 * after a login failure. If clearPasswordOnFailure is <code>true</code> (the default),
 * then the password field will be cleared after the failure is reported.
 * <p>
 * A typical configuration for this component might look like this:
 *
 * <pre>
 *        &lt;bean id=&quot;placeholderConfigurer&quot;
 *             class=&quot;org.springframework.beans.factory.config.PropertyPlaceholderConfigurer&quot;/&gt;
 *
 *         &lt;bean id=&quot;loginCommand&quot;
 *             class=&quot;org.springframework.richclient.security.LoginCommand&quot;&gt;
 *             &lt;property name=&quot;displaySuccess&quot; value=&quot;false&quot;/&gt;
 *             &lt;property name=&quot;defaultUserName&quot; value=&quot;${user.name}&quot;/&gt;
 *         &lt;/bean&gt;
 * </pre>
 *
 * @author Ben Alex
 * @author Larry Streepy
 *
 * @see LoginForm
 * @see LoginDetails
 * @see ApplicationSecurityManager
 */
public class LoginCommand extends ApplicationWindowAwareCommand {
    private static final String ID = "loginCommand";

    private boolean displaySuccessMessage = true;

    private boolean closeOnCancel = true;

    private boolean clearPasswordOnFailure = true;

    private String defaultUserName = null;

    private ApplicationDialog dialog = null;

    /**
     * Constructor.
     */
    public LoginCommand() {
        super( ID );
    }

    /**
     * Indicates whether an information message is displayed to the user upon successful
     * authentication. Defaults to true.
     *
     * @param displaySuccessMessage displays an information message upon successful login if
     *            true, otherwise false
     */
    public void setDisplaySuccess(boolean displaySuccessMessage) {
        this.displaySuccessMessage = displaySuccessMessage;
    }

    /**
     * Execute the login command. Display the dialog and attempt authentication.
     */
    protected void doExecuteCommand() {
        CompositeDialogPage tabbedPage = new TabbedDialogPage( "loginForm" );

        final LoginForm loginForm = createLoginForm();

        tabbedPage.addForm( loginForm );

        if( getDefaultUserName() != null ) {
            loginForm.setUserName( getDefaultUserName() );
        }

        dialog = new TitledPageApplicationDialog( tabbedPage ) {
            protected boolean onFinish() {
                loginForm.commit();
                Authentication authentication = loginForm.getAuthentication();

                // Hand this token to the security manager to actually attempt the login
                ApplicationSecurityManager sm = (ApplicationSecurityManager)getService(ApplicationSecurityManager.class);
                try {
                    sm.doLogin( authentication );
                    postLogin();
                    return true;
                } finally {
                    if( isClearPasswordOnFailure() ) {
                        loginForm.setPassword("");
                    }
                    loginForm.requestFocusInWindow();
                }
            }

            protected void onCancel() {
                super.onCancel(); // Close the dialog

                // Now exit if configured
                if( isCloseOnCancel() ) {
                    ApplicationSecurityManager sm = (ApplicationSecurityManager)getService(ApplicationSecurityManager.class);
                    Authentication authentication = sm.getAuthentication();
                    if( authentication == null ) {
                        LoginCommand.this.logger.info( "User canceled login; close the application." );
                        getApplication().close();
                    }
                }
            }

            protected ActionCommand getCallingCommand() {
                return LoginCommand.this;
            }

            protected void onAboutToShow() {
                loginForm.requestFocusInWindow();
            }
        };
        dialog.setDisplayFinishSuccessMessage( displaySuccessMessage );
        dialog.showDialog();
    }

    /**
     * Construct the Form to place in the login dialog.
     * @return form to use
     */
    protected LoginForm createLoginForm() {
        return new LoginForm();
    }

    /**
     * Get the dialog in use, if available.
     * @return dialog instance in use
     */
    protected ApplicationDialog getDialog() {
        return dialog;
    }

    /**
     * Called to give subclasses control after a successful login.
     */
    protected void postLogin() {
    }

    /**
     * Get the "close on cancel" setting.
     * @return close on cancel
     */
    public boolean isCloseOnCancel() {
        return closeOnCancel;
    }

    /**
     * Indicates if the application should be closed if the user cancels the login
     * operation. Default is true.
     * @param closeOnCancel
     */
    public void setCloseOnCancel(boolean closeOnCancel) {
        this.closeOnCancel = closeOnCancel;
    }

    /**
     * Get the "clear password on failure" setting.
     * @return clear password
     */
    public boolean isClearPasswordOnFailure() {
        return clearPasswordOnFailure;
    }

    /**
     * Indicates if the password field should be cleared after a login failure. Default is
     * true.
     * @param clearPasswordOnFailure
     */
    public void setClearPasswordOnFailure(boolean clearPasswordOnFailure) {
        this.clearPasswordOnFailure = clearPasswordOnFailure;
    }

    /**
     * Get the default user name.
     * @return default user name.
     */
    public String getDefaultUserName() {
        return defaultUserName;
    }

    /**
     * Set the default user name.
     * @param defaultUserName to use as default, null indicates no default
     */
    public void setDefaultUserName(String defaultUserName) {
        this.defaultUserName = defaultUserName;
    }

}