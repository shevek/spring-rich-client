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
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.config.CommandButtonLabelConfigurable;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.util.ToStringBuilder;

/**
 * A convenient super class for objects that can be labeled for display in a
 * GUI.
 * 
 * @author Keith Donald
 */
public class LabeledObjectSupport implements ManagedElement,
        CommandButtonLabelConfigurable, ImageConfigurable,
        DescriptionConfigurable, TitleConfigurable {
    protected final Log logger = LogFactory.getLog(getClass());

    private CommandButtonLabelInfo label;

    private String title;

    private String caption;

    private String description;

    private Image image;

    public void setCommandButtonLabelInfo(CommandButtonLabelInfo label) {
        this.label = label;
    }

    public void setCaption(String shortDescription) {
        this.caption = shortDescription;
    }

    public void setDescription(String longDescription) {
        this.description = longDescription;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDisplayName() {
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

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public Image getImage() {
        return image;
    }

    public Icon getImageIcon() {
        if (image != null) {
            return new ImageIcon(image);
        }
        else {
            return null;
        }
    }

    public int getMnemonic() {
        if (label != null) {
            return label.getMnemonic();
        }
        else {
            return 0;
        }
    }

    public int getMnemonicIndex() {
        if (label != null) {
            return label.getMnemonicIndex();
        }
        else {
            return 0;
        }
    }

    public KeyStroke getAccelerator() {
        if (label != null) {
            return label.getAccelerator();
        }
        else {
            return null;
        }
    }

    protected CommandButtonLabelInfo getLabel() {
        return label;
    }

    public String toString() {
        return new ToStringBuilder(this).appendProperties().toString();
    }

}