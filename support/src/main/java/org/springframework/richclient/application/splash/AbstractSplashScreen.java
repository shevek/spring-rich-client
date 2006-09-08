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

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.richclient.progress.NullProgressMonitor;
import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.util.Assert;

/**
 * Abstract helper implementation for <code>SplashScreen</code>. The only method that needs to be
 * implemented is <code>{@link #createSplashContentPane()}</code>.
 * <p>
 * This class returns a <code>{@link NullProgressMonitor}</code> instance by default. 
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractSplashScreen implements SplashScreen {
    private JFrame frame;
    private MessageSource messageSource;
    private String iconResourcePath;
    private ProgressMonitor progressMonitor;
    private static final Log logger = LogFactory.getLog(AbstractSplashScreen.class);

    public String getIconResourcePath() {
        return iconResourcePath;
    }

    public void setIconResourcePath(String iconSourcePath) {
        this.iconResourcePath = iconSourcePath;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public final void dispose() {
        frame.dispose();
        frame = null;
    }

    public final void splash() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setUndecorated(true);

        frame.setTitle(loadFrameTitle());
        frame.setIconImage(loadFrameIcon());

        JComponent content = createSplashContentPane();
        Assert.notNull(content, "Splash content cannot be null");

        frame.getContentPane().add(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String loadFrameTitle() {
        return messageSource == null ? "" : messageSource.getMessage("splash.title", null, null);
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

    protected abstract JComponent createSplashContentPane();

    public ProgressMonitor getProgressMonitor() {
        if (progressMonitor == null) {
            progressMonitor = new NullProgressMonitor();
        }
        return progressMonitor;
    }
    
    public void setProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }
}
