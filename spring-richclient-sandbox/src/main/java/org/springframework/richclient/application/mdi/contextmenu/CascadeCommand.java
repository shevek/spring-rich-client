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
 * Cascades all <code>JInternalFrame</code>s in a given <code>JDesktopPane</code>.
 * 
 * @author Peter De Bruycker
 */
public class CascadeCommand extends ActionCommand {
    private static final String ID = "cascadeCommand";

    private JDesktopPane desktop;
    private int offset;
    private boolean resizeFrames;

    public CascadeCommand( JDesktopPane desktopPane ) {
        this( desktopPane, true );
    }

    public CascadeCommand( JDesktopPane desktopPane, int offset ) {
        this( desktopPane, offset, true );
    }

    public CascadeCommand( JDesktopPane desktopPane, boolean resizeFrames ) {
        this( desktopPane, 20, resizeFrames );
    }

    public CascadeCommand( JDesktopPane desktopPane, int offset, boolean resizeFrames ) {
        super( ID );
        desktop = desktopPane;
        this.offset = offset;
        this.resizeFrames = resizeFrames;
    }

    protected void doExecuteCommand() {
        int x = 0;
        int y = 0;
        JInternalFrame allFrames[] = desktop.getAllFrames();

        // manager.setNormalSize();
        int frameHeight = (desktop.getBounds().height - 5) - allFrames.length * offset;
        int frameWidth = (desktop.getBounds().width - 5) - allFrames.length * offset;
        for( int i = allFrames.length - 1; i >= 0; i-- ) {
            JInternalFrame frame = allFrames[i];
            if( frame.isIcon() ) {
                try {
                    frame.setIcon( false );
                } catch( PropertyVetoException e ) {
                    // ignore
                }
            }

            if( resizeFrames ) {
                frame.setSize( frameWidth, frameHeight );
            }
            frame.setLocation( x, y );
            x = x + offset;
            y = y + offset;
        }
    }
}
