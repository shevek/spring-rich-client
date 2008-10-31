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
package org.springframework.richclient.application.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;

/**
 * @author Keith Donald
 */
public class DefaultApplicationLifecycleAdvisor extends ApplicationLifecycleAdvisor
        implements ApplicationListener {
    private String windowCommandManagerBeanName;

    private String toolBarBeanName;

    private String menuBarBeanName;

    private String windowCommandBarDefinitions;

    private ConfigurableListableBeanFactory openingWindowCommandBarFactory;

    /** Set of child command contexts created - used to bridge application events. */
    private ArrayList childContexts = new ArrayList();
    
    public void setWindowCommandBarDefinitions(String commandBarDefinitionLocation) {
        this.windowCommandBarDefinitions = commandBarDefinitionLocation;
    }

    public void setWindowCommandManagerBeanName(String commandManagerBeanName) {
        this.windowCommandManagerBeanName = commandManagerBeanName;
    }

    public void setMenubarBeanName(String menubarBeanName) {
        this.menuBarBeanName = menubarBeanName;
    }

    public void setToolbarBeanName(String toolbarBeanName) {
        this.toolBarBeanName = toolbarBeanName;
    }

    public ApplicationWindowCommandManager createWindowCommandManager() {
        initNewWindowCommandBarFactory();
        if (windowCommandManagerBeanName == null || !getCommandBarFactory().containsBean(windowCommandManagerBeanName)) {
            return new ApplicationWindowCommandManager();
        }
        return (ApplicationWindowCommandManager)getCommandBarFactory().getBean(windowCommandManagerBeanName,
                ApplicationWindowCommandManager.class);
    }

    protected void initNewWindowCommandBarFactory() {
    	if (windowCommandBarDefinitions != null) {
    		// Install our own application context so we can register needed post-processors
    		final CommandBarApplicationContext commandBarContext =
    			new CommandBarApplicationContext(windowCommandBarDefinitions);
    		addChildCommandContext(commandBarContext);
    		this.openingWindowCommandBarFactory = commandBarContext.getBeanFactory();
    	} else {
    		this.openingWindowCommandBarFactory = new DefaultListableBeanFactory();
    	}
    }

    protected ConfigurableListableBeanFactory getCommandBarFactory() {
        return openingWindowCommandBarFactory;
    }

    public CommandGroup getMenuBarCommandGroup() {
        CommandGroup menuBarCommandGroup = getCommandGroup(menuBarBeanName);
        return menuBarCommandGroup != null ? menuBarCommandGroup : super.getMenuBarCommandGroup();
    }

    public CommandGroup getToolBarCommandGroup() {
        CommandGroup toolBarCommandGroup = getCommandGroup(toolBarBeanName);
        return toolBarCommandGroup != null ? toolBarCommandGroup : super.getToolBarCommandGroup();
    }

    protected CommandGroup getCommandGroup(String name) {
        if (name == null || !getCommandBarFactory().containsBean(name)) {
            return null;
        }
        return (CommandGroup)getCommandBarFactory().getBean(name);
    }

    /**
     * We need to deliver all application events down to the child command
     * contexts that have been created.
     * @param event to deliver
     */
    public void onApplicationEvent(ApplicationEvent event) {
        // Dispatch the event to all the child command contexts
        for( Iterator iter = getChildCommandContexts().iterator(); iter.hasNext(); ) {
            ApplicationContext ctx = (ApplicationContext) iter.next();
            ctx.publishEvent(event);
        }
    }

    /**
     * Get all the child command contexts that have been created.
     * <p>
     * <em>Note, theactual collection is being returned - so be careful what you
     * do to it.</em>
     * 
     * @return list of contexts
     */
    protected List getChildCommandContexts() {
        return childContexts;
    }

    /**
     * Add a new child command context.
     * @param context
     */
    protected void addChildCommandContext( ApplicationContext context ) {
        childContexts.add( context );
    }

    /**
     * Simple extension to allow us to inject our special bean post-processors
     * and control event publishing.
     */
    private class CommandBarApplicationContext extends ClassPathXmlApplicationContext {

        /**
         * Constructor. Load bean definitions from the specified location.
         * @param location of bean definitions
         */
        public CommandBarApplicationContext(String location) {
            super( new String[] { location }, false, Application.instance().getApplicationContext() );
            refresh();
        }

        /**
         * Install our bean post-processors.
         * @param beanFactory the bean factory used by the application context
         * @throws org.springframework.beans.BeansException in case of errors
         */
        protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.addBeanPostProcessor( new ApplicationWindowSetter( getOpeningWindow() ) );
        }

        /**
         * Publish an event in to this context.  Since we are always getting
         * notification from a parent context, this overriden implementation does
         * not dispatch up to the parent context, thus avoiding an infinite loop.
         */
        public void publishEvent(ApplicationEvent event) {
            // Temporarily disconnect our parent so the event publishing doesn't
            // result in an infinite loop.
            ApplicationContext parent = getParent();
            setParent(null);
            super.publishEvent(event);
            setParent(parent);
        }
    }
}