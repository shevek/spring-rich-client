/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.application;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;
import java.util.logging.Logger;

import org.springframework.util.Assert;

/**
 * A lightweight splash-screen for display when a GUI application is being
 * initialized.
 * <p>
 * The splash screen renders a image in a Frame. It minimizes class loading so
 * it is displayed immediately once the application is started.
 * 
 * @author Keith Donald
 */
public class SplashScreen {
	private Frame frame;

	private Image image;

	private String imageResourcePath;

	private static final Logger logger = Logger.getLogger(SplashScreen.class.getPackage().getName());

	public SplashScreen() {
	}

	/**
	 * Initialize and show a splash screen of the image at the specified URL.
	 * 
	 * @param imageURL
	 *            the URL of the image to splash.
	 */
	public SplashScreen(String imageResourcePath) {
		setImageResourcePath(imageResourcePath);
	}

	/**
	 * Initialize and show a splash screen of the specified image.
	 * 
	 * @param image
	 *            the image to splash.
	 */
	public SplashScreen(Image image) {
		Assert.notNull(image, "The splash screen image is required");
		this.image = image;
	}

	public void setImageResourcePath(String path) {
		Assert.hasText(path, "The splash screen image resource path is required");
		this.imageResourcePath = path;
	}

	/**
	 * Show the splash screen.
	 */
	public void splash() {
		frame = new Frame();
		if (image == null) {
			image = loadImage(imageResourcePath);
			if (image == null) {
				return;
			}
		}
		MediaTracker mediaTracker = new MediaTracker(frame);
		mediaTracker.addImage(image, 0);
		try {
			mediaTracker.waitForID(0);
		}
		catch (InterruptedException e) {
			logger.warning("Interrupted while waiting for splash image to load.");
		}
		frame.setSize(image.getWidth(null), image.getHeight(null));
		center();
		new SplashWindow(frame, image);
	}

	/**
	 * Dispose of the the splash screen. Once disposed, the same splash screen
	 * instance may not be shown again.
	 */
	public void dispose() {
		frame.dispose();
		frame = null;
	}

	private Image loadImage(String path) {
		URL url = this.getClass().getResource(path);
		if (url == null) {
			logger.warning("Unable to locate splash screen in classpath at: " + path);
			return null;
		}
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	private void center() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation((screen.width - r.width) / 2, (screen.height - r.height) / 2);
	}

	private static class SplashWindow extends Window {
		private Image image;

		public SplashWindow(Frame parent, Image image) {
			super(parent);
			this.image = image;
			setSize(parent.getSize());
			setLocation(parent.getLocation());
			setVisible(true);
		}

		public void paint(Graphics graphics) {
			if (image != null) {
				graphics.drawImage(image, 0, 0, this);
			}
		}
	}
}