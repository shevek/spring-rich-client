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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.application.splash.MonitoringSplashScreen;
import org.springframework.richclient.application.splash.SplashScreen;
import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.richclient.util.Assert;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * The main driver for a Spring Rich Client application.
 * 
 * <p>
 * This class displays a configurable splash screen and launches a rich client
 * {@link Application}. Both the splash screen and the application to be
 * launched are expected to be defined as beans, under the names
 * {@link #SPLASH_SCREEN_BEAN_ID} and {@link #APPLICATION_BEAN_ID}
 * respectively, in one of the application contexts provided to the constructor.
 * </p>
 * 
 * <p>
 * For quick loading and display of the splash screen while the rest of the
 * application is being initialized, constructors are provided that take a
 * separate startup context. The startup context will be searched for the
 * {@link #SPLASH_SCREEN_BEAN_ID} bean, which will then be displayed before the
 * main application context is loaded and the application launched. If no
 * startup context is provided or it doesn't contain an appropriately named
 * splash screen bean, an attempt will be made to load a splash screen from the
 * main application context. This can only happen after the main application
 * context has been loaded in its entirety so it is not the recommended approach
 * for displaying a splash screen.
 * </p>
 * 
 * @author Keith Donald
 * @see Application
 */
public class ApplicationLauncher {

	/**
	 * The name of the bean that defines the application's splash screen.
     * {@value}
	 */
	public static final String SPLASH_SCREEN_BEAN_ID = "splashScreen";

	/**
	 * The name of the bean that defines the {@code Application} that this class
	 * will launch.
     * {@value}
	 */
	public static final String APPLICATION_BEAN_ID = "application";

	private final Log logger = LogFactory.getLog(getClass());

	private ApplicationContext startupContext;

	private SplashScreen splashScreen;

	private ApplicationContext rootApplicationContext;

	/**
	 * Launches the application defined by the Spring application context file
	 * at the provided classpath-relative location.
	 * 
	 * @param rootContextPath The classpath-relative location of the application context file.
	 * 
	 * @throws IllegalArgumentException if {@code rootContextPath} is null or empty.
	 */
	public ApplicationLauncher(String rootContextPath) {
		this(new String[] { rootContextPath });
	}

	/**
	 * Launches the application defined by the Spring application context files
	 * at the provided classpath-relative locations.
	 * 
	 * @param rootContextConfigLocations the classpath-relative locations of the
	 * application context files.
	 * 
	 * @throws IllegalArgumentException if {@code rootContextPath} is null or empty.
	 */
	public ApplicationLauncher(String[] rootContextConfigLocations) {
		this(null, rootContextConfigLocations);
	}

	/**
	 * Launches the application defined by the Spring application context files
	 * at the provided classpath-relative locations. The application context
	 * file specified by {@code startupContext} is loaded first to allow for
	 * quick loading of the application splash screen. It is recommended that
	 * the startup context only contains the bean definition for the splash
	 * screen and any other beans that it depends upon. Any beans defined in the
	 * startup context will not be available to the main application once
	 * launched.
	 * 
	 * @param startupContextPath The classpath-relative location of the startup
	 * application context file. May be null or empty.
	 * @param rootContextPath The classpath-relative location of the main
	 * application context file.
	 * 
	 * @throws IllegalArgumentException if {@code rootContextPath} is null or empty.
	 */
	public ApplicationLauncher(String startupContextPath, String rootContextPath) {
		this(startupContextPath, new String[] { rootContextPath });
	}

	/**
	 * Launches the application defined by the Spring application context files
	 * at the provided classpath-relative locations. The application context
	 * file specified by {@code startupContextPath} is loaded first to allow for
	 * quick loading of the application splash screen. It is recommended that
	 * the startup context only contains the bean definition for the splash
	 * screen and any other beans that it depends upon. Any beans defined in the
	 * startup context will not be available to the main application once
	 * launched.
	 * 
	 * @param startupContextPath The classpath-relative location of the startup
	 * context file. May be null or empty.
	 * @param rootContextConfigLocations The classpath-relative locations of the main
	 * application context files.
	 * 
	 * @throws IllegalArgumentException if {@code rootContextConfigLocations} is null or empty.
	 */
	public ApplicationLauncher(String startupContextPath, String[] rootContextConfigLocations) {
        
        Assert.noElementsNull(rootContextConfigLocations, "rootContextConfigLocations");
        Assert.notEmpty(rootContextConfigLocations, 
                        "One or more root rich client application context paths must be provided");
        
		this.startupContext = loadStartupContext(startupContextPath);
		if (startupContext != null) {
			displaySplashScreen(startupContext);
		}
		try {
			setRootApplicationContext(loadRootApplicationContext(rootContextConfigLocations, startupContext));
			launchMyRichClient();
		}
		finally {
			destroySplashScreen();
		}
	}

	/**
	 * Launches the application from the pre-loaded application context.
	 * 
	 * @param rootApplicationContext The main application context.
	 * 
	 * @throws IllegalArgumentException if {@code rootApplicationContext} is
	 * null.
	 */
	public ApplicationLauncher(ApplicationContext rootApplicationContext) {
		this(null, rootApplicationContext);
	}

	/**
	 * Launch the application using a startup context from the given location
	 * and a pre-loaded application context.
	 * 
	 * @param startupContextPath the classpath-relative location of the starup
	 * application context file. If null or empty, no splash screen will be
	 * displayed.
	 * @param rootApplicationContext the main application context.
	 * 
	 * @throws IllegalArgumentException if {@code rootApplicationContext} is
	 * null.
	 * 
	 */
	public ApplicationLauncher(String startupContextPath, ApplicationContext rootApplicationContext) {
		this.startupContext = loadStartupContext(startupContextPath);
		if (startupContext != null) {
			displaySplashScreen(startupContext);
		}
		try {
			setRootApplicationContext(rootApplicationContext);
			launchMyRichClient();
		}
		finally {
			destroySplashScreen();
		}
	}

	/**
	 * Returns an application context loaded from the bean definition file at
	 * the given classpath-relative location.
	 * 
	 * @param startupContextPath The classpath-relative location of the
	 * application context file to be loaded. May be null or empty.
	 * 
	 * @return An application context loaded from the given location, or null if
	 * {@code startupContextPath} is null or empty.
	 */
	private ApplicationContext loadStartupContext(String startupContextPath) {
        if (!StringUtils.hasText(startupContextPath)) {
            return null;
        }
        logger.info("Loading startup context from classpath resource ("
                    + startupContextPath
                    + ")");
        return new ClassPathXmlApplicationContext(startupContextPath);
    }

    /**
     * Returns an {@code ApplicationContext}, loaded from the bean definition
     * files at the classpath-relative locations specified by
     * {@code configLocations}.
     *
     * <p>
     * If a splash screen has been created, the application context will be
     * loaded with a bean post processor that will notify the splash screen's
     * progress monitor as each bean is initialized.
     * </p>
     *
     * @param configLocations The classpath-relative locations of the files from
     * which the application context will be loaded.
     *
     * @return The main application context, never null.
     */
    private ApplicationContext loadRootApplicationContext(String[] configLocations, MessageSource messageSource) {
        final ClassPathXmlApplicationContext applicationContext
                = new ClassPathXmlApplicationContext(configLocations, false);

        if (splashScreen instanceof MonitoringSplashScreen) {
            final ProgressMonitor tracker = ((MonitoringSplashScreen) splashScreen).getProgressMonitor();

            applicationContext.addBeanFactoryPostProcessor(
                    new ProgressMonitoringBeanFactoryPostProcessor(tracker, messageSource));

        }

        applicationContext.refresh();

        return applicationContext;
    }

    private void setRootApplicationContext(ApplicationContext context) {
        Assert.notNull(context, "The root rich client application context is required");
        this.rootApplicationContext = context;
    }

    /**
     * Launches the rich client application. If no startup context has so far
     * been provided, the main application context will be searched for a splash
     * screen to display. The main application context will then be searched for
     * the {@link Application} to be launched, using the bean name
     * {@link #APPLICATION_BEAN_ID}. If the application is found, it will be
     * started.
     *
     */
    private void launchMyRichClient() {
        
        if (startupContext == null) {
            displaySplashScreen(rootApplicationContext);
        }

        final Application application;
        
        try {
            application = (Application) rootApplicationContext.getBean(APPLICATION_BEAN_ID, Application.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException(
                    "A single bean definition with id "
                    + APPLICATION_BEAN_ID
                    + ", of type "
                    + Application.class.getName()
                    + " must be defined in the main application context", 
                    e);
        }
        
        try {
            // To avoid deadlocks when events fire during initialization of some swing components
            // Possible to do: in theory not a single Swing component should be created (=modified) in the launcher thread...
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    application.start();
                }
            });
        }
        catch (InterruptedException e) {
            logger.warn("Application start interrupted", e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new IllegalStateException("Application start thrown an exception: " + cause.getMessage(), cause);
        }
        
        logger.debug("Launcher thread exiting...");
        
    }

    /**
     * Searches the given bean factory for a {@link SplashScreen} defined with
     * the bean name {@link #SPLASH_SCREEN_BEAN_ID} and displays it, if found.
     *
     * @param beanFactory The bean factory that is expected to contain the
     * splash screen bean definition. Must not be null.
     *
     * @throws NullPointerException if {@code beanFactory} is null.
     * @throws BeanNotOfRequiredTypeException if the bean found under the splash
     * screen bean name is not a {@link SplashScreen}.
     *
     */
    private void displaySplashScreen(BeanFactory beanFactory) {
        if (beanFactory.containsBean(SPLASH_SCREEN_BEAN_ID)) {
            this.splashScreen = (SplashScreen) beanFactory.getBean(SPLASH_SCREEN_BEAN_ID, SplashScreen.class);
            logger.debug("Displaying application splash screen...");
            try
            {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        ApplicationLauncher.this.splashScreen.splash();
                    }
                });
            }
            catch (Exception e)
            {
                throw new RuntimeException("EDT threading issue while showing splash screen", e);
            }
        }
        else {
            logger.info("No splash screen bean found to display. Continuing...");
        }
    }

    private void destroySplashScreen() {
        if (splashScreen != null) {
            logger.debug("Closing splash screen...");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    splashScreen.dispose();
                    splashScreen = null;
                }
            });
        }
    }

}
