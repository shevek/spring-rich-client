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
package org.springframework.richclient.command;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.util.ObjectUtils;

/**
 * @author Keith Donald
 */
public class TargetableActionCommand extends ActionCommand {
    private ActionCommandExecutor commandExecutor;

    private ValueChangeListener guardRelay;

    public TargetableActionCommand() {
        this(null);
    }

    public TargetableActionCommand(String commandId) {
        this(commandId, null);
    }

    public TargetableActionCommand(String commandId,
            ActionCommandExecutor delegate) {
        super(commandId);
        setEnabled(false);
        setCommandExecutor(delegate);
    }

    public void setCommandExecutor(ActionCommandExecutor commandExecutor) {
        if (ObjectUtils.nullSafeEquals(this.commandExecutor, commandExecutor)) { return; }
        if (commandExecutor == null) {
            detachCommandExecutor();
        }
        else {
            if (this.commandExecutor instanceof GuardedActionCommandExecutor) {
                unsubscribeFromGuardedCommandDelegate();
            }
            this.commandExecutor = commandExecutor;
            attachCommandExecutor();
        }
    }

    private void attachCommandExecutor() {
        if (this.commandExecutor instanceof GuardedActionCommandExecutor) {
            GuardedActionCommandExecutor dynamicHandler = (GuardedActionCommandExecutor)commandExecutor;
            setEnabled(dynamicHandler.isEnabled());
            subscribeToGuardedCommandDelegate();
        }
        else {
            setEnabled(true);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Command delegate '" + this.commandExecutor
                    + "' attached.");
        }
    }

    private void subscribeToGuardedCommandDelegate() {
        this.guardRelay = new ValueChangeListener() {
            public void valueChanged() {
                setEnabled(((GuardedActionCommandExecutor)commandExecutor)
                        .isEnabled());
            }
        };
        ((GuardedActionCommandExecutor)commandExecutor)
                .addEnabledListener(guardRelay);
    }

    public void detachCommandExecutor() {
        if (this.commandExecutor instanceof GuardedActionCommandExecutor) {
            unsubscribeFromGuardedCommandDelegate();
        }
        this.commandExecutor = null;
        setEnabled(false);
        logger.debug("Command delegate detached.");
    }

    private void unsubscribeFromGuardedCommandDelegate() {
        ((GuardedActionCommandExecutor)this.commandExecutor)
                .removeEnabledListener(guardRelay);
    }

    protected void doExecuteCommand() {
        if (commandExecutor instanceof ParameterizableActionCommandExecutor) {
            ((ParameterizableActionCommandExecutor)commandExecutor)
                    .execute(getParameters());
        }
        else {
            commandExecutor.execute();
        }
    }

}