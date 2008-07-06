/**
 * 
 */
package org.springframework.richclient.security.support;

import java.util.Iterator;

import junit.framework.TestCase;

import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * @author Larry Streepy
 * 
 */
public class UserRoleSecurityControllerTests extends TestCase {

    private TestUserRoleSecurityController controller;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        controller = new TestUserRoleSecurityController();
    }

    /**
     * Test that the role string is properly parsed
     */
    public void testSetAuthorizingRoles() {
        controller.setAuthorizingRoles( "ROLE_1,ROLE_2" );

        ConfigAttributeDefinition cad = controller.getParsedConfigs();
        assertTrue( "Should be 2 roles", cad.getConfigAttributes().size() == 2 );

        Iterator iter = cad.getConfigAttributes().iterator();
        ConfigAttribute attr1 = (ConfigAttribute) iter.next();
        ConfigAttribute attr2 = (ConfigAttribute) iter.next();

        assertEquals( "Should be ROLE_1", attr1.getAttribute(), "ROLE_1" );
        assertEquals( "Should be ROLE_2", attr2.getAttribute(), "ROLE_2" );
    }

    /**
     * Test that objects are properly authorized when the user holds any of the indicated
     * roles.
     */
    public void testAuthorization() {
        controller.setAuthorizingRoles( "ROLE_1,ROLE_2" );

        TestAuthorizable a1 = new TestAuthorizable( false );

        controller.addControlledObject( a1 );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );

        // Now set the authentication token so that it contains one of these roles
        Authentication auth = new TestingAuthenticationToken( "USER1", "FOO",
            new GrantedAuthority[] { new GrantedAuthorityImpl( "ROLE_1" ) } );
        controller.setAuthenticationToken( auth );

        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 2 );

        // Now to a token that does not contain one of the roles
        auth = new TestingAuthenticationToken( "USER1", "FOO", new GrantedAuthority[] { new GrantedAuthorityImpl(
            "ROLE_NOTFOUND" ) } );
        controller.setAuthenticationToken( auth );

        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 3 );

        // Now to a null
        controller.setAuthenticationToken( null );

        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 4 );
    }

    /**
     * More accessible implementation.
     */
    public class TestUserRoleSecurityController extends UserRoleSecurityController {
        public ConfigAttributeDefinition getParsedConfigs() {
            return getConfigAttributeDefinition( null );
        }
    }
}
