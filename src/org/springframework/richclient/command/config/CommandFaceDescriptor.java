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
package org.springframework.richclient.command.config;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;

import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public class CommandFaceDescriptor extends AbstractPropertyChangePublisher
        implements CommandButtonLabelConfigurable, DescriptionConfigurable,
        CommandButtonIconInfoConfigurable {

    public static final String COMMAND_BUTTON_LABEL_PROPERTY = "labelInfo";

    public static final String COMMAND_BUTTON_ICON_PROPERTY = "iconInfo";

    public static final String CAPTION_PROPERTY = "caption";

    public static final CommandButtonLabelInfo EMPTY_LABEL = new CommandButtonLabelInfo(
            "commandLabel");

    public static final CommandButtonIconInfo EMPTY_ICON = new CommandButtonIconInfo(
            null);

    private String caption;

    private String description;

    private CommandButtonLabelInfo labelInfo;

    private CommandButtonIconInfo iconInfo = EMPTY_ICON;

    public CommandFaceDescriptor() {
        this(EMPTY_LABEL);
    }

    public CommandFaceDescriptor(String encodedLabel) {
        this(encodedLabel, null, null);
    }

    public CommandFaceDescriptor(String encodedLabel, Icon icon, String caption) {
        if (StringUtils.hasText(encodedLabel)) {
            this.labelInfo = LabelInfoFactory
                    .createButtonLabelInfo(encodedLabel);
        }
        else {
            this.labelInfo = EMPTY_LABEL;
        }
        if (icon != null) {
            this.iconInfo = new CommandButtonIconInfo(icon);
        }
        this.caption = caption;
    }

    public CommandFaceDescriptor(CommandButtonLabelInfo labelInfo) {
        Assert.notNull(labelInfo);
        this.labelInfo = labelInfo;
    }

    public boolean isEmpty() {
        return labelInfo == EMPTY_LABEL;
    }

    public String getText() {
        return labelInfo.getText();
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public CommandButtonLabelInfo getButtonLabelInfo() {
        return labelInfo;
    }

    public CommandButtonIconInfo getButtonIconInfo() {
        return iconInfo;
    }

    public void setCaption(String shortDescription) {
        if (hasChanged(this.caption, shortDescription)) {
            String old = this.caption;
            this.caption = shortDescription;
            firePropertyChange(CAPTION_PROPERTY, old, this.caption);
        }
    }

    public void setDescription(String longDescription) {
        this.description = longDescription;
    }

    public void setCommandButtonLabelInfo(String encodedLabelInfo) {
        setCommandButtonLabelInfo(LabelInfoFactory
                .createButtonLabelInfo(encodedLabelInfo));
    }

    public void setCommandButtonLabelInfo(CommandButtonLabelInfo labelInfo) {
        if (hasChanged(this.labelInfo, labelInfo)) {
            if (labelInfo == null) {
                labelInfo = EMPTY_LABEL;
            }
            CommandButtonLabelInfo old = this.labelInfo;
            this.labelInfo = labelInfo;
            firePropertyChange(COMMAND_BUTTON_LABEL_PROPERTY, old,
                    this.labelInfo);
        }
    }

    public void setCommandButtonIconInfo(CommandButtonIconInfo iconInfo) {
        if (hasChanged(this.iconInfo, iconInfo)) {
            CommandButtonIconInfo old = this.iconInfo;
            this.iconInfo = iconInfo;
            firePropertyChange(COMMAND_BUTTON_ICON_PROPERTY, old, this.iconInfo);
        }
    }

    public void setIcon(Icon icon) {
        if (getButtonIconInfo() == null || getButtonIconInfo() == EMPTY_ICON) {
            if (icon != null) {
                setCommandButtonIconInfo(new CommandButtonIconInfo(icon));
            }
        }
        else {
            Icon old = iconInfo.getIcon();
            if (hasChanged(old, icon)) {
                this.iconInfo.setIcon(icon);
                firePropertyChange(COMMAND_BUTTON_ICON_PROPERTY, old,
                        this.iconInfo);
            }
        }
    }

    public void configure(AbstractButton button,
            CommandButtonConfigurer strategy) {
        strategy.configure(this, button);
    }

    public void configure(Action action) {
        action.putValue(AbstractAction.NAME, getButtonLabelInfo().getText());
        action.putValue(AbstractAction.MNEMONIC_KEY, new Integer(
                getButtonLabelInfo().getMnemonic()));
        if (getButtonIconInfo() != null) {
            action.putValue(AbstractAction.SMALL_ICON, getButtonIconInfo()
                    .getIcon());
        }
        action.putValue(AbstractAction.ACCELERATOR_KEY, getButtonLabelInfo()
                .getAccelerator());
        action.putValue(AbstractAction.SHORT_DESCRIPTION, caption);
        action.putValue(AbstractAction.LONG_DESCRIPTION, description);
    }

}