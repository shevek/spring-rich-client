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

import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import org.springframework.core.ToStringCreator;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;

public class SimpleGroupMember extends GroupMember {
    private CommandGroup parent;

    private AbstractCommand command;

    public SimpleGroupMember(CommandGroup parentGroup, AbstractCommand command) {
        this.parent = parentGroup;
        this.command = command;
        if (!parentGroup.isAllowedMember(command))
            throw new IllegalArgumentException("Command: " + command + " is not allowed in group: " + parentGroup);
    }

    public void setEnabled(boolean enabled) {
        command.setEnabled(enabled);
    }

    protected void fill(GroupContainerPopulator parentContainerPopulator, Object controlFactory,
            CommandButtonConfigurer buttonConfigurer, List previousButtons) {
        if (controlFactory instanceof MenuFactory) {
            JMenuItem menu = findMenu(command, previousButtons);
            if (menu == null) {
                menu = command.createMenuItem(((MenuFactory)controlFactory), buttonConfigurer);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Adding menu item to container");
            }
            parentContainerPopulator.add(menu);
        }
        else if (controlFactory instanceof ButtonFactory) {
            AbstractButton button = findButton(command, previousButtons);
            if (button == null) {
                button = command.createButton(((ButtonFactory)controlFactory), buttonConfigurer);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Adding button to container");
            }
            parentContainerPopulator.add(button);
        }
    }

    public boolean managesCommand(String commandId) {
        return this.command.getId().equals(commandId);
    }

    public AbstractCommand getCommand() {
        return command;
    }

    protected JMenuItem findMenu(AbstractCommand command, List previousButtons) {
        for (Iterator it = previousButtons.iterator(); it.hasNext();) {
            AbstractButton button = (AbstractButton)it.next();
            if (button instanceof JMenuItem && command.isAttached(button)) {
                it.remove();
                return (JMenuItem)button;
            }
        }
        return null;
    }

    protected AbstractButton findButton(AbstractCommand command, List previousButtons) {
        for (Iterator it = previousButtons.iterator(); it.hasNext();) {
            AbstractButton button = (AbstractButton)it.next();
            if (!(button instanceof JMenuItem) && command.isAttached(button)) {
                it.remove();
                return button;
            }
        }
        return null;
    }

    protected void onAdded() {
        if (parent instanceof ExclusiveCommandGroup) {
            ((ExclusiveCommandGroup)parent).getSelectionController().add((ToggleCommand)command);
        }
    }

    protected void onRemoved() {
        if (parent instanceof ExclusiveCommandGroup) {
            ((ExclusiveCommandGroup)parent).getSelectionController().remove((ToggleCommand)command);
        }
    }

    public String toString() {
        return new ToStringCreator(this).append("command", command).toString();
    }
}