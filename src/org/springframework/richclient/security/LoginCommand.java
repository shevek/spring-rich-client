package org.springframework.richclient.security;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.context.ContextHolder;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CompositeDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;

/**
 * Provides a login interface to the user.
 * 
 * <P>
 * Upon successful login, updates the {@link ContextHolder}with the new,
 * populated {@link Authentication}object, and fires a {@link LoginEvent}so
 * other classes can update toolbars, action status, views etc.
 * </p>
 * 
 * <P>
 * If a login is unsuccessful, any existing <code>Authentication</code> object
 * on the <code>ContextHolder</code> is preserved and no event is fired.
 * </p>
 * 
 * @author Ben Alex
 */
public class LoginCommand extends ApplicationWindowAwareCommand {
    private static final String ID = "loginCommand";

    private AuthenticationManager authenticationManager;

    private boolean displaySuccessMessage = true;

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
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Indicates whether an information message is displayed to the user upon
     * successful authentication. Defaults to true.
     * 
     * @param displaySuccess
     *            displays an information message upon successful login if true,
     *            otherwise false
     */
    public void setDisplaySuccess(boolean displaySuccessMessage) {
        this.displaySuccessMessage = displaySuccessMessage;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.authenticationManager == null) {
            throw new IllegalArgumentException("authenticationManager must be defined");
        }
    }

    protected void doExecuteCommand() {
        CompositeDialogPage tabbedPage = new TabbedDialogPage("loginForm");

        final LoginForm loginForm = new LoginForm(authenticationManager);
        tabbedPage.addForm(loginForm);

        ApplicationDialog dialog = new TitledPageApplicationDialog(tabbedPage) {
            protected boolean onFinish() {
                loginForm.commit();
                return true;
            }

            protected ActionCommand getCallingCommand() {
                return LoginCommand.this;
            }
        };
        dialog.setDisplayFinishSuccessMessage(displaySuccessMessage);
        dialog.showDialog();
    }
}