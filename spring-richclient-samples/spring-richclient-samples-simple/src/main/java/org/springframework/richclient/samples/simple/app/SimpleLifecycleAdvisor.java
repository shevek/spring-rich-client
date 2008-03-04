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
package org.springframework.richclient.samples.simple.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;

/**
 * Custom application lifecycle implementation that configures the sample app at well defined points within its
 * lifecycle.
 * @author Keith Donald
 */
public class SimpleLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * This method is called prior to the opening of an application window. Note at this point the window control has
	 * not been created. This hook allows programmatic control over the configuration of the window (by setting
	 * properties on the configurer) and it provides a hook where code that needs to be executed prior to the window
	 * opening can be plugged in (like a startup wizard, for example).
	 * @param configurer The application window configurer
	 */
	public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {

		// If you override this method, it is critical to allow the superclass
		// implementation to run as well.
		super.onPreWindowOpen(configurer);

		// Uncomment to hide the menubar, toolbar, or alter window size...
		// configurer.setShowMenuBar(false);
		// configurer.setShowToolBar(false);
		// configurer.setInitialSize(new Dimension(640, 480));
	}

	/**
	 * Called just after the command context has been internalized. At this point, all the commands for the window have
	 * been created and are available for use. If you need to force the execution of a command prior to the display of
	 * an application window (like a login command), this is where you'd do it.
	 * @param window The window who's commands have just been created
	 */
	public void onCommandsCreated(ApplicationWindow window) {
		if (logger.isInfoEnabled()) {
			logger.info("onCommandsCreated( windowNumber=" + window.getNumber() + " )");
		}
	}

	/**
	 * Called after the actual window control has been created.
	 * @param window The window being processed
	 */
	public void onWindowCreated(ApplicationWindow window) {
		if (logger.isInfoEnabled()) {
			logger.info("onWindowCreated( windowNumber=" + window.getNumber() + " )");
		}
	}

	/**
	 * Called immediately after making the window visible.
	 * @param window The window being processed
	 */
	public void onWindowOpened(ApplicationWindow window) {
		if (logger.isInfoEnabled()) {
			logger.info("onWindowOpened( windowNumber=" + window.getNumber() + " )");
		}
	}

	/**
	 * Called when the window is being closed. This hook allows control over whether the window is allowed to close. By
	 * returning false from this method, the window will not be closed.
	 * @return boolean indicator if window should be closed. <code>true</code> to allow the close, <code>false</code>
	 * to prevent the close.
	 */
	public boolean onPreWindowClose(ApplicationWindow window) {
		if (logger.isInfoEnabled()) {
			logger.info("onPreWindowClose( windowNumber=" + window.getNumber() + " )");
		}
		return true;
	}

	/**
	 * Called when the application has fully started. This is after the initial application window has been made
	 * visible.
	 */
	public void onPostStartup() {
		if (logger.isInfoEnabled()) {
			logger.info("onPostStartup()");
		}
	}

}
