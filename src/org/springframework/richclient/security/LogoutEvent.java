package org.springframework.richclient.security;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

/**
 * Event fired when a user logs out.
 * 
 * The old <code>Authentication</code> object (if any) is provided as the
 * event source. If no existing <code>Authentication</code> object is
 * available, the {@link #NO_AUTHENTICATION}object must be used.
 * 
 * @author Ben Alex
 */
public class LogoutEvent extends ClientSecurityEvent {
    public static final Authentication NO_AUTHENTICATION = new UsernamePasswordAuthenticationToken(
            "NO_AUTHENTICATION", "NO_AUTHENTICATION");

    public LogoutEvent(Authentication authentication) {
        super(authentication);
    }
}