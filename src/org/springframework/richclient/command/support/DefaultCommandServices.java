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

import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.DefaultButtonConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.DefaultButtonFactory;
import org.springframework.richclient.factory.DefaultMenuFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.image.ArrowIcon;

/**
 * @author Keith Donald
 */
public class DefaultCommandServices implements CommandServices {

    private static final CommandServices INSTANCE = new DefaultCommandServices();

    private static final ArrowIcon PULL_DOWN_ICON = new ArrowIcon(
            ArrowIcon.Direction.DOWN, 3, SystemColor.BLACK);

    private ButtonFactory buttonFactory = new DefaultButtonFactory();

    private MenuFactory menuFactory = new DefaultMenuFactory();

    private CommandButtonConfigurer defaultButtonConfigurer = new DefaultButtonConfigurer();

    private CommandButtonConfigurer toolBarButtonConfigurer = new DefaultButtonConfigurer() {
        public void configure(CommandFaceDescriptor face, AbstractButton button) {
            super.configure(face, button);
            if (button.getIcon() != null) {
                button.setText("");
            }
            button.setMargin(new Insets(2, 5, 2, 5));
        }
    };

    private CommandButtonConfigurer menuItemButtonConfigurer = new DefaultButtonConfigurer() {
        public void configure(CommandFaceDescriptor face, AbstractButton button) {
            super.configure(face, button);
            button.setToolTipText(null);
        }
    };

    private CommandButtonConfigurer pullDownMenuButtonConfigurer = new DefaultButtonConfigurer() {
        public void configure(CommandFaceDescriptor face, AbstractButton button) {
            super.configure(face, button);
            button.setIcon(PULL_DOWN_ICON);
            button.setHorizontalTextPosition(SwingConstants.LEADING);
        }
    };

    private DefaultCommandServices() {

    }

    public static final CommandServices instance() {
        return INSTANCE;
    }

    public ButtonFactory getButtonFactory() {
        return buttonFactory;
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public CommandButtonConfigurer getDefaultButtonConfigurer() {
        return defaultButtonConfigurer;
    }

    public CommandButtonConfigurer getToolBarButtonConfigurer() {
        return toolBarButtonConfigurer;
    }

    public CommandButtonConfigurer getMenuItemButtonConfigurer() {
        return menuItemButtonConfigurer;
    }

    public CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
        return pullDownMenuButtonConfigurer;
    }

}