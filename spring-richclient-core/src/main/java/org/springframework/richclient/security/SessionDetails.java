package org.springframework.richclient.security;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.rules.constraint.Constraint;
import org.springframework.richclient.application.Application;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.Rules;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.SpringSecurityException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

/**
 * This class provides a bean suitable for use in a login form, providing properties for
 * storing the user name and password.  It also provides
 * event firing for application security lifecycle.  {@link LoginCommand} uses this
 * object as its form object to collect the user name and password to use in an
 * authentication request.
 * <p>
 * The actual authentication request is handled here, in the {@link #login()} method.  
 * 
 * <P>
 * Temporarily stores the username and password provided by the user.
 * 
 * @deprecated by the creation of new {@link ApplicationSecurityManager}
 * 
 * @author Ben Alex
 * @see ClientSecurityEvent
 * @see LoginCommand
 * @see LogoutCommand
 */
public class SessionDetails implements Serializable, PropertyConstraintProvider {
    public static final String PROPERTY_USERNAME = "username";

    public static final String PROPERTY_PASSWORD = "password";

    private static final Log logger = LogFactory.getLog( SessionDetails.class );

    private transient AuthenticationManager authenticationManager;

    private String username;

    private String password;

    private Rules validationRules;

    public SessionDetails() {
        // Retrieve any existing login information from the
        // ContextHolder
    	if (SecurityContextHolder.getContext().getAuthentication() != null) {
    		setUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            setPassword(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
    	}
        initRules();
    }

    protected void initRules() {
        this.validationRules = new Rules(getClass()) {
            protected void initRules() {
                add(PROPERTY_USERNAME, all(new Constraint[] { required(), maxLength(getUsernameMaxLength()) }));
                add(PROPERTY_PASSWORD, all(new Constraint[] { required(), minLength(getPasswordMinLength()) }));
            }

            protected int getUsernameMaxLength() {
                return 8;
            }

            protected int getPasswordMinLength() {
                return 2;
            }

        };
    }

    public PropertyConstraint getPropertyConstraint(String propertyName) {
        return validationRules.getPropertyConstraint(propertyName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthenticationManager(AuthenticationManager manager) {
        this.authenticationManager = manager;
    }

    public void login() throws SpringSecurityException {
        final ApplicationContext appCtx = Application.instance().getApplicationContext();

        // Attempt login
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(getUsername(),
                getPassword());

        Authentication result = null;

        try {
            result = authenticationManager.authenticate(request);
        } catch( SpringSecurityException e ) {
            logger.warn( "authentication failed", e);

            // Fire application event to advise of failed login
            appCtx.publishEvent( new AuthenticationFailedEvent(request, e));
            
            // And rethrow the exception to prevent the dialog from closing
            throw e;
        }

        // Handle success or failure of the authentication attempt
        if( logger.isDebugEnabled()) {
            logger.debug("successful login - update context holder and fire event");
        }

        // Commit the successful Authentication object to the secure
        // ContextHolder
        SecurityContextHolder.getContext().setAuthentication(result);

        // Fire application event to advise of new login
        appCtx.publishEvent(new LoginEvent(result));
    }

    public static Authentication logout() {
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();

        // Make the Authentication object null if a SecureContext exists
        SecurityContextHolder.getContext().setAuthentication(null);

        // Create a non-null Authentication object if required (to meet
        // ApplicationEvent contract)
        if (existing == null) {
            existing = ClientSecurityEvent.NO_AUTHENTICATION;
        }

        // Fire application event to advise of logout
        ApplicationContext appCtx = Application.instance().getApplicationContext();
        appCtx.publishEvent(new LogoutEvent(existing));

        return existing;
    }

}