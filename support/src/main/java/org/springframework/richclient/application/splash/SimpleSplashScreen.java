/*
 * Copyright 2002-2007 the original author or authors.
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * A lightweight splash-screen for display when a GUI application is being
 * initialized.
 * <p>
 * The splash screen renders an image in a Frame. It minimizes class loading so
 * it is displayed immediately once the application is started.
 * 
 * @author Keith Donald
 * @author Jan Hoskens
 */
public class SimpleSplashScreen extends AbstractSplashScreen {
	private Image image;

	private Resource imageResourcePath;

	private static final Log logger = LogFactory.getLog(SimpleSplashScreen.class);

	/**
	 * Creates a new uninitialized {@code SimpleSplashScreen}.
	 */
	public SimpleSplashScreen() {
		// do nothing
	}

	/**
	 * Creates a new {@code SimpleSplashScreen} that will display the image at
	 * the specified location.
	 * 
	 * @param imageResourcePath The location of the image file to be displayed
	 * by this splash screen.
	 * 
	 * @see #setImageResourcePath(String)
	 */
	public SimpleSplashScreen(Resource imageResourcePath) {
		setImageResourcePath(imageResourcePath);
	}

	/**
	 * Creates a new {@code SimpleSplashScreen} that will display the given
	 * image.
	 * 
	 * @param image the image to splash.
	 * 
	 * @throws IllegalArgumentException if {@code image} is null.
	 */
	public SimpleSplashScreen(Image image) {
		Assert.notNull(image, "The splash screen image is required");
		this.image = image;
	}

	/**
	 * Sets the location of the image to be displayed by this splash screen. If
	 * the given path starts with a '/', it is interpreted to be relative to the
	 * root of the runtime classpath. Otherwise it is interpreted to be relative
	 * to the subdirectory of the classpath root that corresponds to the package
	 * of this class.
	 * 
	 * @param path The path to the splash screen image.
	 */
	public void setImageResourcePath(Resource path) {
		Assert.notNull(path, "The splash screen image resource path is required");
		this.imageResourcePath = path;
	}

	/**
	 * Load image from path.
	 * 
	 * @param path Path to image.
	 * @return Image
	 * @throws IOException
	 * 
	 * @throws NullPointerException if {@code path} is null.
	 */
	private Image loadImage(Resource path) throws IOException {
		URL url = path.getURL();
		if (url == null) {
			logger.warn("Unable to locate splash screen in classpath at: " + path);
			return null;
		}
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	protected Image getImage() {
		if (image == null && imageResourcePath != null) {
			try {
				image = loadImage(imageResourcePath);
			}
			catch (IOException e) {
				logger.error("Unable to load image from resource " + imageResourcePath, e);
			}
		}
		return image;
	}

	/**
	 * Simple Canvas that paints an image.
	 */
	public class ImageCanvas extends JPanel {
		private static final long serialVersionUID = -5096223464173393949L;

		private Image image;

		/**
		 * Creates a new {@code ImageCanvas} with the specified image. The size
		 * of the canvas will be set to the size of the image.
		 * 
		 * @param image The image to be displayed by the canvas.
		 * 
		 * @throws NullPointerException if {@code image} is null.
		 */
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

		public void paintComponent(Graphics g) {
			g.clearRect(0, 0, getWidth(), getHeight());
			g.drawImage(image, 0, 0, this);
		}
	}

	/**
	 * Returns a component that displays an image in a canvas.
	 */
	protected Component createContentPane() {
		Image image = getImage();
		if (image != null) {
			return new ImageCanvas(image);
		}
		return null;
	}
}
