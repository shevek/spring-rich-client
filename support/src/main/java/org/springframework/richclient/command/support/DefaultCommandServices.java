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
package org.springframework.richclient.command.support;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.DefaultCommandButtonConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.image.ArrowIcon;

/**
 * @author Keith Donald
 */
public class DefaultCommandServices implements CommandServices {

    private static final ArrowIcon PULL_DOWN_ICON = new ArrowIcon(ArrowIcon.Direction.DOWN, 3, Color.BLACK);

    private ButtonFactory buttonFactory;

    private MenuFactory menuFactory;

    private CommandButtonConfigurer defaultButtonConfigurer;

    private CommandButtonConfigurer toolBarButtonConfigurer;

    private CommandButtonConfigurer menuItemButtonConfigurer;

    private CommandButtonConfigurer pullDownMenuButtonConfigurer;

    public void setButtonFactory(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public void setDefaultButtonConfigurer(CommandButtonConfigurer defaultButtonConfigurer) {
        this.defaultButtonConfigurer = defaultButtonConfigurer;
    }

    public void setToolBarButtonConfigurer(CommandButtonConfigurer toolBarButtonConfigurer) {
        this.toolBarButtonConfigurer = toolBarButtonConfigurer;
    }

    public void setMenuItemButtonConfigurer(CommandButtonConfigurer menuItemButtonConfigurer) {
        this.menuItemButtonConfigurer = menuItemButtonConfigurer;
    }

    public void setPullDownMenuButtonConfigurer(CommandButtonConfigurer pullDownMenuButtonConfigurer) {
        this.pullDownMenuButtonConfigurer = pullDownMenuButtonConfigurer;
    }

    public ButtonFactory getButtonFactory() {
        if(buttonFactory == null) {
            buttonFactory = (ButtonFactory) ApplicationServicesLocator.services().getService(ButtonFactory.class);
        }
        return buttonFactory;
    }

    public MenuFactory getMenuFactory() {
        if(menuFactory == null) {
            menuFactory = (MenuFactory) ApplicationServicesLocator.services().getService(MenuFactory.class);
        }
        return menuFactory;
    }

    public CommandButtonConfigurer getDefaultButtonConfigurer() {
        if (defaultButtonConfigurer == null) {
            defaultButtonConfigurer = createDefaultButtonConfigurer();
        }
        return defaultButtonConfigurer;
    }

    public CommandButtonConfigurer getToolBarButtonConfigurer() {
        if (toolBarButtonConfigurer == null) {
            toolBarButtonConfigurer = createToolBarButtonConfigurer();
        }
        return toolBarButtonConfigurer;
    }

    public CommandButtonConfigurer getMenuItemButtonConfigurer() {
        if (menuItemButtonConfigurer == null) {
            menuItemButtonConfigurer = createMenuItemButtonConfigurer();
        }
        return menuItemButtonConfigurer;
    }

    public CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
        if (pullDownMenuButtonConfigurer == null) {
            pullDownMenuButtonConfigurer = createPullDownMenuButtonConfigurer();
        }
        return pullDownMenuButtonConfigurer;
    }

    protected CommandButtonConfigurer createDefaultButtonConfigurer() {
        return new DefaultCommandButtonConfigurer();
    }

    protected CommandButtonConfigurer createToolBarButtonConfigurer() {
        return new DefaultCommandButtonConfigurer() {
            public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
                super.configure(button, command, faceDescriptor);
                if (button.getIcon() != null) {
                    button.setText("");
                }
                button.setMargin(new Insets(2, 5, 2, 5));
            }
        };
    }

    protected CommandButtonConfigurer createMenuItemButtonConfigurer() {
        return new DefaultCommandButtonConfigurer() {
            public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
                super.configure(button, command, faceDescriptor);
                button.setToolTipText(null);
            }
        };
    }

    protected CommandButtonConfigurer createPullDownMenuButtonConfigurer() {
        return new DefaultCommandButtonConfigurer() {
            public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
                super.configure(button, command, faceDescriptor);
                button.setIcon(PULL_DOWN_ICON);
                button.setHorizontalTextPosition(SwingConstants.LEADING);
            }
        };
    }

}