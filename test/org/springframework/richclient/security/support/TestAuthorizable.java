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

    private boolean _authorized;
    private int _authCount = 0;

    public TestAuthorizable() {
        _authorized = false;
    }

    public TestAuthorizable( boolean authorized ) {
        _authorized = authorized;
    }

    public void setAuthorized(boolean authorized) {
        _authCount += 1;
        _authorized = authorized;
    }

    public boolean isAuthorized() {
        return _authorized;
    }
    
    public int getAuthCount() {
        return _authCount;
    }
    public void resetAuthCount() {
        _authCount = 0;
    }
}
