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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The main driver for a GUI application. This class displays a configurable
 * splash screen and instantiates the rich client <code>Application</code>
 * instance.
 * 
 * @author Keith Donald
 * @see Application
 */
public class ApplicationLauncher {
    public static final String SPLASH_SCREEN_BEAN_NAME = "splashScreen";

    public static final String APPLICATION_BEAN_NAME = "application";

    private final Log logger = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    private SplashScreen splashScreen;

    /**
     * Launch the application using the spring application context at the
     * provided path for configuration.
     * 
     * @param contextPath
     *            the classpath application context path
     */
    public ApplicationLauncher(String contextPath) {
        Assert.hasText(contextPath, "The contextPath is required");
        launch(null, new String[] { contextPath });
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration.
     * 
     * @param contextPaths
     *            the classpath application context paths
     */
    public ApplicationLauncher(String[] contextPaths) {
        Assert.notEmpty(contextPaths, "One or more context paths must be provided");
        launch(null, contextPaths);
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration. The startup context path is loaded
     * first to allow for quick loading of the application splash screen.
     * 
     * @param startupContext
     *            the startup context classpath
     * @param contextPaths
     *            the classpath application context paths
     */
    public ApplicationLauncher(String startupContext, String contextPath) {
        this(startupContext, new String[] { contextPath });
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration. The startup context path is loaded
     * first to allow for quick loading of the application splash screen.
     * 
     * @param startupContext
     *            the startup context classpath
     * @param contextPaths
     *            the classpath application context paths
     */
    public ApplicationLauncher(String startupContext, String[] contextPaths) {
        Assert.notEmpty(contextPaths, "One or more context paths must be provided");
        launch(startupContext, contextPaths);
    }

    /**
     * Launch the application from the pre-loaded application context.
     * 
     * @param context
     *            the application context.
     */
    public ApplicationLauncher(ApplicationContext context) {
        setApplicationContext(context);
        launch();
    }

    private void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    /**
     * Launch this rich client application; with the startup context loading
     * first, built from the <code>startupContextPath</code> location in the
     * classpath.
     * 
     * It is recommended that the startup context contain contain a splash
     * screen definition for quick loading & display.
     * 
     * Once the splash screen is displayed, the main application context is then
     * initialized, built from the <code>contextPaths</code> location(s) in
     * the classpath. The root application bean is retrieved and the startup
     * lifecycle begins.
     * 
     * @param startupContextPath
     * @param contextPaths
     */
    private void launch(String startupContextPath, String[] contextPaths) {
        logger.info("Launching Application...");
        try {
            if (StringUtils.hasText(startupContextPath)) {
                ApplicationContext startupContext = new ClassPathXmlApplicationContext(
                        startupContextPath);
                displaySplashScreen(startupContext);
            }
        }
        catch (RuntimeException e) {
            logger.warn("Exception occured initializing context.", e);
        }

        try {
            setApplicationContext(new ClassPathXmlApplicationContext(
                    contextPaths));
        }
        catch (Exception e) {
            logger
                    .warn(
                            "Exception occured initializing application startup context.",
                            e);
        }
        launch();
    }

    private void launch() {
        Assert.state(applicationContext != null, "The application context is required");

        if (splashScreen == null) {
            displaySplashScreen(applicationContext);
        }
        try {
            Application application = (Application)applicationContext.getBean(
                    APPLICATION_BEAN_NAME, Application.class);
            application.openFirstTimeApplicationWindow();
            application.getApplicationAdvisor().onPostStartup();
        }
        catch (NoSuchBeanDefinitionException e) {
            logger
                    .error(
                            "A single org.springframework.richclient.Application bean definition must be defined "
                                    + "in the main application context", e);
            throw e;
        }
        catch (RuntimeException e) {
            logger.error("Exception occured initializing Application bean", e);
            throw e;
        }
        finally {
            destroySplashScreen();
            logger.debug("Launcher thread exiting...");
        }
    }

    private void displaySplashScreen(ApplicationContext context) {
        try {
            if (context.containsBean(SPLASH_SCREEN_BEAN_NAME)) {
                splashScreen = (SplashScreen)context
                        .getBean(SPLASH_SCREEN_BEAN_NAME);
                if (splashScreen != null) {
                    logger.debug("Displaying splash screen...");
                    splashScreen.splash();
                }
            }
        }
        catch (Exception e) {
            logger.warn("Unable to load and display startup splash screen.", e);
        }
    }

    private void destroySplashScreen() {
        if (splashScreen != null) {
            logger.debug("Closing splash screen...");
            new SplashScreenCloser(splashScreen);
        }
    }
}