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
import org.springframework.richclient.application.ApplicationInfo;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;

/**
 * @author Keith Donald
 */
public class ConfigurableApplicationLifecycle extends ApplicationLifecycle {
    private static final String COMMAND_MANAGER_BEAN_NAME = "commandManager";

    private static final String TOOL_BAR_BEAN_NAME = "toolBar";

    private static final String MENU_BAR_BEAN_NAME = "menuBar";

    private ApplicationInfo applicationInfo;

    private String startingPageId;

    private Resource commandFactoryResource;

    private XmlBeanFactory currentWindowCommands;

    public ConfigurableApplicationLifecycle() {

    }

    public void setApplicationInfo(ApplicationInfo info) {
        this.applicationInfo = info;
    }

    public void setCommandFactoryResource(Resource resource) {
        this.commandFactoryResource = resource;
    }

    public void onPreInitialize(Application application) {
        super.onPreInitialize(application);
        application.setApplicationInfo(applicationInfo);
    }

    public String getStartingPageId() {
        return startingPageId;
    }

    public void setStartingPageId(String pageId) {
        this.startingPageId = pageId;
    }

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        super.onPreWindowOpen(configurer);
        configurer.setTitle(applicationInfo.getDisplayName());
        configurer.setImage(applicationInfo.getImage());
    }

    public CommandManager getCommandManager() {
        this.currentWindowCommands = new XmlBeanFactory(commandFactoryResource,
                Application.locator().getApplicationContext());
        this.currentWindowCommands
                .addBeanPostProcessor(new ApplicationWindowSetter(
                        getManagedWindow()));
        this.currentWindowCommands.addBeanPostProcessor(newObjectConfigurer());
        registerBeanPostProcessors();
        return (CommandManager)currentWindowCommands
                .getBean(COMMAND_MANAGER_BEAN_NAME);
    }

    protected BeanPostProcessor newObjectConfigurer() {
        return new BeanPostProcessor() {
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return Application.locator().configure(bean, beanName);
            }
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }

        };
    }
    private void registerBeanPostProcessors() throws BeansException {
        String[] beanNames = getBeanFactory().getBeanDefinitionNames(
                BeanPostProcessor.class);
        if (beanNames.length > 0) {
            List beanProcessors = new ArrayList();
            for (int i = 0; i < beanNames.length; i++) {
                beanProcessors.add(getBeanFactory().getBean(beanNames[i]));
            }
            Collections.sort(beanProcessors, new OrderComparator());
            for (Iterator it = beanProcessors.iterator(); it.hasNext();) {
                getBeanFactory().addBeanPostProcessor(
                        (BeanPostProcessor)it.next());
            }
        }
    }

    public CommandGroup getMenuBarCommandGroup() {
        return (CommandGroup)currentWindowCommands.getBean(MENU_BAR_BEAN_NAME);
    }

    public CommandGroup getToolBarCommandGroup() {
        return (CommandGroup)currentWindowCommands.getBean(TOOL_BAR_BEAN_NAME);
    }

    protected ConfigurableListableBeanFactory getBeanFactory() {
        return currentWindowCommands;
    }

}