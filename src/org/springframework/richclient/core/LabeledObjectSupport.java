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
package org.springframework.richclient.core;

import java.awt.Image;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.factory.LabelInfo;
import org.springframework.richclient.image.config.IconConfigurable;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.util.ToStringBuilder;

/**
 * A conveinet super class for objects that can be labeled for display in a GUI.
 * 
 * @author Keith Donald
 */
public class LabeledObjectSupport implements ManagedElement, LabelConfigurable,
        ImageConfigurable, IconConfigurable, DescriptionConfigurable,
        TitleConfigurable {

    private static final Log logger = LogFactory
            .getLog(LabeledObjectSupport.class);

    private String title;

    private LabelInfo label;

    private String caption;

    private String description;

    private Icon icon;

    private Image image;

    /**
     * @see org.springframework.richclient.core.TitleConfigurable#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @see org.springframework.richclient.core.LabelConfigurable#setLabel(org.springframework.rcp.factory.LabelInfo)
     */
    public void setLabel(LabelInfo label) {
        this.label = label;
    }

    /**
     * @see org.springframework.richclient.core.DescriptionConfigurable#setCaption(java.lang.String)
     */
    public void setCaption(String shortDescription) {
        this.caption = shortDescription;
    }

    /**
     * @see org.springframework.richclient.core.DescriptionConfigurable#setDescription(java.lang.String)
     */
    public void setDescription(String longDescription) {
        this.description = longDescription;
    }

    /**
     * @see org.springframework.richclient.image.config.IconConfigurable#setIcon(Icon)
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * @see org.springframework.richclient.image.config.ImageConfigurable#setImage(java.awt.Image)
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @see org.springframework.richclient.core.ManagedElement#getDisplayName()
     */
    public final String getDisplayName() {
        if (title != null) {
            return title;
        }
        else {
            if (label == null) {
                logger.warn("This labeled object's label is not configured.");
                return "displayName";
            }
            return label.getText();
        }
    }

    /**
     * @see org.springframework.richclient.core.ManagedElement#getCaption()
     */
    public final String getCaption() {
        return caption;
    }

    /**
     * @see org.springframework.richclient.core.ManagedElement#getDescription()
     */
    public final String getDescription() {
        return description;
    }

    public Icon getIcon() {
        return icon;
    }

    public Image getImage() {
        return image;
    }

    protected LabelInfo getLabel() {
        return label;
    }

    public String toString() {
        return new ToStringBuilder(this).appendProperties().toString();
    }

}