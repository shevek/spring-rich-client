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
package org.springframework.richclient.progress;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.factory.ControlFactory;

public class StatusBarCommandGroup extends CommandGroup implements ControlFactory {

    private StatusBar statusBar;

    public StatusBarCommandGroup() {
        super();
    }

    public StatusBarCommandGroup(String groupId) {
        super(groupId);
    }

    public StatusBarCommandGroup(String groupId, CommandRegistry commandRegistry) {
        super(groupId, commandRegistry);
    }

    public JComponent getControl() {
        if (statusBar == null) {
            this.statusBar = createStatusBar();
        }
        return statusBar;
    }

    protected StatusBar createStatusBar() {
        return new StatusBar();
    }

    protected StatusBar getStatusBar() {
        return statusBar;
    }

    public ProgressMonitor getProgressMonitor() {
        return getStatusBar();
    }

    public void setMessage(String message) {
        getStatusBar().setMessage(message);
    }

    public void setMessage(ImageIcon icon, String message) {
        getStatusBar().setMessage(icon, message);
    }

    public void setErrorMessage(String message) {
        getStatusBar().setMessage(message);
    }

    public void setErrorMessage(ImageIcon icon, String message) {
        getStatusBar().setErrorMessage(icon, message);
    }

 public void setVisible(boolean visible) {
 super.setVisible(visible);
 statusBar.setVisible(visible);
 }
}