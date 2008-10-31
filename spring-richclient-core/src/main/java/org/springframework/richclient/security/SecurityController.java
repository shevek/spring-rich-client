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
package org.springframework.richclient.security;

import java.util.List;

import org.springframework.richclient.core.Authorizable;
import org.springframework.security.AccessDecisionManager;

/**
 * A security controller is responsible for authorizing other {@link Authorizable}
 * objects. Based on the current authentication and the configured access decision rules,
 * the controlled objects will have their {@link Authorizable#setAuthorized(boolean)}
 * method called accordingly.
 * <p>
 * The access decision manager is responsible for making the decision to authorize the
 * controlled objects.
 * 
 * @author Larry Streepy
 * 
 */
public interface SecurityController extends AuthenticationAware {

    /**
     * Set the access decision manager to use
     * @param accessDecisionManager
     */
    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager);

    /**
     * Get the access decision manager in use
     * @return decision manager
     */
    public AccessDecisionManager getAccessDecisionManager();

    /**
     * Set the objects that are to be controlled. Only beans that implement the
     * {@link Authorized} interface are processed.
     * @param secured List of objects to control
     */
    public void setControlledObjects(List secured);

    /**
     * Add an object to our controlled set.
     * @param object to control
     */
    public void addControlledObject(Authorizable object);

    /**
     * Remove an object from our controlled set.
     * @param object to remove
     * @return object removed or null if not found
     */
    public Object removeControlledObject(Authorizable object);
}
