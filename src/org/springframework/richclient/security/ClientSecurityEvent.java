package org.springframework.richclient.security;

import org.springframework.context.ApplicationEvent;

/**
 * Parent for all RCP security related application events.
 * 
 * @author Ben Alex
 */
public abstract class ClientSecurityEvent extends ApplicationEvent {
	public ClientSecurityEvent(Object source) {
		super(source);
	}
}