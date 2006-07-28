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
package org.springframework.richclient.command;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.springframework.richclient.command.config.CommandFaceDescriptor;

public class SwingActionAdapter extends AbstractAction implements PropertyChangeListener {
    private ActionCommand command;

    public SwingActionAdapter(ActionCommand command) {
        super();
        this.command = command;
        command.addPropertyChangeListener(this);
        command.addEnabledListener(this);
        update();
    }

    public void actionPerformed(ActionEvent e) {
        command.actionPerformedHandler.actionPerformed(e);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        update();
    }

    protected void update() {
        putValue(Action.ACTION_COMMAND_KEY, command.getActionCommand());
        CommandFaceDescriptor face = command.getFaceDescriptor();
        if (face != null) {
            face.configure(this);
        }
        setEnabled(command.isEnabled());
    }

}