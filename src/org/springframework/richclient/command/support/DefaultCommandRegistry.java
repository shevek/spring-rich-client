/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.command.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.command.CommandRegistryEvent;
import org.springframework.richclient.command.CommandRegistryListener;
import org.springframework.richclient.command.TargetableActionCommand;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author Keith Donald
 */
public class DefaultCommandRegistry implements CommandRegistry,
        CommandRegistryListener {
    protected final Log logger = LogFactory.getLog(getClass());

    private List commandRegistryListeners;

    private Map commandRegistry = new HashMap();

    private CommandRegistry parent;

    public DefaultCommandRegistry() {

    }

    public DefaultCommandRegistry(CommandRegistry parent) {
        setParent(parent);
    }

    public void setParent(CommandRegistry parent) {
        if (!ObjectUtils.nullSafeEquals(this.parent, parent)) {
            if (this.parent != null) {
                this.parent.removeCommandRegistryListener(this);
            }
            this.parent = parent;
            if (this.parent != null) {
                this.parent.addCommandRegistryListener(this);
            }
        }
    }

    public void commandRegistered(CommandRegistryEvent event) {
        fireCommandRegistered(event.getCommand());
    }

    public ActionCommand getActionCommand(String commandId) {
        ActionCommand command = (ActionCommand)commandRegistry.get(commandId);
        if (command == null) {
            if (parent != null) {
                command = parent.getActionCommand(commandId);
            }
        }
        if (command == null) {
            logger.warn("No action command with id '" + commandId
                    + "' exists in registry; returning null");
        }
        return command;
    }

    public CommandGroup getCommandGroup(String groupId) {
        CommandGroup group = (CommandGroup)commandRegistry.get(groupId);
        if (group == null) {
            if (parent != null) {
                group = parent.getCommandGroup(groupId);
            }
        }
        if (group == null) {
            logger.warn("No command group with id '" + groupId
                    + "' exists in registry; returning null");
        }
        return group;
    }

    public boolean containsActionCommand(String commandId) {
        if (commandRegistry.containsKey(commandId)) { return true; }
        if (parent != null) { return parent.containsActionCommand(commandId); }
        return false;
    }

    public boolean containsCommandGroup(String groupId) {
        if (commandRegistry.get(groupId) instanceof CommandGroup) { return true; }
        if (parent != null) { return parent.containsCommandGroup(groupId); }
        return false;
    }

    public void registerCommand(AbstractCommand command) {
        Assert.notNull(command, "Command cannot be null.");
        Assert
                .isTrue(command.getId() != null,
                        "A command must have an identifier to be placed in a registry.");
        if (containsActionCommand(command.getId())) {
            if (logger.isWarnEnabled()) {
                logger
                        .warn("This command registry already contains a command with id '"
                                + command.getId() + "'; will overwrite...");
            }
        }
        commandRegistry.put(command.getId(), command);
        if (command instanceof CommandGroup) {
            ((CommandGroup)command).setCommandRegistry(this);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Command registered '" + command.getId() + "'");
        }
        fireCommandRegistered(command);
    }

    protected void fireCommandRegistered(AbstractCommand command) {
        if (commandRegistryListeners == null) { return; }
        CommandRegistryEvent e = null;
        for (Iterator i = commandRegistryListeners.iterator(); i.hasNext();) {
            if (e == null) {
                e = new CommandRegistryEvent(this, command);
            }
            ((CommandRegistryListener)i.next()).commandRegistered(e);
        }
    }

    public void setTargetableActionCommandExecutor(String commandId,
            ActionCommandExecutor delegate) {
        try {
            TargetableActionCommand command = (TargetableActionCommand)getActionCommand(commandId);
            Assert.isTrue(command != null,
                    "No targetable command found with id " + commandId);
            command.setCommandDelegate(delegate);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "Command delegates can only be attached to targetable action commands; commandId '"
                            + commandId
                            + "' is not an instanceof TargetableActionCommand.");
        }
    }

    public void addCommandRegistryListener(CommandRegistryListener l) {
        if (commandRegistryListeners == null) {
            commandRegistryListeners = new ArrayList();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Adding command registry listener " + l);
        }
        commandRegistryListeners.add(l);
    }

    public void removeCommandRegistryListener(CommandRegistryListener l) {
        Assert.notNull(commandRegistryListeners,
                "Listenerlist not yet created; add a listener first!");
        if (logger.isDebugEnabled()) {
            logger.debug("Removing command registry listener " + l);
        }
        commandRegistryListeners.remove(l);
    }

    public CommandRegistry getParent() {
        return parent;
    }

}