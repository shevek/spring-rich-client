/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.richclient.security.support;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.acegisecurity.AccessDecisionManager;
import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttributeDefinition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.core.Authorizable;
import org.springframework.richclient.security.SecurityController;
import org.springframework.util.Assert;

/**
 * Abstract implementation of a security controller. Derived classes are responsible for
 * providing the {@link ConfigAttributeDefinition} and any secured object that will be
 * used by the decision manager to make the decision to authorize the controlled objects.
 * <p>
 * This class uses weak references to track the the controlled objects, so they can be GCed
 * as needed.
 * 
 * @author Larry Streepy
 * @see #getSecuredObject()
 * @see #getConfigAttributeDefinition(Object)
 * 
 */
public abstract class AbstractSecurityController implements SecurityController {

    private final Log _logger = LogFactory.getLog( getClass() );

    /** The list of objects that we are controlling. */
    private List _controlledObjects = new ArrayList();

    /** The AccessDecisionManager used to make the "authorize" decision. */
    private AccessDecisionManager _accessDecisionManager;

    /** Last know authentication token. */
    private Authentication _lastAuthentication = null;

    /**
     * Constructor.
     */
    protected AbstractSecurityController() {
    }

    /**
     * Get the secured object on which we are making the authorization decision. This may
     * be null if no specific object is to be considered in the decision.
     * @return secured object
     */
    protected abstract Object getSecuredObject();

    /**
     * Get the ConfigAttributeDefinition for the secured object. This will provide the
     * authorization information to the access decision manager.
     * @param securedObject Secured object for whom the config attribute definition is to
     *            be rretrieved. This may be null.
     * @return attribute definition for the provided secured object
     */
    protected abstract ConfigAttributeDefinition getConfigAttributeDefinition(Object securedObject);

    /**
     * The authentication token for the current user has changed. Update all our
     * controlled objects accordingly.
     * @param authentication now in effect, may be null
     */
    public void setAuthenticationToken(Authentication authentication) {
        boolean authorize = shouldAuthorize(authentication);
        _lastAuthentication = authentication;  // Keep for later

        // Install the decision
        for( Iterator iter = _controlledObjects.iterator(); iter.hasNext(); ) {
            WeakReference ref = (WeakReference) iter.next();

            Authorizable controlledObject = (Authorizable) ref.get();
            if( controlledObject == null ) {
                // Has been GCed, remove from our list
                iter.remove();
            } else {
                _logger.debug( "setAuthorized( " + authorize + ") on: " + controlledObject );
                controlledObject.setAuthorized( authorize );
            }
        }
    }

    /**
     * Determine if our controlled objects should be authorized based on the provided
     * authentication token.
     * @param authentication token
     * @return true if should authorize
     */
    protected boolean shouldAuthorize( Authentication authentication ) {
        Assert.state(getAccessDecisionManager() != null, "The AccessDecisionManager can not be null!");
        boolean authorize = false;
        try {
            if( authentication != null ) {
                Object securedObject = getSecuredObject();
                ConfigAttributeDefinition cad = getConfigAttributeDefinition( securedObject );
                getAccessDecisionManager().decide( authentication, getSecuredObject(), cad );
                authorize = true;
            }
        } catch( AccessDeniedException e ) {
            // This means the secured objects should not be authorized
        }
        return authorize;
    }

    /**
     * Set the access decision manager to use
     * @param accessDecisionManager
     */
    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        _accessDecisionManager = accessDecisionManager;
    }

    /**
     * Get the access decision manager in use
     */
    public AccessDecisionManager getAccessDecisionManager() {
        return _accessDecisionManager;
    }

    /**
     * Set the objects that are to be controlled. Only beans that implement the
     * {@link Authorized} interface are processed.
     * @param secured List of objects to control
     */
    public void setControlledObjects(List secured) {
        _controlledObjects = new ArrayList( secured.size() );

        // Convert to weak references and validate the object types
        for( Iterator iter = secured.iterator(); iter.hasNext(); ) {
            Object o = (Object) iter.next();

            // Ensure that we got something we can control
            if( !(o instanceof Authorizable) ) {
                throw new IllegalArgumentException( "Controlled object must implement Authorizable, got " + o.getClass() );
            }
            addAndPrepareControlledObject( (Authorizable)o );
        }
    }

    /**
     * Add an object to our controlled set.
     * @param object to control
     */
    public void addControlledObject(Authorizable object) {
        addAndPrepareControlledObject(object);
    }

    /**
     * Add a new object to the list of controlled objects.  Install our last known
     * authorization decision so newly created objects will reflect the current
     * security state.
     * @param object to add
     */
    private void addAndPrepareControlledObject( Authorizable object ) {
        _controlledObjects.add( new WeakReference( object ) );

        boolean authorize = shouldAuthorize(_lastAuthentication);

        if( _logger.isDebugEnabled() ) {
            _logger.debug( "setAuthorized( " + authorize + ") on: " + object );
        }
        object.setAuthorized( authorize );
    }

    /**
     * Remove an object from our controlled set.
     * @param object to remove
     * @return object removed or null if not found
     */
    public Object removeControlledObject(Authorizable object) {
        Object removed = null;

        for( Iterator iter = _controlledObjects.iterator(); iter.hasNext(); ) {
            WeakReference ref = (WeakReference) iter.next();

            Authorizable controlledObject = (Authorizable) ref.get();
            if( controlledObject == null ) {
                // Has been GCed, remove from our list
                iter.remove();
            } else if( controlledObject.equals( object ) ) {
                removed = controlledObject;
                iter.remove();
            }
        }
        return removed;
    }
}
