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
package org.springframework.richclient.command.support;

import org.springframework.richclient.command.ActionCommand;

/**
 * @author Peter De Bruycker
 */
public class AdditionalStateTestCommand extends ActionCommand {

    private boolean myenabledState = true;
    
    private boolean myvisibleState = true;

    public AdditionalStateTestCommand() {
    }

    public AdditionalStateTestCommand(String id) {
        super(id);
    }

    /**
     * @see org.springframework.richclient.command.ActionCommand#doExecuteCommand()
     */
    protected void doExecuteCommand() {
    }

    public boolean isMyenabledState() {
        return myenabledState;
    }

    public boolean isEnabled() {
        return super.isEnabled() && isMyenabledState();
    }
    
    public void setMyenabledState(boolean myenabledState) {
        if (hasChanged(myenabledState, isMyenabledState())) {
            this.myenabledState = myenabledState;
            updatedEnabledState();
        }
    }
    
    public boolean isVisible() {
        return super.isVisible() && isMyvisibleState();
    }

    public boolean isMyvisibleState() {
        return myvisibleState;
    }

    public void setMyvisibleState(boolean myvisibleState) {
        if (hasChanged(myvisibleState, isMyvisibleState())) {
            this.myvisibleState = myvisibleState;
            updatedVisibleState();
        }
    }
}