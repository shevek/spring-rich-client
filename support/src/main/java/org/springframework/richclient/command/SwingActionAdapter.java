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
import org.springframework.richclient.util.Assert;

/**
 * An adapter between a Spring Rich Client {@link ActionCommand} and the Swing 
 * {@link Action} interface. 
 * 
 * <p>
 * This adheres to the standard GoF {@code Adapter} pattern whereby this class acts as 
 * a wrapper around an underlying {@link ActionCommand} to give it the appearance of 
 * being an {@link Action}. 
 * </p>
 * 
 * <p>
 * The {@link PropertyChangeListener} interface is also implemented so that 
 * instances can be notified of property change events being fired by their underlying command.
 * </p>
 */
public class SwingActionAdapter extends AbstractAction implements PropertyChangeListener {
    
    private ActionCommand command;

    /**
     * Creates a new {@code SwingActionAdapter} with the given underlying action command. The 
     * newly created instance will add itself as a property change listener of the command.
     *
     * @param command The underlying action command.
     * 
     * @throws IllegalArgumentException if {@code command} is null.
     */
    public SwingActionAdapter(ActionCommand command) {
        super();
        
        Assert.required(command, "command");
        this.command = command;
        command.addPropertyChangeListener(this);
        command.addEnabledListener(this);
        update();
        
    }

    /**
     * Delegates the handling of the given event to the underlying command.
     * @param event The action event to be handled.
     */
    public void actionPerformed(ActionEvent event) {
        command.actionPerformedHandler.actionPerformed(event);
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent event) {
        update();
    }

    /**
     * Updates this instance according to the properties provided by the 
     * underlying command. 
     */
    protected void update() {
        putValue(Action.ACTION_COMMAND_KEY, command.getActionCommand());
        CommandFaceDescriptor face = command.getFaceDescriptor();
        if (face != null) {
            face.configure(this);
        }
        setEnabled(command.isEnabled());
    }

}