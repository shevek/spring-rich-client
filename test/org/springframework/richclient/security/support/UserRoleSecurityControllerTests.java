/**
 * 
 */
package org.springframework.richclient.security.support;

import java.util.Iterator;

import junit.framework.TestCase;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.providers.TestingAuthenticationToken;

/**
 * @author Larry Streepy
 * 
 */
public class UserRoleSecurityControllerTests extends TestCase {

    private TestUserRoleSecurityController _controller;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        _controller = new TestUserRoleSecurityController();
    }

    /**
     * Test that the role string is properly parsed
     */
    public void testSetAuthorizingRoles() {
        _controller.setAuthorizingRoles( "ROLE_1,ROLE_2" );

        ConfigAttributeDefinition cad = _controller.getParsedConfigs();
        assertTrue( "Should be 2 roles", cad.size() == 2 );

        Iterator iter = cad.getConfigAttributes();
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
        _controller.setAuthorizingRoles( "ROLE_1,ROLE_2" );

        TestAuthorizable a1 = new TestAuthorizable( false );

        _controller.addControlledObject( a1 );
        assertFalse( "Object should not be authorized", a1.isAuthorized() );

        // Now set the authentication token so that it contains one of these roles
        Authentication auth = new TestingAuthenticationToken( "USER1", "FOO",
            new GrantedAuthority[] { new GrantedAuthorityImpl( "ROLE_1" ) } );
        _controller.setAuthenticationToken( auth );

        assertTrue( "Object should be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 2 );

        // Now to a token that does not contain one of the roles
        auth = new TestingAuthenticationToken( "USER1", "FOO", new GrantedAuthority[] { new GrantedAuthorityImpl(
            "ROLE_NOTFOUND" ) } );
        _controller.setAuthenticationToken( auth );

        assertFalse( "Object should not be authorized", a1.isAuthorized() );
        assertEquals( "Object should be updated", a1.getAuthCount(), 3 );

        // Now to a null
        _controller.setAuthenticationToken( null );

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
