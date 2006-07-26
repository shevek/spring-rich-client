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

import java.awt.EventQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The main driver for a Spring Rich Client application. This class displays a
 * configurable splash screen and instantiates the rich client <code>Application</code>
 * instance.
 * 
 * @author Keith Donald
 * @see Application
 */
public class ApplicationLauncher {
    public static final String SPLASH_SCREEN_BEAN_ID = "splashScreen";

    public static final String APPLICATION_BEAN_ID = "application";

    private final Log logger = LogFactory.getLog( getClass() );

    private ApplicationContext startupContext;

    private SplashScreen splashScreen;

    private ApplicationContext rootApplicationContext;

    /**
     * Launch the application using the spring application context at the provided path
     * for configuration.
     * 
     * @param rootContextPath the classpath application context path
     */
    public ApplicationLauncher( String rootContextPath ) {
        this( new String[] { rootContextPath } );
    }

    /**
     * Launch the application using the spring application context at the provided paths
     * for configuration.
     * 
     * @param rootContextPath the classpath application context paths
     */
    public ApplicationLauncher( String[] rootContextPath ) {
        this( null, rootContextPath );
    }

    /**
     * Launch the application using the spring application context at the provided paths
     * for configuration. The startup context path is loaded first to allow for quick
     * loading of the application splash screen.
     * 
     * @param startupContext the startup context classpath
     * @param contextPaths the classpath application context paths
     */
    public ApplicationLauncher( String startupContext, String rootContextPath ) {
        this( startupContext, new String[] { rootContextPath } );
    }

    /**
     * Launch the application using the spring application context at the provided paths
     * for configuration. The startup context path is loaded first to allow for quick
     * loading of the application splash screen.
     * 
     * @param startupContextPath the startup context classpath
     * @param rootContextPath the classpath application context paths
     */
    public ApplicationLauncher( String startupContextPath, String[] rootContextPath ) {
        Assert.notEmpty( rootContextPath, "One or more root rich client application context paths must be provided" );
        this.startupContext = loadStartupContext( startupContextPath );
        if( startupContext != null ) {
            displaySplashScreen( startupContext );
        }
        this.rootApplicationContext = loadRootApplicationContext( rootContextPath );
        launchMyRichClient();
    }

    /**
     * Launch the application from the pre-loaded application context.
     * 
     * @param context the application context.
     */
    public ApplicationLauncher( String startupContextPath, ApplicationContext rootApplicationContext ) {
        this.startupContext = loadStartupContext( startupContextPath );
        if( startupContext != null ) {
            displaySplashScreen( startupContext );
        }
        setRootApplicationContext( rootApplicationContext );
        launchMyRichClient();
    }

    /**
     * Launch the application from the pre-loaded application context.
     * 
     * @param context the application context.
     */
    public ApplicationLauncher( ApplicationContext rootApplicationContext ) {
        setRootApplicationContext( rootApplicationContext );
        launchMyRichClient();
    }

    private void setRootApplicationContext( ApplicationContext context ) {
        Assert.notNull( context, "The root rich client application context is required" );
        this.rootApplicationContext = context;
    }

    private ApplicationContext loadStartupContext( String startupContextPath ) {
        logger.info( "Loading startup context..." );
        try {
            if( StringUtils.hasText( startupContextPath ) ) {
                return new ClassPathXmlApplicationContext( startupContextPath );
            }
        } catch( Exception e ) {
            logger.warn( "Exception occured initializing startup context.", e );
        }
        return null;
    }

    private ApplicationContext loadRootApplicationContext( String[] contextPaths ) {
        try {
            return new ClassPathXmlApplicationContext( contextPaths );
        } catch( Exception e ) {
            logger.warn( "Exception occured initializing application startup context.", e );
            destroySplashScreen(); // when app context fails to load, destroy
                                    // splashscreen
            throw new ApplicationException( "Unable to start rich client application", e );
        }
    }

    /**
     * Launch this rich client application; with the startup context loading first, built
     * from the <code>startupContextPath</code> location in the classpath.
     * <p>
     * It is recommended that the startup context contain contain a splash screen
     * definition for quick loading & display.
     * <p>
     * Once the splash screen is displayed, the main application context is then
     * initialized, built from the <code>contextPaths</code> location(s) in the
     * classpath. The root application bean is retrieved and the startup lifecycle begins.
     */
    private void launchMyRichClient() {
        if( startupContext == null ) {
            displaySplashScreen( rootApplicationContext );
        }

        try {
            Application application = (Application) rootApplicationContext.getBean( APPLICATION_BEAN_ID,
                    Application.class );
            application.start();
        } catch( NoSuchBeanDefinitionException e ) {
            logger.error( "A single org.springframework.richclient.Application bean definition must be defined "
                    + "in the main application context", e );
            throw e;
        } catch( RuntimeException e ) {
            logger.error( "Exception occured initializing Application bean", e );
            throw new ApplicationException( "Unable to start rich client application", e );
        } finally {
            destroySplashScreen();
            logger.debug( "Launcher thread exiting..." );
        }
    }

    private void displaySplashScreen( BeanFactory beanFactory ) {
        try {
            if( beanFactory.containsBean( SPLASH_SCREEN_BEAN_ID ) ) {
                this.splashScreen = (SplashScreen) beanFactory.getBean( SPLASH_SCREEN_BEAN_ID, SplashScreen.class );
                logger.info( "Displaying application splash screen..." );
                this.splashScreen.splash();
            } else {
                logger.info( "No splash screen bean found to display--continuing..." );
            }
        } catch( Exception e ) {
            logger.warn( "Unable to load and display startup splash screen.", e );
        }
    }

    private void destroySplashScreen() {
        if( splashScreen != null ) {
            logger.debug( "Closing splash screen..." );
            new SplashScreenCloser( splashScreen );
        }
    }

    /**
     * Closes the splash screen in the event dispatching (GUI) thread.
     * 
     * @author Keith Donald
     * @see SplashScreen
     */
    public static class SplashScreenCloser {

        /**
         * Closes the currently-displayed, non-null splash screen.
         * 
         * @param splashScreen
         */
        public SplashScreenCloser( final SplashScreen splashScreen ) {

            /*
             * Removes the splash screen.
             * 
             * Invoke this <code> Runnable </code> using <code> EventQueue.invokeLater
             * </code> , in order to remove the splash screen in a thread-safe manner.
             */
            EventQueue.invokeLater( new Runnable() {
                public void run() {
                    splashScreen.dispose();
                }
            } );
        }
    }
}
