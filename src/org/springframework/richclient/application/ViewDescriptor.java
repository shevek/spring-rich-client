/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.application;

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.Assert;

/**
 * Metadata about a view; a view descriptor is effectively a singleton view
 * definition. A descriptor also acts as a factory which produces new instances
 * of a given view when requested, typically by a requesting application page. A
 * view descriptor can also produce a command which launches a view for display
 * on the page within the current active window.
 * 
 * @author Keith Donald
 */
public class ViewDescriptor extends LabeledObjectSupport implements
        InitializingBean {
    private Class viewClass;

    private Map viewProperties;

    /**
     * Sets the class that is the implementation of the View described by this
     * descriptor.
     * 
     * @param viewClass
     */
    public void setViewClass(Class viewClass) {
        this.viewClass = viewClass;
    }

    /**
     * Sets the map of properties to inject when new view instances 
     * are instantiated by this descriptor.
     * 
     * @param viewClass
     */
    public void setViewProperties(Map viewProperties) {
        this.viewProperties = viewProperties;
    }

    /**
     * Lookup and return the event multicaster (broadcaster) stored within the
     * application context (service registry) of this application. If no
     * multicaster, bean is defined, null is returned, and View instances
     * created by this ViewDescriptor will not be wired as ApplicationListeners.
     * 
     * @return The event multicaster
     */
    public ApplicationEventMulticaster getApplicationEventMulticaster() {
        if (getApplicationContext() != null) {
            if (getApplicationContext()
                    .containsBean(
                            AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) { return (ApplicationEventMulticaster)getApplicationContext()
                    .getBean(
                            AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME); }
        }
        return null;
    }

    public void afterPropertiesSet() {
        Assert.notNull(viewClass, "The viewClass property must be specified");
    }

    /**
     * Factory method that produces a new instance of the View described by this
     * view descriptor each time it is called. The new view instance is
     * instantiated (it must have a default constructor), and any configured
     * view properties are injected. If the view is an instance of
     * ApplicationListener, and an ApplicationEventMulticaster is configured in
     * this application's ApplicationContext, the view is registered as an
     * ApplicationListener.
     * 
     * @return The new view prototype
     */
    public View createView() {
        Object o = BeanUtils.instantiateClass(viewClass);
        Assert.isTrue((o instanceof View), "View class '" + viewClass
                + "' was instantiated, but instance is not a View!");
        View view = (View)o;
        if (view instanceof ApplicationListener
                && getApplicationEventMulticaster() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering new view instance '"
                        + view.getDisplayName()
                        + "' as an application event listener...");
            }
            getApplicationEventMulticaster().addApplicationListener(
                    (ApplicationListener)view);
        }
        if (viewProperties != null) {
            BeanWrapper wrapper = new BeanWrapperImpl(view);
            wrapper.setPropertyValues(viewProperties);
        }
        return view;
    }

    /**
     * Create a command that when executed, will attempt to show the view
     * described by this descriptor in the provided application window.
     * 
     * @param window
     *            The window
     * @return The show view command.
     */
    public ActionCommand createActionCommand(ApplicationWindow window) {
        return new ShowViewCommand(this, window);
    }

    public static class ShowViewCommand extends ActionCommand {
        private ApplicationWindow window;

        private ViewDescriptor viewDescriptor;

        public ShowViewCommand(ViewDescriptor viewDescriptor,
                ApplicationWindow window) {
            super(viewDescriptor.getDisplayName());
            setLabel(viewDescriptor.getLabel());
            setIcon(viewDescriptor.getImageIcon());
            setCaption(viewDescriptor.getCaption());
            this.viewDescriptor = viewDescriptor;
            this.window = window;
            setEnabled(true);
        }

        protected void doExecuteCommand() {
            if (this.window == null) {
                this.window = Application.locator().getActiveWindow();
            }
            this.window.showViewOnPage(viewDescriptor);
        }
    }
}