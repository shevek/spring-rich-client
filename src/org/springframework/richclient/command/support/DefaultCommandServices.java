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

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.DefaultButtonConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.DefaultButtonFactory;
import org.springframework.richclient.factory.DefaultMenuFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.image.ArrowIcon;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class DefaultCommandServices implements CommandServices {

	private static DefaultCommandServices INSTANCE = new DefaultCommandServices();

	private static final ArrowIcon PULL_DOWN_ICON = new ArrowIcon(ArrowIcon.Direction.DOWN, 3, SystemColor.BLACK);

	private ButtonFactory buttonFactory = DefaultButtonFactory.instance();

	private MenuFactory menuFactory = DefaultMenuFactory.instance();

	private CommandButtonConfigurer defaultButtonConfigurer;

	private CommandButtonConfigurer toolBarButtonConfigurer;

	private CommandButtonConfigurer menuItemButtonConfigurer;

	private CommandButtonConfigurer pullDownMenuButtonConfigurer;

	public static DefaultCommandServices instance() {
		return INSTANCE;
	}

	public static void load(DefaultCommandServices instance) {
		Assert.notNull(instance, "The sole default command services instance is required");
		INSTANCE = instance;
	}

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
		return buttonFactory;
	}

	public MenuFactory getMenuFactory() {
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
		return new DefaultButtonConfigurer();
	}

	protected CommandButtonConfigurer createToolBarButtonConfigurer() {
		return new DefaultButtonConfigurer() {
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
		return new DefaultButtonConfigurer() {
			public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
				super.configure(button, command, faceDescriptor);
				button.setToolTipText(null);
			}
		};
	}

	protected CommandButtonConfigurer createPullDownMenuButtonConfigurer() {
		return new DefaultButtonConfigurer() {
			public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
				super.configure(button, command, faceDescriptor);
				button.setIcon(PULL_DOWN_ICON);
				button.setHorizontalTextPosition(SwingConstants.LEADING);
			}
		};
	}

}