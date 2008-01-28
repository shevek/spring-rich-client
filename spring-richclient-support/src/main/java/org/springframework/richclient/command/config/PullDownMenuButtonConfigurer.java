/*
 * Copyright 2002-2007 the original author or authors.
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

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.image.ArrowIcon;

/**
 * <code>CommandButtonConfigurer</code> for pulldown menu buttons.
 * <p>
 * Sets a custom icon (arrow down), and the text is shown before the icon.
 * 
 * @author Keith Donald
 */
public final class PullDownMenuButtonConfigurer extends DefaultCommandButtonConfigurer {
	private static final ArrowIcon PULL_DOWN_ICON = new ArrowIcon(ArrowIcon.Direction.DOWN, 3, Color.BLACK);

	public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
		super.configure(button, command, faceDescriptor);
		button.setIcon(PULL_DOWN_ICON);
		button.setHorizontalTextPosition(SwingConstants.LEADING);
	}
}