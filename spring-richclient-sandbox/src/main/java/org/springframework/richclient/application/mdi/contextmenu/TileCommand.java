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

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.springframework.richclient.command.ActionCommand;

/**
 * Tiles all <code>JInternalFrame</code>s in a given <code>JDesktopPane</code>.
 * 
 * @author Peter De Bruycker
 */
public class TileCommand extends ActionCommand {
    private static final String ID = "tileCommand";
    private JDesktopPane desktop;

    public TileCommand( JDesktopPane desktopPane ) {
        super( ID );
        desktop = desktopPane;
    }

    protected void doExecuteCommand() {
        JInternalFrame allFrames[] = desktop.getAllFrames();

        int frameHeight = desktop.getBounds().height / allFrames.length;
        int y = 0;
        for( int i = 0; i < allFrames.length; i++ ) {
            JInternalFrame frame = allFrames[i];
            if( frame.isIcon() ) {
                try {
                    frame.setIcon( false );
                } catch( PropertyVetoException e ) {
                    // ignore
                }
            }

            frame.setSize( desktop.getBounds().width, frameHeight );
            frame.setLocation( 0, y );
            y = y + frameHeight;
        }
    }
}
