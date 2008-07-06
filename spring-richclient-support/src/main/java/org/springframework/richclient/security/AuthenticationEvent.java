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

import org.springframework.security.Authentication;

/**
 * Event fired when the user's authentication token changes.
 * <p>
 * The source of this event is the <code>Authentication</code> token returned by a
 * successful call to
 * {@link org.springframework.security.AuthenticationManager#authenticate(org.springframework.security.Authentication)}
 * or {@link ClientSecurityEvent#NO_AUTHENTICATION} if no authentication is in place (such
 * as after the user logs out).
 * 
 * @author Larry Streepy
 */
public class AuthenticationEvent extends ClientSecurityEvent {

    /**
     * Constructor.
     * @param new authentication token, may be null
     */
    public AuthenticationEvent(Authentication authentication) {
        super( authentication );
    }
}
