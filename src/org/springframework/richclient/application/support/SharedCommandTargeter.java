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

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewContext;
import org.springframework.richclient.command.TargetableActionCommand;
import org.springframework.util.Assert;

/**
 * Retargets global command when the active View associated with an
 * ApplicationPage changes.
 * 
 * @author Keith Donald
 */
public class SharedCommandTargeter extends AbstractViewListener {
    private ApplicationWindow window;

    public SharedCommandTargeter(ApplicationWindow window) {
        Assert.notNull(window,
                "The application window with shared commands is required");
        this.window = window;
    }

    public void viewFocusGained(View view) {
        super.viewFocusGained(view);
        ViewContext viewContext = view.getContext();
        for (Iterator i = window.getSharedCommands(); i.hasNext();) {
            TargetableActionCommand globalCommand = (TargetableActionCommand)i
                    .next();
            globalCommand.setCommandExecutor(viewContext
                    .getSharedCommandExecutor(globalCommand.getId()));
        }
    }

}