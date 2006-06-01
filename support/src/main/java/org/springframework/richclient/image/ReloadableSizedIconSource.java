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
package org.springframework.richclient.image;

import java.awt.Image;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.springframework.util.Assert;

/**
 * Default implementation of a reloadable sized icon registry. Icons by default
 * are loaded by appending registry's current iconSize name to the iconKey as
 * follows:
 * <p>
 * 
 * <pre>
 * 
 *  
 *    &lt;iconKey&gt;.&lt;iconSize&gt;
 *   
 *  
 * </pre>
 * 
 * <p>
 * For example:
 * <p>
 * 
 * <pre>
 * 
 *  
 *    action.edit.copy.small=/images/edit/copy16.gif
 *   
 *  
 * </pre>
 * 
 * @author Keith Donald
 */
public class ReloadableSizedIconSource extends DefaultIconSource implements SizedIconSource {
    private IconSize iconSize;

    /**
     * Create a sized icon registry with icons of a specified size and icon
     * resources to be loaded from the specified image source.
     * 
     * @param iconSize
     *            the size of icons in this registry
     * @param iconResources
     *            The image icon source
     */
    public ReloadableSizedIconSource(IconSize iconSize, ImageSource iconResources) {
        super(iconResources);
        Assert.notNull(iconSize);
        this.iconSize = iconSize;
    }

    public void reload(IconSize size) {
        Assert.notNull(size);
        this.iconSize = size;
        Iterator keys = cache().keySet().iterator();
        if (!keys.hasNext()) {
            logger.warn("No icons currently in the registry--nothing to reload.");
            return;
        }
        while (keys.hasNext()) {
            reloadIconImage((String)keys.next());
        }
    }

    // reloads the specified image resource key and update the cached icon
    private void reloadIconImage(String key) {
        ImageIcon icon = (ImageIcon)cache().get(key);
        if (icon != null) {
            Image image = cache().images().getImage(appendIconSizeSuffix(key));
            icon.setImage(image);
        }
    }

    private String appendIconSizeSuffix(String key) {
        if (iconSize == null) 
            return key;

        logger.debug("Appending icon suffix '." + iconSize.getName() + "'");
        return key + "." + iconSize.getName();
    }

    protected String doProcessImageKeyBeforeLookup(String key) {
        return appendIconSizeSuffix(key);
    }

}