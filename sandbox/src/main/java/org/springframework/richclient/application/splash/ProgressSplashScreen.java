/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.application.splash;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Splash screen implementation that shows the progress of the Spring application context loading.
 * <p>
 * TODO i18n of progressbar messages
 * TODO define SplashScreen interface for easier pluggable splash screens
 * 
 * @author Peter De Bruycker
 */
public class ProgressSplashScreen implements ApplicationContextAware, BeanPostProcessor, InitializingBean {

    private JWindow window;

    private Image image;

    private String imageResourcePath;

    private static final Logger logger = Logger.getLogger( ProgressSplashScreen.class.getPackage().getName() );
    private ApplicationContext context;
    private JProgressBar progressBar;
    private int progress = 0;
    private boolean showProgressLabel;

    public ProgressSplashScreen() {
        progressBar = new JProgressBar();
    }

    /**
     * Show the splash screen.
     */
    public void splash() {
        window = new JWindow();
        if( image == null ) {
            image = loadImage( imageResourcePath );
            if( image == null ) {
                return;
            }
        }
        MediaTracker mediaTracker = new MediaTracker( window );
        mediaTracker.addImage( image, 0 );
        try {
            mediaTracker.waitForID( 0 );
        } catch( InterruptedException e ) {
            logger.warning( "Interrupted while waiting for splash image to load." );
        }

        window.getContentPane().add( new JLabel( new ImageIcon( image ) ) );
        window.getContentPane().add( progressBar, BorderLayout.SOUTH );
        window.pack();
        center( window );

        window.setVisible( true );
    }

    public void setImageResourcePath( String path ) {
        Assert.hasText( path, "The splash screen image resource path is required" );
        this.imageResourcePath = path;
    }

    private void center( JWindow window ) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = window.getBounds();
        window.setLocation( (screen.width - r.width) / 2, (screen.height - r.height) / 2 );
    }

    /**
     * Dispose of the the splash screen. Once disposed, the same splash screen instance
     * may not be shown again.
     */
    public void dispose() {
        window.dispose();
        window = null;
    }

    private Image loadImage( String path ) {
        URL url = this.getClass().getResource( path );
        if( url == null ) {
            logger.warning( "Unable to locate splash screen in classpath at: " + path );
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage( url );
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext( ApplicationContext context ) throws BeansException {
        this.context = context;
    }

    /**
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     *      java.lang.String)
     */
    public Object postProcessBeforeInitialization( Object bean, String name ) throws BeansException {
        // if (context.containsBeanDefinition(name)) {
        progressBar.setValue( progress++ );
        if( showProgressLabel ) {
            progressBar.setString( "Loading bean " + name );
        }
        // }
        return bean;
    }

    /**
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     *      java.lang.String)
     */
    public Object postProcessAfterInitialization( Object bean, String name ) throws BeansException {
        return bean;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if( context.containsBean( "lookAndFeelConfigurer" ) ) {
            context.getBean( "lookAndFeelConfigurer" );
            progressBar = new JProgressBar();
        }

        Assert.state( StringUtils.hasText( imageResourcePath ), "The splash screen image resource path is required" );

        if( showProgressLabel ) {
            progressBar.setStringPainted( true );
            progressBar.setString( "Loading context" );
        }
        progressBar.setMinimum( 0 );
        progressBar.setMaximum( calculateMaximum() );
        splash();
    }

    private int calculateMaximum() {
        int maximum = 0;
        String[] beanNames = context.getBeanDefinitionNames();
        for( int i = 0; i < beanNames.length; i++ ) {
            if( context.isSingleton( beanNames[i] ) )
                maximum++;
        }
        return maximum;
    }

    public boolean getShowProgressLabel() {
        return showProgressLabel;
    }

    public void setShowProgressLabel( boolean showProgressLabel ) {
        this.showProgressLabel = showProgressLabel;
    }
}
