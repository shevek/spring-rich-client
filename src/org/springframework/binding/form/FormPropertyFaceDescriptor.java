/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.richclient.core.DescribedElement;
import org.springframework.richclient.core.VisualizedElement;
import org.springframework.richclient.factory.LabelInfo;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.util.Assert;

/**
 * Provides metadata related to the visualization of a form property and convenience methods
 * for configuring GUI components using the metadata.
 * 
 * @author Oliver Hutchison
 */
public class FormPropertyFaceDescriptor extends AbstractPropertyChangePublisher implements DescribedElement,
        VisualizedElement {

    private final String displayName;

    private final String caption;

    private final String description;

    private final LabelInfo labelInfo;

    private final Icon icon;

    /**
     * Constructs a new FormPropertyFaceDescriptor with the provided values.
     */
    public FormPropertyFaceDescriptor(String displayName, String caption, String description, String encodedLabel, Icon icon) {
        this.displayName = displayName;
        this.caption = caption;
        this.description = description;
        this.labelInfo = LabelInfoFactory.createLabelInfo(encodedLabel);
        this.icon = icon;
    }

    /**
     * The name of the property in human readable form; typically used for validation messages.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * A short caption describing the property.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * A longer caption describing the property; typically used for tool tips.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The text, mnemonic and mnemonicIndex for any labels created for the property.
     */
    public LabelInfo getLabelInfo() {
        return labelInfo;
    }

    public Image getImage() {
        if (getIcon() instanceof ImageIcon) {
            return ((ImageIcon)getIcon()).getImage();
        }
        else {
            return null;
        }
    }

    /**
     * The icon that is used for any labels created for this property.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Configures the supplied JLabel using LabelInfo and Icon.
     */
    public void configure(JLabel label) {
        Assert.notNull(label, "The JLabel to configure is required");
        labelInfo.configureLabel(label);
        label.setIcon(icon);
    }
}