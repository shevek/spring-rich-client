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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;

/**
 * @author Keith Donald
 */
public class DefaultApplicationLifecycleAdvisor extends ApplicationLifecycleAdvisor {
    private String windowCommandManagerBeanName = "windowCommandManager";

    private String toolBarBeanName = "toolBar";

    private String menuBarBeanName = "menuBar";

    private Resource windowCommandBarDefinitions;

    private XmlBeanFactory openingWindowCommandBarFactory;

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

    public CommandGroup getMenuBarCommandGroup() {
        if (menuBarBeanName == null || !getCommandBarFactory().containsBean(menuBarBeanName)) {
            return super.getMenuBarCommandGroup();
        }
        return (CommandGroup)getCommandBarFactory().getBean(menuBarBeanName);
    }

    public CommandGroup getToolBarCommandGroup() {
        if (toolBarBeanName == null || !getCommandBarFactory().containsBean(toolBarBeanName)) {
            return super.getToolBarCommandGroup();
        }
        return (CommandGroup)getCommandBarFactory().getBean(toolBarBeanName);
    }

}