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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.session.ApplicationSessionInitializer;
import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.application.statusbar.support.DefaultStatusBar;
import org.springframework.richclient.application.support.ApplicationWindowCommandManager;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.exceptionhandling.DefaultRegisterableExceptionHandler;
import org.springframework.richclient.exceptionhandling.RegisterableExceptionHandler;

/**
 * <p>
 * Advisor for the Application. This class provides the startingPageId, an
 * exceptionHandler for uncaught exceptions, application wide window
 * structures(menu/toolbar/statusbar) and a number of hook methods that are
 * called in the startup/closing process.
 * </p>
 *
 * <p>
 * The sequence in which the hooks are called is as follows:
 * </p>
 *
 * <pre>
 * Application Creation
 * 		{@link ApplicationLifecycleAdvisor#setApplication(Application)}
 * 		{@link ApplicationLifecycleAdvisor#onPreInitialize(Application)}
 *
 * Application Start
 * 		{@link ApplicationLifecycleAdvisor#onPreStartup()}
 *
 * 		ApplicationWindow Creation
 * 			{@link ApplicationLifecycleAdvisor#setOpeningWindow(ApplicationWindow)}
 * 			{@link ApplicationLifecycleAdvisor#onPreWindowOpen(ApplicationWindowConfigurer)}
 * 			{@link ApplicationLifecycleAdvisor#createWindowCommandManager()}
 * 			{@link ApplicationLifecycleAdvisor#getMenuBarCommandGroup()}
 * 			{@link ApplicationLifecycleAdvisor#getToolBarCommandGroup()}
 * 			{@link ApplicationLifecycleAdvisor#getStatusBar()}
 * 			{@link ApplicationLifecycleAdvisor#onCommandsCreated(ApplicationWindow)}
 *
 * 		ApplicationWindow Creating the JFrame
 * 			{@link ApplicationLifecycleAdvisor#onWindowCreated(ApplicationWindow)}
 * 			ApplicationWindow Shows JFrame (setVisible(true))
 * 			{@link ApplicationLifecycleAdvisor#onWindowOpened(ApplicationWindow)}
 *
 * 		{@link ApplicationLifecycleAdvisor#onPostStartup()}
 * </pre>
 *
 * <p>
 * The remaining hook is called when the ApplicationWindow is closed:
 * {@link ApplicationLifecycleAdvisor#onPreWindowClose(ApplicationWindow)}.
 * </p>
 *
 * @author Keith Donald
 * @author Jim Moore
 */
public abstract class ApplicationLifecycleAdvisor implements InitializingBean {

	/** Application to work with. */
	private Application application;

	/** The applicationWindow. */
	private ApplicationWindow openingWindow;

	/** Initial page to show. */
	private String startingPageId;

    private ApplicationSessionInitializer applicationSessionInitializer;

	/** ExceptionHandler to catch all uncaught exceptions. */
	private RegisterableExceptionHandler registerableExceptionHandler;

	/**
	 * This is used to allow the ViewDescriptor to be lazily created when the
	 * ApplicationWindow is opened. Useful when the ApplicationAdvisor needs to
	 * do things before ViewDescriptor should be created, such as setting up a
	 * security context.
	 *
	 * @param pageDescriptorId id of the pageDescriptor bean to show on startup.
	 *
	 * @see #getStartingPageId()
	 */
	public void setStartingPageId(String pageDescriptorId) {
		this.startingPageId = pageDescriptorId;
	}

	/**
	 * Sets the exception handler which will be registered upon initialization
	 * to handle uncaught throwables.
	 *
	 * By default this is a DefaultRegisterableExceptionHandler, which is
	 * inferior to a well configured DelegatingExceptionHandler (java 1.5 only).
	 *
	 * @param registerableExceptionHandler the exception handler which will
	 * handle uncaught throwables
	 */
	public void setRegisterableExceptionHandler(RegisterableExceptionHandler registerableExceptionHandler) {
		this.registerableExceptionHandler = registerableExceptionHandler;
	}

