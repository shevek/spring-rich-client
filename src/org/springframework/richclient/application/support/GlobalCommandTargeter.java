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

import java.util.Iterator;

import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewContext;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.command.TargetableActionCommand;
import org.springframework.util.Assert;

/**
 * Retargets global command when the active View associated with an
 * ApplicationPage changes.
 * 
 * @author Keith Donald
 */
public class GlobalCommandTargeter extends AbstractViewListener {
    private CommandManager globalCommandRegistry;

    public GlobalCommandTargeter(CommandManager commandRegistry) {
        Assert.notNull(commandRegistry, "Command manager is required");
        this.globalCommandRegistry = commandRegistry;
    }

    public void viewFocusGained(View view) {
        super.viewFocusGained(view);
        ViewContext viewContext = view.getContext();
        for (Iterator i = globalCommandRegistry.getGlobalCommands(); i
                .hasNext();) {
            TargetableActionCommand globalCommand = (TargetableActionCommand)i
                    .next();
            globalCommand.setCommandExecutor(viewContext
                    .findGlobalCommandDelegate(globalCommand.getId()));
        }
    }

}