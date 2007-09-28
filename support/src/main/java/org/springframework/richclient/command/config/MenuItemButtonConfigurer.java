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

import javax.swing.AbstractButton;

import org.springframework.richclient.command.AbstractCommand;

/**
 * <code>CommandButtonConfigurer</code> implementation for menu items.
 * <p>
 * Sets the tooltip text of menu items to <code>null</code>.
 * 
 * @author Keith Donald
 */
public class MenuItemButtonConfigurer extends DefaultCommandButtonConfigurer {
	public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
		super.configure(button, command, faceDescriptor);
		button.setToolTipText(null);
	}
}