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
package org.springframework.richclient.image;

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapTemplate;
import org.springframework.util.Styler;
import org.springframework.util.ToStringCreator;

/**
 * A collection of image resources, each indexed by a common key alias.
 * <p>
 * For example, <code>action.edit.copy = /images/edit/copy.gif</code>
 * <p>
 * This class by default performs caching of all loaded image resources using
 * soft references.
 * 
 * @author Keith Donald
 */
public class DefaultImageSource implements ImageSource {
	protected static final Log logger = LogFactory.getLog(DefaultImageSource.class);

	private Map imageResources;

	private ImageCache imageCache;

	private AwtImageResource brokenImageIndicatorResource;

	private Image brokenImageIndicator;

	/**
	 * Creates a image resource bundle containing the specified map of keys to
	 * resource paths.
	 * <p>
	 * A custom URL protocol {@link Handler handler}will be installed for the
	 * "image:" protocol. This allows for images in this image source to be
	 * located using the Java URL classes: <br>
	 * <code>URL imageUrl = new URL("image:the.image.key")</code>
	 * 
	 * @param imageResources a map of key-to-image-resources.
	 */
	public DefaultImageSource(Map imageResources) {
		this(true, imageResources);
	}

	/**
	 * Creates a image resource bundle containing the specified map of keys to
	 * resource paths.
	 * 
	 * @param installUrlHandler should a URL handler be installed.
	 * @param imageResources a map of key-to-image-resources.
	 */
	public DefaultImageSource(boolean installUrlHandler, Map imageResources) {
		Assert.notNull(imageResources);
		this.imageResources = new HashMap(imageResources);
		debugPrintResources();
		this.imageCache = new ImageCache();
		if (installUrlHandler) {
			Handler.installImageUrlHandler(this);
		}
	}

	private void debugPrintResources() {
		if (logger.isDebugEnabled()) {
			logger.debug("Initialing image source with resources: " + Styler.call(this.imageResources));
		}
	}

	public Image getImage(String key) {
		Assert.notNull(key);
		AwtImageResource resource = getImageResource(key);
		try {
			return (Image)imageCache.get(resource);
		}
		catch (RuntimeException e) {
			if (brokenImageIndicator != null) {
				return returnBrokenImageIndicator(resource);
			}
			throw e;
		}
	}

	public AwtImageResource getImageResource(String key) {
		Assert.notNull(key);
		Resource resource = (Resource)imageResources.get(key);
		if (resource == null) {
			throw new NoSuchImageResourceException(key);
		}
		try {
			resource.getInputStream();
			return new AwtImageResource(resource);
		}
		catch (IOException e) {
			if (brokenImageIndicatorResource == null) {
				throw new NoSuchImageResourceException(resource, e);
			}
			logger.warn("Unable to load image resource at '" + resource + "'; returning the broken image indicator.");
			return brokenImageIndicatorResource;
		}
	}

	public boolean containsKey(Object key) {
		return imageResources.containsKey(key);
	}

	private Image returnBrokenImageIndicator(Resource resource) {
		logger.warn("Unable to load image resource at '" + resource + "'; returning the broken image indicator.");
		return brokenImageIndicator;
	}

	public Image getImageAtLocation(Resource location) {
		try {
			return new AwtImageResource(location).getImage();
		}
		catch (IOException e) {
			if (brokenImageIndicator == null) {
				throw new NoSuchImageResourceException(location, e);
			}
			return returnBrokenImageIndicator(location);
		}
	}

	public int size() {
		return imageResources.size();
	}

	public void setBrokenImageIndicator(Resource resource) {
		try {
			brokenImageIndicatorResource = new AwtImageResource(resource);
			brokenImageIndicator = brokenImageIndicatorResource.getImage();
		}
		catch (IOException e) {
			brokenImageIndicatorResource = null;
			throw new NoSuchImageResourceException(resource, e);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("imageResources", imageResources).toString();
	}

	private static class ImageCache extends CachingMapTemplate {
		public ImageCache() {
			super(true);
		}

		public Object create(Object resource) {
			try {
				return ((AwtImageResource)resource).getImage();
			}
			catch (IOException e) {
				throw new NoSuchImageResourceException("No image found at resource '" + resource + '"', e);
			}
		}
	}
}
