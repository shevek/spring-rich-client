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
package org.springframework.richclient.security.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.security.SecurityController;
import org.springframework.richclient.security.SecurityControllerManager;

/**
 * Default implementation of the SecurityControllerManager. The controller map can be set
 * during bean initialization. Also, if the map is not set, or does not contain an entry
 * for a requested security controller Id, then an attempt to retrieve a bean instance
 * with the given id will be made. Thus, no map entries are required for security
 * controllers that are referenced by their application context bean id.
 * <p>
 * This implementation also provides for a <em>fallback</em> security controller. The
 * fallback controller is registered with the
 * {@link #setFallbackSecurityController(SecurityController)} method. Once registered, any
 * call to {@link #getSecurityController(String)} that would have normally resulted in not
 * finding a controller will instead return the fallback controller.
 * <p>
 * Here's an example configuration:
 * 
 * <pre>
 *   &lt;bean id=&quot;securityControllerManager&quot; class=&quot;org.springframework.richclient.security.support.DefaultSecurityControllerManager&quot;&gt;
 *       &lt;property name=&quot;fallbackSecurityController&quot; ref=&quot;writeController&quot; /&gt;
 *       &lt;property name=&quot;securityControllerMap&quot;&gt;
 *           &lt;map&gt;
 *               &lt;entry key=&quot;contact.newContactCommand&quot; value-ref=&quot;adminController&quot;/&gt;
 *               &lt;entry key=&quot;contact.deleteContactCommand&quot; value-ref=&quot;adminController&quot;/&gt;
 *           &lt;/map&gt;
 *       &lt;/property&gt;
 *   &lt;/bean&gt;
 * </pre>
 * 
 * @author Larry Streepy
 * 
 */
public class DefaultSecurityControllerManager implements SecurityControllerManager {

    private Map securityControllerMap = new HashMap();
    private SecurityController fallbackController = null;

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.security.SecurityControllerManager#setSecurityControllerMap(java.util.Map)
     */
    public void setSecurityControllerMap(Map map) {
        for( Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            registerSecurityControllerAlias( (String) entry.getKey(), (SecurityController) entry.getValue() );
        }
    }

    /**
     * Get the security controller for the given id. If the id is registered in our map,
     * then return the registered controller. If not, then try to obtain a bean with the
     * given id if it implements the SecurityController interface.
     * @param id of controller to retrieve
     * @return controller, null if not found
     */
    public SecurityController getSecurityController(String id) {
        SecurityController sc = (SecurityController) securityControllerMap.get( id );
        if( sc == null ) {
            // Try for a named bean
            try {
                sc = (SecurityController) Application.instance().getApplicationContext().getBean( id,
                    SecurityController.class );
            } catch( NoSuchBeanDefinitionException e ) {
                // Try for a fallback
                sc = getFallbackSecurityController();
            }
        }
        return sc;
    }

    /**
     * Register an alias for a SecurityController.
     * @param aliasId to register
     * @param securityController to register under given alias Id
     */
    public void registerSecurityControllerAlias(String aliasId, SecurityController securityController) {
        securityControllerMap.put( aliasId, securityController );
    }

    /**
     * Set the fallback security controller. This controller will be returned for any
     * requested controller Id that is not found in the registry.
     * @param fallbackController
     */
    public void setFallbackSecurityController(SecurityController fallbackController) {
        this.fallbackController = fallbackController;
    }

    /**
     * Get the fallback security controller, if any.
     * @return fallback security controller, null if not defined
     */
    public SecurityController getFallbackSecurityController() {
        return fallbackController;
    }
}
