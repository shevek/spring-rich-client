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

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.springframework.richclient.factory.ButtonConfigurer;

/**
 * @author Keith Donald
 */
public class CommandButtonIconInfo implements ButtonConfigurer {

    private Icon icon;

    private Icon selectedIcon;

    private Icon disabledIcon;

    private Icon pressedIcon;

    private Icon rolloverIcon;

    public CommandButtonIconInfo(Icon icon) {
        this.icon = icon;
    }

    public CommandButtonIconInfo(Icon icon, Icon selectedIcon) {
        this.icon = icon;
        this.selectedIcon = selectedIcon;
    }

    public CommandButtonIconInfo(Icon icon, Icon selectedIcon, Icon rolloverIcon) {
        this.icon = icon;
        this.selectedIcon = selectedIcon;
        this.rolloverIcon = rolloverIcon;
    }

    public CommandButtonIconInfo(Icon icon, Icon selectedIcon,
            Icon rolloverIcon, Icon disabledIcon, Icon pressedIcon) {
        this.icon = icon;
        this.selectedIcon = selectedIcon;
        this.rolloverIcon = rolloverIcon;
        this.disabledIcon = disabledIcon;
        this.pressedIcon = pressedIcon;
    }

    public AbstractButton configure(AbstractButton button) {
        if (button instanceof JMenu) {
            button.setIcon(null);
            button.setSelectedIcon(null);
            button.setDisabledIcon(null);
            button.setPressedIcon(null);
            button.setRolloverIcon(null);
        }
        else if (button instanceof JMenuItem) {
            button.setIcon(icon);
            button.setDisabledIcon(disabledIcon);
        }
        else {
            button.setIcon(icon);
            button.setSelectedIcon(selectedIcon);
            button.setDisabledIcon(disabledIcon);
            button.setPressedIcon(pressedIcon);
            button.setRolloverIcon(rolloverIcon);
        }
        return button;
    }

    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    public Icon getIcon() {
        return icon;
    }

    public Icon getPressedIcon() {
        return pressedIcon;
    }

    public Icon getRolloverIcon() {
        return rolloverIcon;
    }

    public Icon getSelectedIcon() {
        return selectedIcon;
    }

    public void setDisabledIcon(Icon disabledIcon) {
        this.disabledIcon = disabledIcon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setPressedIcon(Icon pressedIcon) {
        this.pressedIcon = pressedIcon;
    }

    public void setRolloverIcon(Icon rolloverIcon) {
        this.rolloverIcon = rolloverIcon;
    }

    public void setSelectedIcon(Icon selectedIcon) {
        this.selectedIcon = selectedIcon;
    }
}