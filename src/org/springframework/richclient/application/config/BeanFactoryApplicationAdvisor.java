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
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;

/**
 * @author Keith Donald
 */
public class BeanFactoryApplicationAdvisor extends ApplicationAdvisor implements BeanFactoryAware {
    private String commandManagerBeanName = "commandManager";

    private String toolBarBeanName = "toolBar";

    private String menuBarBeanName = "menuBar";

    private String startingViewDescriptorBeanName;

    private ViewDescriptor startingViewDescriptor;

    private Resource commandFactoryResource;

    private XmlBeanFactory currentWindowCommands;

    private BeanFactory beanFactory;

    public void setCommandFactoryResource(Resource resource) {
        this.commandFactoryResource = resource;
    }

    public ViewDescriptor getStartingViewDescriptor() {
        if (this.startingViewDescriptor == null) {
            this.startingViewDescriptor =
                    (ViewDescriptor)this.beanFactory.getBean(startingViewDescriptorBeanName,
                            ViewDescriptor.class);
        }
        return this.startingViewDescriptor;
    }

    public void setStartingViewDescriptor(
            ViewDescriptor startingViewDescriptor) {
        this.startingViewDescriptor = startingViewDescriptor;
    }

    /**
     * This is used to allow the ViewDescriptor to be lazily created
     * when the ApplicationWindow is opened.  Useful when the
     * ApplicationAdvisor needs to do things before ViewDescriptor should be
     * created, such as setting up a security context.
     *
     * @param startingViewDescriptorBeanName the name of the bean to create
     *
     * @see #getStartingViewDescriptor()
     */
    public void setStartingViewDescriptorBeanName(
            String startingViewDescriptorBeanName) {
        this.startingViewDescriptorBeanName = startingViewDescriptorBeanName;
    }

    public void setBeanFactory(BeanFactory beanFactory)
            throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (this.startingViewDescriptor == null) {
            if (this.startingViewDescriptorBeanName == null) {
                throw new FatalBeanException("Either startingViewDescriptor " +
                        "or startingViewDescriptorBeanName must be set.");
            }
            else {
                final BeanFactory beanFactory = this.beanFactory;
                if (!beanFactory.containsBean(this.startingViewDescriptorBeanName)) {
                    throw new FatalBeanException("Do not know about the " +
                            "bean definition for '" +
                            this.startingViewDescriptorBeanName+"'");
                }
            }
        }
    }

    public void setCommandManagerBeanName(String commandManagerBeanName) {
        this.commandManagerBeanName = commandManagerBeanName;
    }

    public void setCurrentWindowCommands(XmlBeanFactory currentWindowCommands) {
        this.currentWindowCommands = currentWindowCommands;
    }

    public void setMenubarBeanName(String menubarBeanName) {
        this.menuBarBeanName = menubarBeanName;
    }

    public void setToolbarBeanName(String toolbarBeanName) {
        this.toolBarBeanName = toolbarBeanName;
    }

    public CommandManager createWindowCommandManager() {
        this.currentWindowCommands = new XmlBeanFactory(commandFactoryResource,
                Application.instance().getApplicationContext());
        this.currentWindowCommands
                .addBeanPostProcessor(new ApplicationWindowSetter(
                        getManagedWindow()));
        this.currentWindowCommands.addBeanPostProcessor(newObjectConfigurer());
        registerBeanPostProcessors();
        return (CommandManager)currentWindowCommands
                .getBean(commandManagerBeanName);
    }

    protected BeanPostProcessor newObjectConfigurer() {
        return new BeanPostProcessor() {
            public Object postProcessBeforeInitialization(Object bean,
                    String beanName) {
                return Application.services().configure(bean, beanName);
            }

            public Object postProcessAfterInitialization(Object bean,
                    String beanName) {
                return bean;
            }
        };
    }

    private void registerBeanPostProcessors() throws BeansException {
        String[] beanNames = getCommandsBeanFactory().getBeanDefinitionNames(
            BeanPostProcessor.class);
        if (beanNames.length > 0) {
            List beanProcessors = new ArrayList();
            for (int i = 0; i < beanNames.length; i++) {
                beanProcessors.add(getCommandsBeanFactory().getBean(beanNames[i]));
            }
            Collections.sort(beanProcessors, new OrderComparator());
            for (Iterator it = beanProcessors.iterator(); it.hasNext();) {
                getCommandsBeanFactory().addBeanPostProcessor(
                    (BeanPostProcessor)it.next());
            }
        }
    }

    public void onPreInitialize(Application application) {
        super.onPreInitialize(application);
    }

    public CommandGroup getMenuBarCommandGroup() {
        if (menuBarBeanName == null
                || !currentWindowCommands.containsBean(menuBarBeanName)) { return super
                .getMenuBarCommandGroup(); }
        return (CommandGroup)currentWindowCommands.getBean(menuBarBeanName);
    }

    public CommandGroup getToolBarCommandGroup() {
        if (toolBarBeanName == null
                || !currentWindowCommands.containsBean(toolBarBeanName)) { return super
                .getToolBarCommandGroup(); }
        return (CommandGroup)currentWindowCommands.getBean(toolBarBeanName);
    }

    protected ConfigurableListableBeanFactory getCommandsBeanFactory() {
        return currentWindowCommands;
    }
}