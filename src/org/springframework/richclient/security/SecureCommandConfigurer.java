package org.springframework.richclient.security;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.SecureContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Correctly configures the command enabled/disabled and visible/invisible state
 * based on changes to the logged in user.
 * 
 * <P>
 * This bean listens for <code>ClientSecurityEvent</code> s and responds as
 * follows:
 * 
 * <P>
 * Upon receipt of a {@link LoginEvent}......
 * 
 * <P>
 * Upon receipt of a {@link LogoutEvent}.....
 * 
 * @author Ben Alex
 */
public class SecureCommandConfigurer implements ApplicationListener {
    protected final Log logger = LogFactory.getLog(getClass());

    public void onApplicationEvent(ApplicationEvent event) {
        // TODO: Add RCP-specific events (such as new page loaded/refreshed)
        if (event instanceof LoginEvent || event instanceof LogoutEvent) {
            updateActions();
        }
    }

    private void updateActions() {
        GrantedAuthority[] granted = null;
        if (ContextHolder.getContext() != null
                && ContextHolder.getContext() instanceof SecureContext) {
            SecureContext secureContext = (SecureContext)ContextHolder
                    .getContext();
            if (secureContext.getAuthentication() != null)
                granted = secureContext.getAuthentication().getAuthorities();
        }

        if (logger.isInfoEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Updating commands using granted authorities: ");
            if (granted != null) {
                for (int i = 0; i < granted.length; i++) {
                    sb.append(granted[i].toString()).append("; ");
                }
            }
            logger.info(sb.toString());
        }

        // TODO: Update the commands....
    }
}