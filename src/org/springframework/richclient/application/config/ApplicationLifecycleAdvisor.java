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

import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 * @author Jim Moore
 */
public abstract class ApplicationLifecycleAdvisor implements InitializingBean {
    private static final String EXCEPTION_HANDLER_KEY = "sun.awt.exception.handler";

    private Application application;

    private ApplicationWindow openingWindow;

    private String startingPageId;

    private boolean introShown;

    private Class eventExceptionHandler;

    /**
     * This is used to allow the ViewDescriptor to be lazily created when the
     * ApplicationWindow is opened. Useful when the ApplicationAdvisor needs to
     * do things before ViewDescriptor should be created, such as setting up a
     * security context.
     * 
     * @param startingViewDescriptorBeanName the name of the bean to create
     * 
     * @see #getStartingViewDescriptor()
     */
    public void setStartingPageId(String pageDescriptorId) {
        this.startingPageId = pageDescriptorId;
    }

    /**
     * Sets the class to use to handle exceptions that happen in the
     * {@link java.awt.EventDispatchThread}. The class must have a no-arg
     * public constructor and a method "public void handle(Throwable)".
     * 
     * @param eventExceptionHandler the class to use
     * 
     * @see java.awt.EventDispatchThread#handleException(Throwable)
     */
    public void setEventExceptionHandler(Class eventExceptionHandler) {
        this.eventExceptionHandler = eventExceptionHandler;
    }

    public void afterPropertiesSet() throws Exception {
        final Properties systemProperties = System.getProperties();
        if (systemProperties.get(EXCEPTION_HANDLER_KEY) == null) {
            systemProperties.put(EXCEPTION_HANDLER_KEY, getEventExceptionHandler().getName());
        }
        Assert.state(startingPageId != null,
                "startingPageId must be set: it must point to a page descriptor, or a view descriptor for a single view per page");
    }

    public String getStartingPageId() {
        return startingPageId;
    }

    protected Application getApplication() {
        return application;
    }

    protected ApplicationServices getApplicationServices() {
        return getApplication().getServices();
    }

    public void onPreInitialize(Application application) {
        this.application = application;
    }

    public void onPreStartup() {

    }

    public void onPostStartup() {

    }

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        this.openingWindow = configurer.getWindow();
        configurer.setTitle(getApplication().getName());
        configurer.setImage(getApplication().getImage());
    }

    protected final ApplicationWindow getOpeningWindow() {
        return openingWindow;
    }

    public ApplicationWindowCommandManager createWindowCommandManager() {
        return new ApplicationWindowCommandManager();
    }

    public CommandGroup getMenuBarCommandGroup() {
        return new CommandGroup();
    }

    public CommandGroup getToolBarCommandGroup() {
        return new CommandGroup();
    }

    public StatusBarCommandGroup getStatusBarCommandGroup() {
        return new StatusBarCommandGroup();
    }

    public void onCommandsCreated(ApplicationWindow window) {

    }

    public void onWindowCreated(ApplicationWindow window) {

    }

    public void showIntroComponentIfNecessary(ApplicationWindow window) {
        if (introShown) {
            showIntro(window);
            introShown = true;
        }
    }

    protected void showIntro(ApplicationWindow window) {

    }

    public void onWindowOpened(ApplicationWindow window) {

    }

    public boolean onPreWindowClose(ApplicationWindow window) {
        return true;
    }

    /**
     * Logs any event loop exception not caught.
     * 
     * @see ApplicationLifecycleAdvisor#setEventExceptionHandler(Class)
     */
    public static class DefaultEventExceptionHandler {
        public void handle(Throwable t) {
            LogFactory.getLog(ApplicationLifecycleAdvisor.class).error(t.getMessage(), t);
        }
    }

    public Class getEventExceptionHandler() {
        if (this.eventExceptionHandler == null) {
            this.eventExceptionHandler = DefaultEventExceptionHandler.class;
        }
        return this.eventExceptionHandler;
    }

}