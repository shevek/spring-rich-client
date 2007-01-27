/**
 * 
 */
package org.springframework.richclient.security.support;

import org.springframework.richclient.core.Authorizable;

/**
 * Authorizable object for test support
 * @author Larry Streepy
 */
public class TestAuthorizable implements Authorizable {

    private boolean authorized;
    private int authCount = 0;

    public TestAuthorizable() {
        authorized = false;
    }

    public TestAuthorizable( boolean authorized ) {
        this.authorized = authorized;
    }

    public void setAuthorized(boolean authorized) {
        authCount += 1;
        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }
    
    public int getAuthCount() {
        return authCount;
    }
    public void resetAuthCount() {
        authCount = 0;
    }
}
