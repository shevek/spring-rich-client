/**
 * 
 */
package org.springframework.richclient.security.support;

import java.util.ArrayList;

import junit.framework.TestCase;
import net.sf.acegisecurity.AccessDecisionManager;
import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.providers.TestingAuthenticationToken;

/**
 * @author Larry Streepy
 * 
 */
public class SecurityControllerTests extends TestCase {

    private TestAbstractSecurityController _controller;
    private TestAccessDecisionManager _accessDecisionManager;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        _controller = new TestAbstractSecurityController();
        _accessDecisionManager = new TestAccessDecisionManager();
        _controller.setAccessDecisionManager( _accessDecisionManager );
    }

    /**
     * Test that setting the authentication token updates all controlled objects.
     */
    public void testSetAuthenticationToken() {

        // Validate that the controller updates the controlled objects whenever the
        // Authentication token is updated
        TestAuthorizable a1 = new TestAuthorizable( true );
        TestAuthorizable a2 = new TestAuthorizable( true );
        _controller.addControlledObject( a1 );
        _controller.addControlledObject( a2 );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );

        a1.resetAuthCount();
        a2.resetAuthCount();

        // Set the decision manager to authorize
        _accessDecisionManager.setDecisionValue( true );

        // Now install a token, a should be updated
        _controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO", null ) );
        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 1 );
        assertTrue( "Object should be authorized", a2.isAuthorized() );
        assertEquals( "Object should be updated", a2.getAuthCount(), 1 );

        _controller.setAuthenticationToken( null );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 2 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertEquals( "Object should be updated", a2.getAuthCount(), 2 );

        // Set the decision manager to NOT authorize
        _accessDecisionManager.setDecisionValue( false );

        // Now install a token, a should be updated
        _controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO", null ) );
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
            _controller.setControlledObjects( badList );
            fail( "Should reject objects that aren't Authorizable" );
        } catch( IllegalArgumentException e ) {
            // expected
        }

        _controller.setControlledObjects( goodList );
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
        _controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO", null ) );

        // Set the decision manager to authorize
        _accessDecisionManager.setDecisionValue( true );

        TestAuthorizable a1 = new TestAuthorizable( false );
        _controller.addControlledObject( a1 );
        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertTrue( "Object should be updated", a1.getAuthCount() == 1 );

        // Set the decision manager to NOT authorize
        _accessDecisionManager.setDecisionValue( false );

        TestAuthorizable a2 = new TestAuthorizable( true );
        _controller.addControlledObject( a2 );
        assertFalse( "Object should not be authorized", a2.isAuthorized() );
        assertTrue( "Object should be updated", a2.getAuthCount() == 1 );
    }

    /**
     * Test that once removed an object is no longer updated.
     */
    public void testRemoveControlledObject() {
        TestAuthorizable a = new TestAuthorizable( true );
        _controller.addControlledObject( a );
        assertFalse( "Object should not be authorized", a.isAuthorized() );
        assertTrue( "Object should be updated", a.getAuthCount() == 1 );

        _controller.removeControlledObject( a );
        a.resetAuthCount();

        // Set the decision manager to authorize
        _accessDecisionManager.setDecisionValue( true );
        _controller.setAuthenticationToken( new TestingAuthenticationToken( "USER2", "FOO", null ) );

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

        private boolean _decisionValue;

        public void setDecisionValue(boolean decisionValue) {
            _decisionValue = decisionValue;
        }

        public void decide(Authentication authentication, Object object, ConfigAttributeDefinition config)
                throws AccessDeniedException {
            if( !_decisionValue ) {
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
