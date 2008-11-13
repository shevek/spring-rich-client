/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.mdi.contextmenu;

import javax.swing.JDesktopPane;

import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;

/**
 * Factory for JDesktopPane CommandGroups:
 * <ul>
 * <li>a Window menu CommandGroup: </li>
 * <li>a context menu CommandGroup: cascade, tile, minimze, separator, all open
 * frames</li>
 * 
 * @author Peter De Bruycker
 */
public interface DesktopCommandGroupFactory {
	CommandGroup createWindowMenuCommandGroup(CommandManager commandManager, JDesktopPane desktopPane);

	CommandGroup createContextMenuCommandGroup(CommandManager commandManager, JDesktopPane desktopPane);
}
