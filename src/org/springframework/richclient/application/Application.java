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
import org.springframework.context.support.ApplicationObjectSupport;

import org.springframework.richclient.application.config.ApplicationAdvisor;
import org.springframework.util.Assert;

/**
 * A singleton workbench or shell of a rich client application.
 * 
 * The application provides a point of reference and context for an entire
 * application. It provides an interface to open application windows.
 * 
 * @author Keith Donald
 */
public class Application extends ApplicationObjectSupport  {

    private static Application sharedInstance;

    private ApplicationWindow activeWindow;

    private WindowManager windowManager = new WindowManager();

    private ApplicationAdvisor advisor;

    public Application(ApplicationServices applicationServices, ApplicationAdvisor advisor) {
        setAdvisor(advisor);
        Assert.isTrue(sharedInstance == null,
                "Only one instance of a RCP Application allowed per VM.");
        load(this);
    }

    public static void load(Application instance) {
        sharedInstance = instance;
    }

    /**
     * Return the single application instance.
     * 
     * @return The application
     */
    public static Application locator() {
        Assert.notNull(sharedInstance,
            "The global application instance has not yet been initialized.");
        return sharedInstance;
    }
    
    public String getName() {
        return advisor.getApplicationName();
    }

    public Image getImage() {
        return advisor.getApplicationImage();
    }

    private void setAdvisor(ApplicationAdvisor advisor) {
        Assert.notNull(advisor);
        this.advisor = advisor;
    }

    protected ApplicationAdvisor getAdvisor() {
        return advisor;
    }

    protected void initApplicationContext() throws BeansException {
        getAdvisor().onPreInitialize(this);
        getAdvisor().onPreStartup();
        openFirstTimeApplicationWindow();
        getAdvisor().onStarted();
    }

    protected void openFirstTimeApplicationWindow() {
        ApplicationWindow mainWindow = createNewWindow();
        mainWindow.openPage(getAdvisor().getStartingPageId());
        this.activeWindow = mainWindow;
    }

    private ApplicationWindow createNewWindow() {
        ApplicationWindow newWindow = new ApplicationWindow(windowManager
                .size());
        windowManager.add(newWindow);
        windowManager.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (windowManager.getWindows().length == 0) {
                    close();
                }
            }
        });
        return newWindow;
    }

    public ApplicationWindow getActiveWindow() {
        return activeWindow;
    }

    public void close() {
        System.exit(0);
    }

    public void openWindow(String pageId) {
        ApplicationWindow newWindow = createNewWindow();
        newWindow.openPage(pageId);
        // @TODO track active window...
        this.activeWindow = newWindow;
    }

}