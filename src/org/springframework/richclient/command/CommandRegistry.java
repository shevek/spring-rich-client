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


public interface CommandRegistry {
    public ActionCommand getActionCommand(String commandId);

    public CommandGroup getCommandGroup(String groupId);

    public boolean containsCommand(String commandId);

    public boolean containsCommandGroup(String groupId);

    public void registerCommand(AbstractCommand command);

    public void registerTargetableActionCommandDelegate(String commandId,
            CommandDelegate delegate);

    public void addCommandRegistryListener(CommandRegistryListener l);

    public void removeCommandRegistryListener(CommandRegistryListener l);
}