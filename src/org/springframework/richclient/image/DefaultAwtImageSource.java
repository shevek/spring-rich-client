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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.Assert;
import org.springframework.util.Cache;
import org.springframework.util.DefaultObjectStyler;
import org.springframework.util.ToStringBuilder;

/**
 * A collection of image resources, each indexed by a common key alias.
 * <p>
 * <p>
 * For example, <code>action.edit.copy = /images/edit/copy.gif</code>
 * <p>
 * This class by default performs caching of all loaded image resources using
 * soft references.
 * 
 * @author Keith Donald
 */
public class DefaultAwtImageSource implements AwtImageSource {
    protected static final Log logger = LogFactory
            .getLog(DefaultAwtImageSource.class);

    private Map imageResources;

    class ImageCache extends Cache {
        public ImageCache() {
            super(true);
        }

        public Object create(Object resource) {
            try {
                return ((AwtImageResource)resource).getImage();
            }
            catch (IOException e) {
                throw new DataAccessResourceFailureException(
                        "No image found at resource '" + resource + '"', e);
            }
        }
    }

    private ImageCache imageCache;

    private Image brokenImageIndicator;

    /**
     * Creates a image resource bundle containing the specified map of keys to
     * resource paths. The imageBaseName is prepended to all paths when loading
     * resolved images.
     * 
     * @param imageResources
     *            A map of key-to-image-resources.
     * @param imageBaseName
     *            The basepath to prepend to each resource.
     */
    public DefaultAwtImageSource(Map imageResources) {
        Assert.notNull(imageResources);
        this.imageResources = new HashMap(imageResources);
        debugPrintResources();
        this.imageCache = new ImageCache();
    }

    private void debugPrintResources() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialing image source with resources: "
                    + DefaultObjectStyler.evaluate(this.imageResources));
        }
    }

    protected DefaultAwtImageSource() {

    }

    public Image getImage(String key) {
        Assert.notNull(key);
        AwtImageResource resource = getImageResource(key);
        try {
            return (Image)imageCache.get(resource);
        }
        catch (DataAccessResourceFailureException e) {
            if (brokenImageIndicator != null) { return returnBrokenImageIndicator(resource); }
            throw e;
        }
    }

    public AwtImageResource getImageResource(String key) {
        Assert.notNull(key);
        Resource resource = (Resource)imageResources.get(key);
        if (resource == null) { throw new NoSuchImageResourceException(key); }
        return new AwtImageResource(resource);
    }

    public boolean containsKey(Object key) {
        return imageResources.containsKey(key);
    }

    private Image returnBrokenImageIndicator(Object resource) {
        logger.warn("Unable to load image resource at '" + resource
                + "'; returning the broken image indicator.");
        return brokenImageIndicator;
    }

    /**
     * @see com.csi.commons.ui.image.AwtImageSource#getImageAtLocation(org.springframework.core.io.Resource)
     */
    public Image getImageAtLocation(Resource location) {
        try {
            return new AwtImageResource(location).getImage();
        }
        catch (IOException e) {
            if (brokenImageIndicator == null) { throw new NoSuchImageResourceException(
                    location, e); }
            return returnBrokenImageIndicator(location);
        }
    }

    public int size() {
        return imageResources.size();
    }

    public void setBrokenImageIndicator(Resource resource) {
        try {
            this.brokenImageIndicator = new AwtImageResource(resource)
                    .getImage();
        }
        catch (IOException e) {
            throw new NoSuchImageResourceException(resource, e);
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("imageResources",
                imageResources).toString();
    }

}