	/**
	 * After properties are set, register the exceptionHandler.
	 */
	public void afterPropertiesSet() throws Exception {
		getRegisterableExceptionHandler().registerExceptionHandler();
	}

	/**
	 * @return the id of the starting Page.
	 */
	public String getStartingPageId() {
		return startingPageId;
	}

	/**
	 * @return Application.
	 */
	protected Application getApplication() {
		return application;
	}

	/**
	 * @return ApplicationServices.
	 */
	protected ApplicationServices getApplicationServices() {
		return ApplicationServicesLocator.services();
	}

	/**
	 * Hook called right after the application has been created.
	 *
	 * @param application the application.
	 */
	public void onPreInitialize(Application application) {

	}

	/**
	 * Hook called right before the applicationWindow is created.
	 */
	public void onPreStartup() {

	}

	/**
	 * Hook called right after the applicationWindow is created.
	 */
	public void onPostStartup() {

	}

	/**
	 * Hook called right afther the application is closed.
	 */
	public void onShutdown() {

	}

	/**
	 * @param window the openingWindow.
	 */
	public void setOpeningWindow(ApplicationWindow window) {
		this.openingWindow = window;
	}

	/**
	 * Hook called right before the application opens a window.
	 *
	 * @param configurer
	 */
	public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
		configurer.setTitle(getApplication().getName());
		configurer.setImage(getApplication().getImage());
	}

	/**
	 * @return the openingWindow.
	 */
	protected final ApplicationWindow getOpeningWindow() {
		return openingWindow;
	}

	/**
	 * Create a {@link ApplicationWindowCommandManager} for the application.
	 *
	 * @return applicationWindowCommandManager.
	 */
	public ApplicationWindowCommandManager createWindowCommandManager() {
		return new ApplicationWindowCommandManager();
	}

	/**
	 * Create the menuBar for the application.
	 *
	 * @return a CommandGroup.
	 */
	public CommandGroup getMenuBarCommandGroup() {
		return new CommandGroup();
	}

	/**
	 * Create the toolBar for the application.
	 *
	 * @return a CommandGroup.
	 */
	public CommandGroup getToolBarCommandGroup() {
		return new CommandGroup();
	}

	/**
	 * Create the statusBar for the application.
	 *
	 * @return a statusBar.
	 */
	public StatusBar getStatusBar() {
		return new DefaultStatusBar();
	}

	/**
	 * Hook called right after commands are initialized. Typically the next step
	 * after the get*CommandGroup() methods are called.
	 *
	 * @param window applicationWindow.
	 */
	public void onCommandsCreated(ApplicationWindow window) {

	}

	/**
	 * Hook called right after the window (JFrame) of the application is
	 * created.
	 *
	 * @param window applicationWindow.
	 */
	public void onWindowCreated(ApplicationWindow window) {

	}

	/**
	 * Hook called right after the window (JFrame) of the application is shown
	 * (setVisible(true)).
	 *
	 * @param window applicationWindow.
	 */
	public void onWindowOpened(ApplicationWindow window) {

	}

	/**
	 * Check if the ApplicationWindow can close.
	 *
	 * @param window the applicationWindow that should be closed.
	 * @return <code>true</code> if the window may close.
	 */
	public boolean onPreWindowClose(ApplicationWindow window) {
		return true;
	}

	/**
	 * @return the ExceptionHandler to be registered as
	 * uncaughtExceptionHandler.
	 */
	public RegisterableExceptionHandler getRegisterableExceptionHandler() {
		if (registerableExceptionHandler == null) {
			this.registerableExceptionHandler = new DefaultRegisterableExceptionHandler();
		}
		return registerableExceptionHandler;
	}

	/**
	 * @param application set the current application.
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

    public ApplicationSessionInitializer getApplicationSessionInitializer()
    {
        return applicationSessionInitializer;
    }

    public void setApplicationSessionInitializer(ApplicationSessionInitializer applicationSessionInitializer)
    {
        this.applicationSessionInitializer = applicationSessionInitializer;
    }
}