/**
 * 
 */
package org.springframework.richclient.security.support;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.security.AccessDecisionManager;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * @author Larry Streepy
 * 
 */
public class SecurityControllerTests extends TestCase {

    private TestAbstractSecurityController controller;
    private TestAccessDecisionManager accessDecisionManager;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        controller = new TestAbstractSecurityController();
        accessDecisionManager = new TestAccessDecisionManager();
        controller.setAccessDecisionManager(accessDecisionManager);
    }

    /**
     * Test that setting the authentication token updates all controlled objects.
     */
    public void testSetAuthenticationToken() {

        // Validate that the controller updates the controlled objects whenever the
        // Authentication token is updated
        TestAuthorizable a1 = new TestAuthorizable( true );
        TestAuthorizable a2 = new TestAuthorizable( true );
        controller.addControlledObject( a1 );
        controller.addControlledObject( a2 );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );

        a1.resetAuthCount();
        a2.resetAuthCount();

        // Set the decision manager to authorize
        accessDecisionManager.setDecisionValue( true );

        // Now install a token, a should be updated
        controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO") );
        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 1 );
        assertTrue( "Object should be authorized", a2.isAuthorized() );
        assertEquals( "Object should be updated", a2.getAuthCount(), 1 );

        controller.setAuthenticationToken( null );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 2 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertEquals( "Object should be updated", a2.getAuthCount(), 2 );

        // Set the decision manager to NOT authorize
        accessDecisionManager.setDecisionValue( false );

        // Now install a token, a should be updated
        controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO") );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 3 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertEquals( "Object should be updated", a2.getAuthCount(), 3 );
    }

    public void testSetControlledObjects() {

        TestAuthorizable a1 = new TestAuthorizable( true );
        TestAuthorizable a2 = new TestAuthorizable( true );
        ArrayList goodList = new ArrayList();
        goodList.add( a1 );
        goodList.add( a2 );

        ArrayList badList = new ArrayList();
        badList.add( new Object() );

        try {
            controller.setControlledObjects( badList );
            fail( "Should reject objects that aren't Authorizable" );
        } catch( IllegalArgumentException e ) {
            // expected
        }

        controller.setControlledObjects( goodList );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertTrue( "Object should be updated", a1.getAuthCount() == 1 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertTrue( "Object should be updated", a2.getAuthCount() == 1 );
    }

    /**
     * Test that added objects are initially configured.
     */
    public void testAddControlledObject() {

        // Install an authentication token
        controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO") );

        // Set the decision manager to authorize
        accessDecisionManager.setDecisionValue( true );

        TestAuthorizable a1 = new TestAuthorizable( false );
        controller.addControlledObject( a1 );
        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertTrue( "Object should be updated", a1.getAuthCount() == 1 );

        // Set the decision manager to NOT authorize
        accessDecisionManager.setDecisionValue( false );

        TestAuthorizable a2 = new TestAuthorizable( true );
        controller.addControlledObject( a2 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertTrue( "Object should be updated", a2.getAuthCount() == 1 );
    }

    /**
     * Test that once removed an object is no longer updated.
     */
    public void testRemoveControlledObject() {
        TestAuthorizable a = new TestAuthorizable( true );
        controller.addControlledObject( a );
        assertFalse( "Object should not be authorized", a.isAuthorized() );
        assertTrue( "Object should be updated", a.getAuthCount() == 1 );

        controller.removeControlledObject( a );
        a.resetAuthCount();

        // Set the decision manager to authorize
        accessDecisionManager.setDecisionValue( true );
        controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO" ) );

        assertFalse( "Object should not be authorized", a.isAuthorized() );
        assertTrue( "Object should not be updated", a.getAuthCount() == 0 );
    }

    /**
     * Concrete implementation under test.
     */
    public class TestAbstractSecurityController extends AbstractSecurityController {

        public TestAbstractSecurityController() {
        }

        protected Object getSecuredObject() {
            return null;
        }

        protected ConfigAttributeDefinition getConfigAttributeDefinition(Object securedObject) {
            return null;
        }
    }

    /**
     * Controllable AccessDecisionManager
     */
    public class TestAccessDecisionManager implements AccessDecisionManager {

        private boolean decisionValue;

        public void setDecisionValue(boolean decisionValue) {
            this.decisionValue = decisionValue;
        }

        public void decide(Authentication authentication, Object object, ConfigAttributeDefinition config)
                throws AccessDeniedException {
            if( !decisionValue) {
                throw new AccessDeniedException( "access denied" );
            }
        }

        public boolean supports(ConfigAttribute attribute) {
            return false;
        }

        public boolean supports(Class clazz) {
            return false;
        }
    }
}
