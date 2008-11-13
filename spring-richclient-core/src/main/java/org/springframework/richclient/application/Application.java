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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.application.support.MultiViewPageDescriptor;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A singleton workbench or shell of a rich client application.
 * <p>
 * The application provides a point of reference and context for an entire application. It
 * provides an interface to open application windows.
 * 
 * @author Keith Donald
 */
public class Application implements InitializingBean, ApplicationContextAware {

    private static final String DEFAULT_APPLICATION_IMAGE_KEY = "applicationInfo.image";

    private static Application SOLE_INSTANCE;

    private ApplicationContext applicationContext;

    private ApplicationDescriptor descriptor;

    private ApplicationLifecycleAdvisor lifecycleAdvisor;

    private WindowManager windowManager;

    private boolean forceShutdown = false;

    /**
     * Load the single application instance.
     * 
     * @param instance The application
     */
    public static void load( Application instance ) {
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
    	if (!ApplicationServicesLocator.isLoaded()) {
    		ApplicationServicesLocator.load(new ApplicationServicesLocator(new DefaultApplicationServices()));
    	}
        return ApplicationServicesLocator.services();
    }
    
    public Application() {
    	this(new DefaultApplicationLifecycleAdvisor());
    }

    public Application( ApplicationLifecycleAdvisor advisor ) {
        this(null, advisor);
    }

    public Application( ApplicationDescriptor descriptor, ApplicationLifecycleAdvisor advisor ) {
        setDescriptor(descriptor);
        setLifecycleAdvisor(advisor);
        this.windowManager = new WindowManager();
        this.windowManager.addObserver(new CloseApplicationObserver());
        Assert.state(!isLoaded(), "Only one instance of a Spring Rich Application allowed per VM.");
        load(this);
    }

    public void setDescriptor( ApplicationDescriptor descriptor ) {
        this.descriptor = descriptor;
    }

    public ApplicationDescriptor getDescriptor() {
        return descriptor;
    }

    private void setLifecycleAdvisor( ApplicationLifecycleAdvisor advisor ) {
        this.lifecycleAdvisor = advisor;
    }

    public void setApplicationContext( ApplicationContext context ) {
        applicationContext = context;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.lifecycleAdvisor,
                "The application advisor is required, for processing of application lifecycle events");
        getLifecycleAdvisor().setApplication(this);
        getLifecycleAdvisor().onPreInitialize(this);
    }

    public ApplicationLifecycleAdvisor getLifecycleAdvisor() {
        return lifecycleAdvisor;
    }

    public String getName() {
        if( descriptor != null && StringUtils.hasText(descriptor.getDisplayName()) )
            return descriptor.getDisplayName();

        return "Spring Rich Client Application";
    }

    public Image getImage() {
        if( descriptor != null && descriptor.getImage() != null )
            return descriptor.getImage();

        try {
        	ImageSource isrc = (ImageSource) services().getService(ImageSource.class);
        	return isrc.getImage(DEFAULT_APPLICATION_IMAGE_KEY);
        }
        catch (NoSuchImageResourceException e) {
        	return null;
        }
    }

    public void openWindow( String pageDescriptorId ) {
        ApplicationWindow newWindow = initWindow(createNewWindow());
        if ( pageDescriptorId == null ) {
        	ApplicationPageFactory pageFactory
        		= (ApplicationPageFactory)services().getService(ApplicationPageFactory.class);
        	newWindow.showPage(pageFactory.createApplicationPage(newWindow, new MultiViewPageDescriptor()));
        }
        else {
        	newWindow.showPage(pageDescriptorId);
        }
    }

    private ApplicationWindow initWindow( ApplicationWindow window ) {
        windowManager.add(window);
        return window;
    }

    protected ApplicationWindow createNewWindow() {
        ApplicationWindowFactory windowFactory = (ApplicationWindowFactory) services().getService(
                ApplicationWindowFactory.class);
        return windowFactory.createApplicationWindow();
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * ActiveWindow is tracked by windowManager. When a window gains focus, the manager
     * will receive this window as the active one.
     * 
     * @return the activeWindow.
     */
    public ApplicationWindow getActiveWindow() {
        return windowManager.getActiveWindow();
    }

    /**
     * @return true if the application is in a force shutdown mode.
     */
    public boolean isForceShutdown()
    {
        return forceShutdown;
    }

    public void close() {
        close(false, 0);
    }

    public void close(boolean force, int exitCode) {
        forceShutdown = force;
        try {
            if (windowManager.close() ) {
                forceShutdown = true;
                if( getApplicationContext() instanceof ConfigurableApplicationContext ) {
                    ((ConfigurableApplicationContext) getApplicationContext()).close();
                }
                getLifecycleAdvisor().onShutdown();
            }
        } finally {
            if (isForceShutdown()) {
                System.exit(exitCode);
            }
        }
    }

    /*
     * Closes the application once all windows have been closed.
     */
    private class CloseApplicationObserver implements Observer {

        boolean firstWindowCreated = false;

        public void update( Observable o, Object arg ) {
            int numOpenWidows = windowManager.getWindows().length;
            // make sure we only close the application after at least 1 window
            // has been added
            if( !firstWindowCreated && numOpenWidows > 0 ) {
                firstWindowCreated = true;
            } else if( firstWindowCreated && numOpenWidows == 0 ) {
                close();
            }
        }
    }

    /**
     * Starts this application.
     */
    public void start() {
        getLifecycleAdvisor().onPreStartup();
        openWindow(getLifecycleAdvisor().getStartingPageId());
        getLifecycleAdvisor().onPostStartup();
    }
}
