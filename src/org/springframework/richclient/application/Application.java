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

import java.awt.Image;
import java.util.Observable;
import java.util.Observer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.richclient.application.config.ApplicationAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationWindow;
import org.springframework.util.Assert;

/**
 * A singleton workbench or shell of a rich client application.
 * 
 * The application provides a point of reference and context for an entire
 * application. It provides an interface to open application windows.
 * 
 * @author Keith Donald
 */
public class Application extends ApplicationObjectSupport {
    private static final String APPLICATION_WINDOW = "applicationWindow";

    private static Application INSTANCE;

    private ApplicationServices applicationServices;

    private ApplicationAdvisor applicationAdvisor;

    private ApplicationWindow activeWindow;

    private WindowManager windowManager = new WindowManager();

    public Application(ApplicationAdvisor advisor) {
        this(null, advisor);
    }

    public Application(ApplicationServices applicationServices,
            ApplicationAdvisor advisor) {
        setApplicationServices(applicationServices);
        setApplicationAdvisor(advisor);
        Assert
                .isTrue(INSTANCE == null,
                        "Only one instance of a Spring Rich Application allowed per VM.");
        load(this);
    }

    public ApplicationServices getApplicationServices() {
        return applicationServices;
    }

    private void setApplicationServices(ApplicationServices services) {
        this.applicationServices = services;
    }

    public ApplicationAdvisor getApplicationAdvisor() {
        return applicationAdvisor;
    }

    private void setApplicationAdvisor(ApplicationAdvisor advisor) {
        Assert.notNull(advisor, "The application advisor is required");
        this.applicationAdvisor = advisor;
    }

    public String getName() {
        return getApplicationAdvisor().getApplicationName();
    }

    public Image getImage() {
        return getApplicationAdvisor().getApplicationImage();
    }

    /**
     * Load the single application instance.
     * 
     * @param instance
     *            The application
     */
    public static void load(Application instance) {
        INSTANCE = instance;
    }

    /**
     * Return the single application instance.
     * 
     * @return The application
     */
    public static Application instance() {
        Assert
                .notNull(INSTANCE,
                        "The global rich client application instance has not yet been initialized.");
        return INSTANCE;
    }

    /**
     * Return a global service locator for application services.
     * 
     * @return The application services locator.
     */
    public static ApplicationServices services() {
        return instance().getApplicationServices();
    }

    protected void initApplicationContext() throws BeansException {
        getApplicationAdvisor().onPreInitialize(this);
        initApplicationServices();
    }

    protected void initApplicationServices() {
        if (this.applicationServices == null) {
            this.applicationServices = new ApplicationServices();
            this.applicationServices
                    .setApplicationContext(getApplicationContext());
        }
    }

    void openFirstApplicationWindow() {
        openWindow(getApplicationAdvisor().getStartingPageId());
    }

    public void openWindow(String pageDescriptorId) {
        ApplicationWindow newWindow = initWindow(createNewWindow());
        newWindow.showPage(pageDescriptorId);
        // @TODO track active window...
        this.activeWindow = newWindow;
    }

    private ApplicationWindow initWindow(ApplicationWindow window) {
        windowManager.add(window);
        windowManager.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (windowManager.getWindows().length == 0) {
                    close();
                }
            }
        });
        return window;
    }

    protected ApplicationWindow createNewWindow() {
        try {
            return (ApplicationWindow)getApplicationContext().getBean(
                    APPLICATION_WINDOW);
        }
        catch (NoSuchBeanDefinitionException e) {
            return new DefaultApplicationWindow();
        }
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public ApplicationWindow getActiveWindow() {
        return activeWindow;
    }

    public void close() {
        if (windowManager.close())
            System.exit(0);
    }

}