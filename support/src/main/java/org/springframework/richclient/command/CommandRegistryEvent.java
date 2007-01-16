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

import java.util.EventObject;

import org.springframework.richclient.util.Assert;

/**
 * An event object that originated from a {@link CommandRegistry}.
 *
 */
public class CommandRegistryEvent extends EventObject {

    private static final long serialVersionUID = 740046110521079234L;
    
    private final AbstractCommand command;

    /**
     * Creates a new {@code CommandRegistryEvent} for the given registry and command.
     *
     * @param source The command registry that originated the event. Must not be null.
     * @param command The command that the event relates to. Must not be null.
     * 
     * @throws IllegalArgumentException if either argument is null.
     */
    public CommandRegistryEvent(CommandRegistry source, AbstractCommand command) {
        super(source);
        Assert.required(source, "source");
        Assert.required(command, "command");
        this.command = command;
    }

    /**
     * Returns the command that the event relates to.
     *
     * @return The command, never null.
     */
    public AbstractCommand getCommand() {
        return command;
    }
    
}
