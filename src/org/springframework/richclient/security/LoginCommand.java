package org.springframework.richclient.security;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.providers.rcp.RemoteAuthenticationException;

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.TitledApplicationDialog;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;


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
    //~ Static fields/initializers =============================================

    private static final String ID = "loginCommand";

    //~ Instance fields ========================================================

    private AuthenticationManager authenticationManager;
    private boolean displaySuccess = true;

    //~ Constructors ===========================================================

    public LoginCommand() {
        super(ID);
    }

    //~ Methods ================================================================

    /**
     * The command requires an authentication manager which can attempt to
     * authenticate the user.
     *
     * @param authenticationManager the authentication manager to use to
     *        authenticate the user
     */
    public void setAuthenticationManager(
        AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Indicates whether an information message is displayed to the user upon
     * successful authentication. Defaults to true.
     *
     * @param displaySuccess displays an information message upon successful
     *        login if true, otherwise false
     */
    public void setDisplaySuccess(boolean displaySuccess) {
        this.displaySuccess = displaySuccess;
    }

    public boolean isDisplaySuccess() {
        return displaySuccess;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (this.authenticationManager == null) {
            throw new IllegalArgumentException(
                "authenticationManager must be defined");
        }
    }

    protected void doExecuteCommand() {
        TitledApplicationDialog dialog = new TitledApplicationDialog("Login",
                getParentWindowControl()) {
                private LoginPanel loginGeneralPanel;

                public JComponent createTitledDialogContentPane() {
                    // Construct a login dialog
                    setTitleAreaText("User Login");
                    setDescription(
                        "Please login with your username and password.");

                    JTabbedPane tabbedPane = getComponentFactory()
                                                 .createTabbedPane();
                    this.loginGeneralPanel = new LoginPanel(authenticationManager);
                    this.loginGeneralPanel.newSingleLineResultsReporter(this,
                        this);
                    getComponentFactory().addConfiguredTab(tabbedPane,
                        "General", loginGeneralPanel.getControl());

                    return tabbedPane;
                }

                protected void onWindowGainedFocus() {
                    loginGeneralPanel.requestFocusInWindow();
                }

                protected boolean onFinish() {
                    // todo EXCEPTION workflow! this joption pane stuff has got to
                    // go...:-)
                    try {
                        loginGeneralPanel.commit();
                    } catch (RemoteAuthenticationException authentication) {
                        JOptionPane.showMessageDialog(getDialog(),
                            authentication.getMessage(),
                            "Remote Authentication Failure",
                            JOptionPane.ERROR_MESSAGE);

                        return false;
                    } catch (AuthenticationException authentication) {
                        JOptionPane.showMessageDialog(getDialog(),
                            authentication.getMessage(),
                            "Authentication Failure", JOptionPane.ERROR_MESSAGE);

                        return false;
                    }

                    if (displaySuccess) {
                        JOptionPane.showMessageDialog(getDialog(),
                            "You have logged in as '"
                            + loginGeneralPanel.getValue(
                                SessionDetails.PROPERTY_USERNAME) + "'.",
                            "Authentication Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    }

                    return true;
                }
            };

        dialog.showDialog();
    }
}
