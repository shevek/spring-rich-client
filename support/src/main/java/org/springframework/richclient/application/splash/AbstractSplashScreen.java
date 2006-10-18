/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.application.splash;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.richclient.progress.NullProgressMonitor;
import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.util.Assert;

/**
 * An abstract helper implementation of the {@link SplashScreen} interface.
 * 
 * <p>
 * The splash screen produced by this class will be an undecorated, centered
 * frame containing the component provided by {@link #createContentPane()},
 * which is the only method that subclasses need to implement.
 * </p>
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractSplashScreen implements SplashScreen {
    
    /**
     * The message resource key used to look up the splash screen's frame title.
     */
    public static final String SPLASH_TITLE_KEY = "splash.title";
    
    private JFrame frame;
    private MessageSource messageSource;
    private String iconResourcePath;
    
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Returns the location of the image to be used as the icon for the splash
     * screen's frame. The splash screen produced by this class is undecorated, 
     * so the icon will only be visible in the frame's taskbar icon. If the given 
     * path starts with a '/', it is interpreted to be relative to the root of the 
     * runtime classpath. Otherwise it is interpreted to be relative to the 
     * subdirectory of the classpath root that corresponds to the package of this 
     * class. 
     *
     * @return The location of the icon resource.
     */
    public String getIconResourcePath() {
        return iconResourcePath;
    }

    /**
     * Sets the location of the image to be used as the icon for the splash
     * screen's frame. The splash screen produced by this class is undecorated, 
     * so the icon will only be visible in the frame's taskbar icon. If the given 
     * path starts with a '/', it is interpreted to be relative to the root of the 
     * runtime classpath. Otherwise it is interpreted to be relative to the 
     * subdirectory of the classpath root that corresponds to the package of this 
     * class. 
     *
     * @param iconResourcePath The location of the icon resource.
     */
    public void setIconResourcePath(String iconResourcePath) {
        this.iconResourcePath = iconResourcePath;
    }

    /**
     * Returns the message source used to resolve localized messages.
     *
     * @return The message source, or null.
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Sets the message source used to resolve localized messages.
     * 
     * @param messageSource The message source.
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public final void dispose() {
        
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
        
    }

    /**
     * Creates and displays an undecorated, centered splash screen containing the 
     * component provided by {@link #createContentPane()}. If this instance
     * has been provided with a {@link MessageSource}, it will be used to retrieve 
     * the splash screen's frame title under the key {@value #SPLASH_TITLE_KEY}. 
     */
    public final void splash() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setUndecorated(true);

        frame.setTitle(loadFrameTitle());
        frame.setIconImage(loadFrameIcon());

        Component content = createContentPane();
        if(content != null) {
            frame.getContentPane().add(content);
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Loads the message under the key {@value #SPLASH_TITLE_KEY} if 
     * a {@link MessageSource} has been set on this instance.
     * 
     * @return The message resource stored under the key {@value #SPLASH_TITLE_KEY},
     * or null if no message source has been set.
     */
    private String loadFrameTitle() {
        try {
            return messageSource == null ? null : messageSource.getMessage(SPLASH_TITLE_KEY, null, null);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    private Image loadFrameIcon() {
        if (iconResourcePath == null) {
            return null;
        }

        URL url = this.getClass().getResource(iconResourcePath);
        if (url == null) {
            logger.warn("Unable to locate splash screen in classpath at: " + iconResourcePath);
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
    }

    /**
     * Returns the component to be displayed in the splash screen's main frame. If the returned value is null the frame
     * for the splash screen will still be created but will not have any content
     * 
     * @return The content pane component. Can be null.
     */
    protected abstract Component createContentPane();
}
