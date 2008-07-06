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

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.security.support.DefaultApplicationSecurityManager;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.BadCredentialsException;

/**
 * @author Larry Streepy
 * 
 */
public class SecurityAwareConfigurerTests extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;
    private AuthAwareBean authAwareBean;
    private LoginAwareBean loginAwareBean;
    private static int sequence = 0;
    private ApplicationSecurityManager securityManager;

    protected void setUp() throws Exception {
        super.setUp();
        applicationContext = new ClassPathXmlApplicationContext(
            "org/springframework/richclient/security/security-test-configurer-ctx.xml" );
        Application.load( null );
        Application app = new Application( new DefaultApplicationLifecycleAdvisor() );
        app.setApplicationContext( applicationContext );

        securityManager = (ApplicationSecurityManager)ApplicationServicesLocator.services().getService(ApplicationSecurityManager.class);
        authAwareBean = (AuthAwareBean) applicationContext.getBean( "authAwareBean" );
        loginAwareBean = (LoginAwareBean) applicationContext.getBean( "loginAwareBean" );
    }

    public void testConfiguration() {
        Object asm = applicationContext.getBean( "applicationSecurityManager" );
        Object am = applicationContext.getBean( "authenticationManager" );
        Object sc = applicationContext.getBean( "securityConfigurer" );

        assertTrue( "securityManager must implement ApplicationSecurityManager",
            asm instanceof ApplicationSecurityManager );
        assertTrue( "securityManager must be instance of DefaultApplicationSecurityManager",
            asm instanceof DefaultApplicationSecurityManager );
        assertTrue( "authenticationManager must implement AuthenticationManager", am instanceof AuthenticationManager );
        assertTrue( "authenticationManager must be instance of TestAuthenticationManager",
            am instanceof TestAuthenticationManager );
        assertEquals( asm, ApplicationServicesLocator.services().getService(ApplicationSecurityManager.class) );
        assertTrue( "securityConfigurer must implement SecurityAwareConfigurer", sc instanceof SecurityAwareConfigurer );
    }

    public void testAuthenticationAware() {

        securityManager.doLogin( TestAuthenticationManager.VALID_USER1 );
        assertEquals( "Authentication token should be VALID_USER1", authAwareBean.authentication,
            TestAuthenticationManager.VALID_USER1 );

        securityManager.doLogin( TestAuthenticationManager.VALID_USER2 );
        assertEquals( "Authentication token should be VALID_USER2", authAwareBean.authentication,
            TestAuthenticationManager.VALID_USER2 );

        try {
            securityManager.doLogin( TestAuthenticationManager.BAD_CREDENTIALS );
            fail( "Exception should have been thrown" );
        } catch( BadCredentialsException e ) {
            // Shouldn't have been changed
            assertEquals( "Authentication token should be VALID_USER2", authAwareBean.authentication,
                TestAuthenticationManager.VALID_USER2 );
        }

        securityManager.doLogout();
        assertNull( "Authentication token should have been cleared", authAwareBean.authentication );
    }

    public void testLoginAware() {

        securityManager.doLogin( TestAuthenticationManager.VALID_USER1 );
        assertEquals( "Authentication token should be VALID_USER1", loginAwareBean.authentication,
            TestAuthenticationManager.VALID_USER1 );
        assertEquals( "Authentication tokens on beans should be equal ", authAwareBean.authentication,
            loginAwareBean.authentication );
        assertTrue( "LoginAware notifications should happen after AuthAware",
            authAwareBean.sequence < loginAwareBean.sequence );

        loginAwareBean.reset();
        securityManager.doLogout();
        assertTrue( "Logout should be called", loginAwareBean.logoutCalled );
        assertEquals( "Previous token should be VALID_USER1", loginAwareBean.oldAuthentication,
            TestAuthenticationManager.VALID_USER1 );
        assertTrue( "LoginAware notifications should happen after AuthAware",
            authAwareBean.sequence < loginAwareBean.sequence );
    }

    /**
     * Class to test automatic notification.
     */
    public static class AuthAwareBean implements AuthenticationAware {

        public Authentication authentication = null;
        public int sequence;

        public void setAuthenticationToken(Authentication authentication) {
            this.authentication = authentication;
            sequence = SecurityAwareConfigurerTests.sequence++;
        }

        public void reset() {
            authentication = null;
            sequence = 0;
        }
    }

    /**
     * Class to test automatic notification of login/logout events.
     */
    public static class LoginAwareBean implements LoginAware {
        public Authentication authentication = null;
        public Authentication oldAuthentication = null;
        public int sequence;
        public boolean logoutCalled = false;

        public void userLogin(Authentication authentication) {
            this.authentication = authentication;
            sequence = SecurityAwareConfigurerTests.sequence++;
        }

        public void userLogout(Authentication authentication) {
            this.oldAuthentication = authentication;
            logoutCalled = true;
            sequence = SecurityAwareConfigurerTests.sequence++;
        }

        public void reset() {
            authentication = null;
            oldAuthentication = null;
            sequence = 0;
            logoutCalled = false;
        }
    }
}
