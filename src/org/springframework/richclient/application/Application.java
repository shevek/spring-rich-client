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
import org.springframework.util.StringUtils;

/**
 * A singleton workbench or shell of a rich client application.
 * <p>
 * The application provides a point of reference and context for an entire
 * application. It provides an interface to open application windows.
 * 
 * @author Keith Donald
 */
public class Application implements InitializingBean, ApplicationContextAware {
    private static final String DEFAULT_APPLICATION_IMAGE_KEY = "applicationInfo.image";

    private static final String APPLICATION_WINDOW_BEAN_ID = "applicationWindowPrototype";

    private static Application SOLE_INSTANCE;

    private ApplicationDescriptor descriptor;

    private ApplicationServices services;

    private ApplicationLifecycleAdvisor lifecycleAdvisor;

    private ApplicationContext context;

    private ApplicationWindow activeWindow;

    private WindowManager windowManager;

    /**
     * Load the single application instance.
     * 
     * @param instance The application
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
        this(null, advisor, null);
    }

    public Application(ApplicationDescriptor descriptor, ApplicationLifecycleAdvisor advisor) {
        this(descriptor, advisor, null);
    }

    public Application(ApplicationDescriptor descriptor, ApplicationLifecycleAdvisor advisor,
            ApplicationServices services) {
        setDescriptor(descriptor);
        setLifecycleAdvisor(advisor);
        setServices(services);
        this.windowManager = new WindowManager();
        this.windowManager.addObserver(new CloseApplicationObserver());
        Assert.state(!isLoaded(), "Only one instance of a Spring Rich Application allowed per VM.");
        load(this);
    }

    public void setDescriptor(ApplicationDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void setServices(ApplicationServices services) {
        this.services = services;
    }

    private void setLifecycleAdvisor(ApplicationLifecycleAdvisor advisor) {
        this.lifecycleAdvisor = advisor;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.lifecycleAdvisor,
                "The application advisor is required, for processing of application lifecycle events");
        getLifecycleAdvisor().onPreInitialize(this);
        getServices();
    }

    public ApplicationLifecycleAdvisor getLifecycleAdvisor() {
        return lifecycleAdvisor;
    }

    public ApplicationServices getServices() {
        if (services == null) {
            services = new ApplicationServices();
            services.setApplicationContext(context);
        }
        return services;
    }

    public String getName() {
        if (descriptor != null && StringUtils.hasText(descriptor.getDisplayName())) {
            return descriptor.getDisplayName();
        }
        else {
            return "Spring Rich Client Application";
        }
    }

    public Image getImage() {
        if (descriptor != null && descriptor.getImage() != null) {
            return descriptor.getImage();
        }
        else {
            return Application.services().getImage(DEFAULT_APPLICATION_IMAGE_KEY);
        }
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