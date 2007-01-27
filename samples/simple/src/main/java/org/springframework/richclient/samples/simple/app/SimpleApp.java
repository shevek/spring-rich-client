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
import org.springframework.richclient.application.ApplicationLauncher;

/**
 * This application provides a very simple introduction to the Spring Richclient platform. It is intended to highlight
 * the common models for working with the platform (including configuration and runtime access to platform services) and
 * how to organize the basic pieces of an application using the platform.
 * <p>
 * This sample provides an implementation of a&nbsp;<em>trivial</em> address book. Trust me when I say trivial :-) It
 * has no persistence, no security, and no complex business logic. The focus of the sample is how to work with the
 * Spring Rich platform, not about how to build a great address book.
 * <p>
 * Other samples will introduce more complex topics like user management (security), alternate page/view layouts,
 * complex data binding, remoting, etc.
 * <p>
 * The Spring Rich platform relies on the <a href="http://www.springframework.org/">Spring</a> project to manage the
 * application context with all the associated benefits it offers.
 * <p>
 * A start at the Spring Rich Client documentation can be found on the <a
 * href="http://opensource.atlassian.com/confluence/spring/display/RCP/Home">wiki</a>.
 * </p>
 * @author Larry Streepy
 * @see The <a href="http://www.springframework.org/">Spring project</a>
 * @see The <a href="http://opensource.atlassian.com/confluence/spring/display/RCP/Home">Spring Rich Wiki</a>
 */
public class SimpleApp {

	private static final Log logger = LogFactory.getLog(SimpleApp.class);

	/**
	 * Main routine for the simple sample application.
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("SimpleApp starting up");

		// In order to launch the platform, we have to construct an
		// application context that defines the beans (services) and
		// wiring. This is pretty much straight Spring.
		//
		// Part of this configuration will indicate the initial page to be
		// displayed.

		String rootContextDirectoryClassPath = "/org/springframework/richclient/samples/simple/ctx";

		// The startup context defines elements that should be available
		// quickly such as a splash screen image.

		String startupContextPath = rootContextDirectoryClassPath + "/richclient-startup-context.xml";

		String richclientApplicationContextPath = rootContextDirectoryClassPath + "/richclient-application-context.xml";

		// The ApplicationLauncher is responsible for loading the contexts,
		// presenting the splash screen, initializing the Application
		// singleton instance, creating the application window to display
		// the initial page.

		try {
			new ApplicationLauncher(startupContextPath, new String[] { richclientApplicationContextPath });
		}
		catch (RuntimeException e) {
			logger.error("RuntimeException during startup", e);
		}
	}

}
