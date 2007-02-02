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
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;

import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;

/**
 * Helper class to create a context menu for a <code>JDesktopPane</code>. The context menu contains the following items:
 * <ul>
 * <li>tile command</li>
 * <li>cascade command</li>
 * <li>a separator</li>
 * <li>a "show" command for each frame in the desktop pane</li>
 * </ul>
 * 
 * @author Peter De Bruycker
 */
public class DesktopPaneContextMenuFactory {
    private JDesktopPane desktop;

    private CommandManager commandManager;

    public DesktopPaneContextMenuFactory( CommandManager commandManager, JDesktopPane desktopPane ) {
        desktop = desktopPane;
        this.commandManager = commandManager;
    }

    public JPopupMenu getContextMenu() {
        CommandGroup commandGroup = new CommandGroup();

        TileCommand tileCommand = new TileCommand( desktop );
        CascadeCommand cascadeCommand = new CascadeCommand( desktop );

        commandManager.configure( tileCommand );
        commandManager.configure( cascadeCommand );

        commandGroup.add( tileCommand );
        commandGroup.add( cascadeCommand );

        if( desktop.getAllFrames().length > 0 ) {
            commandGroup.addSeparator();
            // TODO try to get the frames in the order they've been added to the desktop
            // pane.
            for( int i = 0; i < desktop.getAllFrames().length; i++ ) {
                JInternalFrame frame = desktop.getAllFrames()[i];

                ShowFrameCommand showFrameCommand = new ShowFrameCommand( frame );
                showFrameCommand.setIcon( frame.getFrameIcon() );
                showFrameCommand.setCaption( "ttt" );

                String label = i + " " + frame.getTitle();
                if( i < 10 ) {
                    label = "&" + label;
                }
                showFrameCommand.setLabel( label );

                commandGroup.add( showFrameCommand );
            }
        }
        return commandGroup.createPopupMenu();
    }
}
