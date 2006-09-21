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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.validation.Severity;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.exceptionhandling.RegisterableExceptionHandler;
import org.springframework.richclient.exceptionhandling.DefaultRegisterableExceptionHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 * @author Jim Moore
 */
public abstract class ApplicationLifecycleAdvisor implements InitializingBean {
    private static final String EXCEPTION_HANDLER_KEY = "sun.awt.exception.handler";

    private Application application;

    private ApplicationWindow openingWindow;

    private String startingPageId;

    private RegisterableExceptionHandler registerableExceptionHandler;

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
     * Sets the exception handler which will be registered upon initialization to handle uncaught throwables.
     *
     * By default this is a DefaultRegisterableExceptionHandler,
     * which is inferiour to a well configured DelegatingExceptionHandler (java 1.5 only).
     *
     * @param registerableExceptionHandler the exception handler which will handle uncaught throwables
     */
    public void setRegisterableExceptionHandler(RegisterableExceptionHandler registerableExceptionHandler) {
        this.registerableExceptionHandler = registerableExceptionHandler;
    }

    public void afterPropertiesSet() throws Exception {
        getRegisterableExceptionHandler().registerExceptionHandler();
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
        return ApplicationServicesLocator.services();
    }

    public void onPreInitialize(Application application) {

    }

    public void onPreStartup() {

    }

    public void onPostStartup() {

    }

    public void setOpeningWindow(ApplicationWindow window) {
    	this.openingWindow = window;
    }

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
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

    public void onWindowOpened(ApplicationWindow window) {

    }

    public boolean onPreWindowClose(ApplicationWindow window) {
        return true;
    }

    public RegisterableExceptionHandler getRegisterableExceptionHandler() {
        if (registerableExceptionHandler == null) {
            this.registerableExceptionHandler = new DefaultRegisterableExceptionHandler();
        }
        return registerableExceptionHandler;
    }

	public void setApplication(Application application) {
		this.application = application;
	}

}