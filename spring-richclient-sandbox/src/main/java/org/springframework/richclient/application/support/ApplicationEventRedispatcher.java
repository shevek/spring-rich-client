/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.application.support;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * This class is responsible for re-dispatching application events to a collection of
 * additional registered listeners. This is needed due to the way that the application
 * context handles the ApplicationListener tag interface. For any beans that are
 * prototypes, one instance will be registered as an ApplicationListener and all the
 * others will not. Further, there is no mechanism to get a reference to the context's
 * event dispatcher to add listeners at runtime.
 * <p>
 * By defining a bean with this type in the context, it will be added as a typical event
 * listener. Then, this dispatcher can be set on any other bean (including prototypes)
 * that need to be added to the event mechanism. In order to wire it all together, any
 * object that wants to register for events simply defines a property of type
 * ApplicationEventDispatcher and in the property setter method, call the
 * {@link #addListener(ApplicationListener)} method on itself (or a delegate handler).
 * Like this:
 * 
 * <pre>
 * public void setApplicationEventRedispatcher( ApplicationEventRedispatcher dispatcher ) {
 *     dispatcher.addListener(this);
 * }
 * </pre>
 * 
 * @author Larry Streepy
 * 
 */
public class ApplicationEventRedispatcher implements ApplicationListener {

    /** Our list of delegates to dispatch events to. */
    private ArrayList delegates = new ArrayList();

    /**
     * Handle an application event, dispatch it to all our delegates.
     * 
     * @param event to dispatch
     */
    public void onApplicationEvent( ApplicationEvent event ) {
        Iterator iter = delegates.iterator();
        while( iter.hasNext() ) {
            ((ApplicationListener) iter.next()).onApplicationEvent(event);
        }
    }

    /**
     * Add a listener to our set.
     * 
     * @param l Listener to add
     */
    public void addListener( ApplicationListener l ) {
        delegates.add(l);
    }

    /**
     * Remove a listener from our set.
     * 
     * @param l Listener to remove
     */
    public void removeListener( ApplicationListener l ) {
        delegates.remove(l);
    }
}
