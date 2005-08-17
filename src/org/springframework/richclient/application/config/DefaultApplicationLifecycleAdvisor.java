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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;

/**
 * @author Keith Donald
 */
public class DefaultApplicationLifecycleAdvisor extends ApplicationLifecycleAdvisor
        implements ApplicationListener {
    private String windowCommandManagerBeanName = "windowCommandManager";

    private String toolBarBeanName = "toolBar";

    private String menuBarBeanName = "menuBar";

    private Resource windowCommandBarDefinitions;

    private XmlBeanFactory openingWindowCommandBarFactory;

    private ApplicationEventMulticaster eventMulticaster;

    public void setWindowCommandBarDefinitions(Resource commandBarDefinitionLocation) {
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

    public void setEventMulticaster(ApplicationEventMulticaster eventMulticaster) {
        this.eventMulticaster = eventMulticaster;
    }

    public ApplicationWindowCommandManager createWindowCommandManager() {
        initNewWindowCommandBarFactory();
        return (ApplicationWindowCommandManager)getCommandBarFactory().getBean(windowCommandManagerBeanName,
                ApplicationWindowCommandManager.class);
    }

    protected void initNewWindowCommandBarFactory() {
        this.openingWindowCommandBarFactory = new XmlBeanFactory(windowCommandBarDefinitions, Application.services()
                .getBeanFactory());
        this.openingWindowCommandBarFactory.addBeanPostProcessor(new ApplicationWindowSetter(getOpeningWindow()));
        this.openingWindowCommandBarFactory.addBeanPostProcessor(newObjectConfigurer());
        registerBeanPostProcessors();
        installApplicationEventBridge();
    }

    protected ConfigurableListableBeanFactory getCommandBarFactory() {
        return openingWindowCommandBarFactory;
    }

    protected BeanPostProcessor newObjectConfigurer() {
        return new BeanPostProcessor() {
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return Application.services().configure(bean, beanName);
            }

            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        };
    }

    private void registerBeanPostProcessors() throws BeansException {
        String[] beanNames = getCommandBarFactory().getBeanDefinitionNames(BeanPostProcessor.class);
        if (beanNames.length > 0) {
            List beanProcessors = new ArrayList();
            for (int i = 0; i < beanNames.length; i++) {
                beanProcessors.add(getCommandBarFactory().getBean(beanNames[i]));
            }
            Collections.sort(beanProcessors, new OrderComparator());
            for (Iterator it = beanProcessors.iterator(); it.hasNext();) {
                getCommandBarFactory().addBeanPostProcessor((BeanPostProcessor)it.next());
            }
        }
    }

    // Establish a bridge for ApplicationEvents between
    // the main ApplicationContext and command's BeanFactory
    // for participating in the global notification mechanism
    private void installApplicationEventBridge() {
        ConfigurableListableBeanFactory factory = getCommandBarFactory();

        Map beans = factory.getBeansOfType(ApplicationListener.class,true,false);
        for (Iterator iterator = beans.values().iterator(); iterator.hasNext();) {
            ApplicationListener applicationListener =
                    (ApplicationListener) iterator.next();
            eventMulticaster.addApplicationListener(applicationListener);
        }
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

    /** {@inheritDoc} */
    public void onApplicationEvent(ApplicationEvent event) {
        // Dispatch to child listeners.
        eventMulticaster.multicastEvent(event);
    }

    // initialize event multicaster if not set.
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (eventMulticaster == null) {
            eventMulticaster = new SimpleApplicationEventMulticaster();
        }
    }
}