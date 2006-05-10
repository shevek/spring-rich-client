/**
 * 
 */
package org.springframework.richclient.security.support;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationWindow;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.security.ApplicationSecurityManager;
import org.springframework.richclient.security.SecurityController;
import org.springframework.richclient.security.SecurityControllerManager;
import org.springframework.richclient.security.TestAuthenticationManager;

/**
 * @author Larry Streepy
 * 
 */
public class DefaultSecurityControllerManagerTests extends TestCase {
    private ClassPathXmlApplicationContext _applicationContext;
    private TestAuthorizable _testAuth1;
    private SecurityControllerManager _manager;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Application.load( null );
        TestApplicationLifecycleAdvisor ala = new TestApplicationLifecycleAdvisor();
        ala.setWindowCommandBarDefinitions( "org/springframework/richclient/security/support/test-command-ctx.xml" );
        Application app = new Application( ala );
        _applicationContext = new ClassPathXmlApplicationContext(
            "org/springframework/richclient/security/support/test-security-controller-ctx.xml" );
        app.setApplicationContext( _applicationContext );

        ala.setStartingPageId( "start" );
        ala.setApplication( app );
        app.openWindow( "start" );

        _testAuth1 = (TestAuthorizable) _applicationContext.getBean( "testAuth1" );
        _manager = (SecurityControllerManager)ApplicationServicesLocator.services().getService(SecurityControllerManager.class);

        // Prepare the command context
        ala.createWindowCommandManager();
    }

    /**
     * Test alias registration
     */
    public void testRegisterSecurityControllerAlias() {
        SecurityController controller = new UserRoleSecurityController();
        _manager.registerSecurityControllerAlias( "newAlias", controller );

        assertEquals( "Should be same controller", controller, _manager.getSecurityController( "newAlias" ) );
    }

    /**
     * Test obtaining controllers
     */
    public void testGetSecurityController() {
        SecurityController write = (SecurityController) _applicationContext.getBean( "writeController",
            SecurityController.class );
        SecurityController admin = (SecurityController) _applicationContext.getBean( "adminController",
            SecurityController.class );

        // test defaulting to bean id if no alias registered
        assertEquals( "Should be same controller", write, _manager.getSecurityController( "writeController" ) );
        assertEquals( "Should be same controller", admin, _manager.getSecurityController( "adminController" ) );

        // Test registered alias
        assertEquals( "Should be same controller", admin, _manager.getSecurityController( "adminAlias" ) );
    }

    /**
     * Test the processing of beans referenced in the app context.
     */
    public void testApplicationContext() {
        ApplicationSecurityManager securityManager = (ApplicationSecurityManager)ApplicationServicesLocator.services().getService(ApplicationSecurityManager.class);

        assertFalse( "Object should not be authorized", _testAuth1.isAuthorized() );
        assertEquals( "Object should be updated", 1, _testAuth1.getAuthCount() );

        CommandManager cmgr = Application.instance().getActiveWindow().getCommandManager();
        ActionCommand cmdWrite = cmgr.getActionCommand( "cmdWrite" );
        ActionCommand cmdAdmin = cmgr.getActionCommand( "cmdAdmin" );
        ActionCommand cmdAdminAlias = cmgr.getActionCommand( "cmdAdminAlias" );

        assertFalse( "Object should not be authorized", cmdWrite.isAuthorized() );
        assertFalse( "Object should not be authorized", cmdAdmin.isAuthorized() );
        assertFalse( "Object should not be authorized", cmdAdminAlias.isAuthorized() );

        // Now login with ROLE_WRITE
        Authentication auth = TestAuthenticationManager.makeAuthentication( "test", "test", "ROLE_WRITE" );
        securityManager.doLogin( auth );

        assertTrue( "Object should be authorized", cmdWrite.isAuthorized() );
        assertFalse( "Object should not be authorized", cmdAdmin.isAuthorized() );
        assertFalse( "Object should not be authorized", cmdAdminAlias.isAuthorized() );
        assertFalse( "Object should not be authorized", _testAuth1.isAuthorized() );
        assertEquals( "Object should be updated", 2, _testAuth1.getAuthCount() );

        // Now login with ROLE_ADMIN
        auth = TestAuthenticationManager.makeAuthentication( "test", "test", "ROLE_ADMIN" );
        securityManager.doLogin( auth );

        assertTrue( "Object should be authorized", cmdWrite.isAuthorized() );
        assertTrue( "Object should be authorized", cmdAdmin.isAuthorized() );
        assertTrue( "Object should be authorized", cmdAdminAlias.isAuthorized() );
        assertTrue( "Object should be authorized", _testAuth1.isAuthorized() );
        assertEquals( "Object should be updated", 3, _testAuth1.getAuthCount() );
    }

    /**
     * Test that the authorized state overrides the enabled state
     */
    public void testAuthorizedOverridesEnabled() {
        ApplicationSecurityManager securityManager = (ApplicationSecurityManager)ApplicationServicesLocator.services().getService(ApplicationSecurityManager.class);
        CommandManager cmgr = Application.instance().getActiveWindow().getCommandManager();
        ActionCommand cmdWrite = cmgr.getActionCommand( "cmdWrite" );

        // We start with no authentication, so nothing should be authorized
        assertFalse( "Object should not be authorized", cmdWrite.isAuthorized() );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );

        // Try to enable them, should not happen
        cmdWrite.setEnabled( true );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );

        // Now authorize it
        Authentication auth = TestAuthenticationManager.makeAuthentication( "test", "test", "ROLE_WRITE" );
        securityManager.doLogin( auth );

        assertTrue( "Object should be authorized", cmdWrite.isAuthorized() );
        assertTrue( "Object should be enabled", cmdWrite.isEnabled() );

        // Now we should be able to disable and re-enabled it
        cmdWrite.setEnabled( false );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );
        cmdWrite.setEnabled( true );
        assertTrue( "Object should be enabled", cmdWrite.isEnabled() );

        // Now leave it disabled, remove the authorization, re-authorize and it
        // should still be disabled
        cmdWrite.setEnabled( false );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );
        securityManager.doLogout();

        assertFalse( "Object should not be authorized", cmdWrite.isAuthorized() );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );

        securityManager.doLogin( auth );

        assertTrue( "Object should be authorized", cmdWrite.isAuthorized() );
        assertFalse( "Object should not be enabled", cmdWrite.isEnabled() );

    }

    /**
     * Special ApplicationWindow class for testing.
     */
    public static class TestApplicationWindow extends DefaultApplicationWindow {

        public TestApplicationWindow() {
            super( 1 );
        }

        public void showPage(String pageId) {
            System.out.println( "showPage: " + pageId );
        }
    }

    public static class TestApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

        public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
            // Do nothing
        }
    }
}
