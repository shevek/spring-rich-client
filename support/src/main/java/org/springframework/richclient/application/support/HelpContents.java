/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.application.support;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.util.WindowUtils;

/**
 * A simple implementation of a help contents frame for an application using
 * javahelp.
 * 
 * @author Keith Donald
 */
public class HelpContents {

    private Resource helpSetPath = new ClassPathResource("help/helpset.hs");

    private JFrame helpFrame;

    public HelpContents() {

    }

    public void setHelpSetPath(Resource helpSetPath) {
        this.helpSetPath = helpSetPath;
    }

    protected String getApplicationName() {
        return Application.instance().getName();
    }

    protected Image getApplicationImage() {
        return Application.instance().getImage();
    }

    public void display(Window parent) {
        if (helpFrame == null) {
            helpFrame = new JFrame();
            helpFrame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                HelpSet helpSet = new HelpSet(null, helpSetPath.getURL());
                JHelp jhelp = new JHelp(helpSet);
                helpFrame = new JFrame("Help - " + getApplicationName());
                helpFrame.getContentPane().add(jhelp);
                helpFrame.setIconImage(getApplicationImage());
                helpFrame.pack();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            helpFrame.getGlassPane().setCursor(Cursor.getDefaultCursor());
        }
        if (!helpFrame.isVisible()) {
        	WindowUtils.centerOnParent(helpFrame, parent);
            helpFrame.setVisible(true);
        }
        if ((helpFrame.getExtendedState() & Frame.NORMAL) == 0) {
            helpFrame.setExtendedState(Frame.NORMAL);
        }
        helpFrame.toFront();
    }

}