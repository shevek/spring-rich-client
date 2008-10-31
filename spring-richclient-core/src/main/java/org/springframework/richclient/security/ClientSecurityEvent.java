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

import org.springframework.context.ApplicationEvent;
import org.springframework.security.Authentication;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

/**
 * Parent for all RCP security related application events.
 * 
 * @author Ben Alex
 * @author Larry Streepy
 */
public abstract class ClientSecurityEvent extends ApplicationEvent {
    /**
     * This token is used when the real authentication token is null and it needs to be
     * used as the source of an event.
     */
    public static final Authentication NO_AUTHENTICATION = new UsernamePasswordAuthenticationToken(
        "NO_AUTHENTICATION", "NO_AUTHENTICATION" );

    /**
     * Constructor. Use the given authentication token as the source of the event, if this
     * is null, then the {@link #NO_AUTHENTICATION} token is used instead.
     * 
     * @param authentication token, may be null
     */
    public ClientSecurityEvent(Authentication authentication) {
        super( authentication == null ? NO_AUTHENTICATION : authentication );
    }
}