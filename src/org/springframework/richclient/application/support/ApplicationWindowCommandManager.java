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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.support.DefaultCommandManager;

public class ApplicationWindowCommandManager extends DefaultCommandManager {
    private List sharedCommands;

    public ApplicationWindowCommandManager() {
        super();
    }

    public ApplicationWindowCommandManager(CommandRegistry parent) {
        super(parent);
    }

    public ApplicationWindowCommandManager(CommandServices commandServices) {
        super(commandServices);
    }

    public void setSharedCommandIds(String[] sharedCommandIds) {
        if (sharedCommandIds.length == 0) {
            sharedCommands = Collections.EMPTY_LIST;
        }
        else {
            this.sharedCommands = new ArrayList(sharedCommandIds.length);
            for (int i = 0; i < sharedCommandIds.length; i++) {
                ActionCommand globalCommand = createTargetableActionCommand(sharedCommandIds[i], null);
                sharedCommands.add(globalCommand);
            }
        }
    }

    public Iterator getSharedCommands() {
        if (sharedCommands == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return sharedCommands.iterator();
    }

}