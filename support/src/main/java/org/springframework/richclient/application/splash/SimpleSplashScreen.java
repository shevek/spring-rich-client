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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * A lightweight splash-screen for display when a GUI application is being initialized.
 * <p>
 * The splash screen renders a image in a Frame. It minimizes class loading so it is
 * displayed immediately once the application is started.
 * 
 * @author Keith Donald
 * @author Jan Hoskens
 */
public class SimpleSplashScreen extends AbstractSplashScreen {
    private Image image;

    private String imageResourcePath;

    private static final Log logger = LogFactory.getLog(SimpleSplashScreen.class);

    public SimpleSplashScreen() {
    }

    /**
     * Initialize and show a splash screen of the image at the specified URL.
     * 
     * @param imageURL the URL of the image to splash.
     */
    public SimpleSplashScreen(String imageResourcePath) {
        setImageResourcePath(imageResourcePath);
    }

    /**
     * Initialize and show a splash screen of the specified image.
     * 
     * @param image the image to splash.
     */
    public SimpleSplashScreen(Image image) {
        Assert.notNull(image, "The splash screen image is required");
        this.image = image;
    }

    public void setImageResourcePath(String path) {
        Assert.hasText(path, "The splash screen image resource path is required");
        this.imageResourcePath = path;
    }

    /**
     * Load image from path.
     * 
     * @param path Path to image.
     * @return Image
     */
    private Image loadImage(String path) {
        URL url = this.getClass().getResource(path);
        if (url == null) {
            logger.warn("Unable to locate splash screen in classpath at: " + path);
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
    }

    /**
     * Simple Canvas that paints an image.
     */
    public class ImageCanvas extends JPanel {
        private static final long serialVersionUID = -5096223464173393949L;
        private Image image;

        public ImageCanvas(Image image) {
            this.image = image;

            loadImage();

            Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));

            setSize(size);
            setPreferredSize(size);
            setMinimumSize(size);
        }

        private void loadImage() {
            MediaTracker mediaTracker = new MediaTracker(this);
            mediaTracker.addImage(image, 0);
            try {
                mediaTracker.waitForID(0);
            }
            catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for splash image to load.", e);
            }
        }

        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, this);
        }
    }

    protected JComponent createSplashContentPane() {
        if (image == null) {
            image = loadImage(imageResourcePath);
            if (image == null) {
                return null;
            }
        }

        return new ImageCanvas(image);
    }
}
