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

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationInfo;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.command.support.DefaultCommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;

/**
 * @author Keith Donald
 */
public abstract class ApplicationAdvisor {
    private static final String DEFAULT_APPLICATION_IMAGE_KEY = "applicationImage.default";

    private Application application;

    private ApplicationInfo applicationInfo;

    private ApplicationWindow managedWindow;

    private boolean introShown;

    public void setApplicationInfo(ApplicationInfo info) {
        this.applicationInfo = info;
    }

    public void onPreInitialize(Application application) {
        this.application = application;
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
            return getApplication().getImage(DEFAULT_APPLICATION_IMAGE_KEY);
        }
    }

    public void onPreStartup() {

    }

    public void onStarted() {

    }

    public abstract String getStartingPageId();

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        this.managedWindow = configurer.getWindow();
        configurer.setTitle(getApplicationName());
        configurer.setImage(getApplicationImage());
    }

    protected final ApplicationWindow getManagedWindow() {
        return managedWindow;
    }

    public CommandManager getCommandManager() {
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

    public void onWindowCreated(ApplicationWindow window) {

    }

    public void showIntroIfNecessary(ApplicationWindow window) {
        if (introShown) {
            showIntro(window);
            introShown = true;
        }
    }

    protected void showIntro(ApplicationWindow window) {

    }

    public void onWindowOpened(ApplicationWindow window) {

    }

    public boolean preWindowClose(ApplicationWindow window) {
        return true;
    }

}