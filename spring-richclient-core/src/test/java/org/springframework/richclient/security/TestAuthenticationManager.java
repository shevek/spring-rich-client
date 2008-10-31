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
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.LockedException;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * Test implementation giving us control over the authentication results.
 * 
 * @author Larry Streepy
 */
public class TestAuthenticationManager implements AuthenticationManager {

    /** Test role */
    public static final String ROLE_EXPECTED = "ROLE_EXPECTED";
    
    /** Token to use to force a successful authentication. */
    public static final Authentication VALID_USER1 = new TestingAuthenticationToken( "USER1", "FOO",
        new GrantedAuthority[] { new GrantedAuthorityImpl( ROLE_EXPECTED ) } );

    /** Token to use to force a successful authentication. */
    public static final Authentication VALID_USER2 = new TestingAuthenticationToken( "USER2", "FOO");

    /** Token to use to force a failed (bad credentials) authentication. */
    public static final Authentication BAD_CREDENTIALS = new TestingAuthenticationToken( "FAIL", "FOO");

    /** Token to use to force a LOCKED authentication exception. */
    public static final Authentication LOCKED = new TestingAuthenticationToken( "LOCKED", "FOO");

    /**
     * Construct a token with the given id, password, and role
     */
    public static Authentication makeAuthentication( String user, String password, String role ) {
        return new TestingAuthenticationToken( user, password,
            new GrantedAuthority[] { new GrantedAuthorityImpl( role ) } );
    }

    /**
     * Authenticate a token
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if( authentication == BAD_CREDENTIALS ) {
            throw new BadCredentialsException( "Bad credentials" );
        } else if( authentication == LOCKED ) {
            throw new LockedException( "Account is locked" );
        }
        return authentication;
    }
}
