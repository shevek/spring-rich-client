package org.springframework.richclient.security;

import net.sf.acegisecurity.Authentication;

/**
 * Event fired when a new user logs in.
 * 
 * <P>
 * Upon login the <code>ContextHolder</code> is updated with a new
 * <code>Authentication</code> object. In addition, the new
 * <code>Authentication</code> object is provided in the
 * <code>LoginEvent</code> message.
 * 
 * @author Ben Alex
 */
public class LoginEvent extends ClientSecurityEvent {
	public LoginEvent(Authentication authentication) {
		super(authentication);
	}
}