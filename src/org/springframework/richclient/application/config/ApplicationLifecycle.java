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

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.command.support.DefaultCommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;

/**
 * @author Keith Donald
 */
public abstract class ApplicationLifecycle {
    private Application application;

    private ApplicationWindow managedWindow;

    private boolean introShown;
    
    public void onPreInitialize(Application application) {
        this.application = application;
    }

    public void onPreStartup() {

    }

    public void onStarted() {

    }

    public abstract String getStartingPageId();

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        this.managedWindow = configurer.getWindow();
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