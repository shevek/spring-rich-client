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

import java.awt.Image;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationInfo;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.command.support.DefaultCommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;

/**
 * @author Keith Donald
 * @author Jim Moore
 */
public abstract class ApplicationAdvisor implements InitializingBean {
    private static final String DEFAULT_APPLICATION_IMAGE_KEY = "applicationInfo.image";

    private static final String EXCEPTION_HANDLER_KEY = "sun.awt.exception.handler";

    private Application application;

    private ApplicationInfo applicationInfo;

    private ApplicationWindow managedWindow;

    private boolean introShown;

    private Class eventExceptionHandler;

    /**
     * Logs any event loop exception not caught.
     * 
     * @see ApplicationAdvisor#setEventExceptionHandler(Class)
     */
    public static class DefaultEventExceptionHandler {
        public void handle(Throwable t) {
            LogFactory.getLog(ApplicationAdvisor.class)
                    .error(t.getMessage(), t);
        }
    }

    public Class getEventExceptionHandler() {
        if (this.eventExceptionHandler == null) {
            this.eventExceptionHandler = DefaultEventExceptionHandler.class;
        }
        return this.eventExceptionHandler;
    }

    /**
     * Sets the class to use to handle exceptions that happen in the
     * {@link java.awt.EventDispatchThread}. The class must have a no-arg
     * public constructor and a method "public void handle(Throwable)".
     * 
     * @param eventExceptionHandler
     *            the class to use
     * 
     * @see java.awt.EventDispatchThread#handleException(Throwable)
     */
    public void setEventExceptionHandler(Class eventExceptionHandler) {
        this.eventExceptionHandler = eventExceptionHandler;
    }

    public void afterPropertiesSet() throws Exception {
        final Properties systemProperties = System.getProperties();
        if (systemProperties.get(EXCEPTION_HANDLER_KEY) == null) {
            systemProperties.put(EXCEPTION_HANDLER_KEY,
                    getEventExceptionHandler().getName());
        }
    }

    public void setApplicationInfo(ApplicationInfo info) {
        this.applicationInfo = info;
    }

    protected final Application getApplication() {
        return application;
    }

    public String getApplicationName() {
        if (applicationInfo != null) {
            return applicationInfo.getDisplayName();
        }
        else {
            return "Spring Rich Client Application";
        }
    }

    public Image getApplicationImage() {
        if (applicationInfo != null) {
            return applicationInfo.getImage();
        }
        else {
            return Application.services().getImage(
                    DEFAULT_APPLICATION_IMAGE_KEY);
        }
    }

    public void onPreInitialize(Application application) {
        this.application = application;
    }

    public void onPreStartup() {

    }

    public void onPostStartup() {

    }

    /**
     * Returns the {@link ViewDescriptor}that is the default view when the
     * application is started.
     */
    public abstract ViewDescriptor getStartingViewDescriptor();

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        this.managedWindow = configurer.getWindow();
        configurer.setTitle(getApplicationName());
        configurer.setImage(getApplicationImage());
    }

    protected final ApplicationWindow getManagedWindow() {
        return managedWindow;
    }

    public CommandManager createWindowCommandManager() {
        return new DefaultCommandManager();
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

}