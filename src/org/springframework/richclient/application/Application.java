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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationWindow;
import org.springframework.util.Assert;

/**
 * A singleton workbench or shell of a rich client application.
 * <p>
 * The application provides a point of reference and context for an entire
 * application. It provides an interface to open application windows.
 * 
 * @author Keith Donald
 */
public class Application implements InitializingBean, ApplicationContextAware {
    private static final String APPLICATION_WINDOW_BEAN_ID = "applicationWindowPrototype";

    private static Application SOLE_INSTANCE;

    private ApplicationContext applicationContext;

    private ApplicationServices applicationServices;

    private ApplicationLifecycleAdvisor applicationAdvisor;

    private ApplicationWindow activeWindow;

    private WindowManager windowManager;

    /**
     * Load the single application instance.
     * 
     * @param instance
     *            The application
     */
    public static void load(Application instance) {
        SOLE_INSTANCE = instance;
    }

    /**
     * Return the single application instance.
     * 
     * @return The application
     */
    public static Application instance() {
        Assert
                .state(isLoaded(),
                        "The global rich client application instance has not yet been initialized; it must be created and loaded first.");
        return SOLE_INSTANCE;
    }

    public static boolean isLoaded() {
        return SOLE_INSTANCE != null;
    }

    /**
     * Return a global service locator for application services.
     * 
     * @return The application services locator.
     */
    public static ApplicationServices services() {
        return instance().getServices();
    }

    public Application(ApplicationLifecycleAdvisor advisor) {
        this(advisor, null);
    }

    public Application(ApplicationLifecycleAdvisor advisor, ApplicationServices services) {
        setAdvisor(advisor);
        setServices(services);
        windowManager = new WindowManager();
        windowManager.addObserver(new CloseApplicationObserver());
        Assert.state(!isLoaded(), "Only one instance of a Spring Rich Application allowed per VM.");
        load(this);
    }

    public void setServices(ApplicationServices services) {
        this.applicationServices = services;
    }

    private void setAdvisor(ApplicationLifecycleAdvisor advisor) {
        this.applicationAdvisor = advisor;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.applicationAdvisor,
                "The application advisor is required, for processing of application lifecycle events");
        getAdvisor().onPreInitialize(this);
        getServices();
    }

    public ApplicationLifecycleAdvisor getAdvisor() {
        return applicationAdvisor;
    }

    public ApplicationServices getServices() {
        if (applicationServices == null) {
            applicationServices = new ApplicationServices();
            applicationServices.setApplicationContext(applicationContext);
        }
        return applicationServices;
    }

    public String getName() {
        return getAdvisor().getApplicationName();
    }

    public Image getImage() {
        return getAdvisor().getApplicationImage();
    }

    public void openWindow(String pageDescriptorId) {
        ApplicationWindow newWindow = initWindow(createNewWindow());
        newWindow.showPage(pageDescriptorId);
        // @TODO track active window...
        this.activeWindow = newWindow;
    }

    private ApplicationWindow initWindow(ApplicationWindow window) {
        windowManager.add(window);
        return window;
    }

    protected ApplicationWindow createNewWindow() {
        try {
            return (ApplicationWindow)services().getBean(APPLICATION_WINDOW_BEAN_ID, ApplicationWindow.class);
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
        if (windowManager.close()) {
            System.exit(0);
        }
    }

    /*
     * Closes the application once all windows have been closed.
     */
    private class CloseApplicationObserver implements Observer {
        boolean firstWindowCreated = false;

        public void update(Observable o, Object arg) {
            int numOpenWidows = windowManager.getWindows().length;
            // make sure we only close the application after at least 1 window
            // has been added
            if (!firstWindowCreated && numOpenWidows > 0) {
                firstWindowCreated = true;
            }
            else if (firstWindowCreated && numOpenWidows == 0) {
                close();
            }
        }
    }
}