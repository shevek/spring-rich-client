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

import org.springframework.rules.values.ValueListener;
import org.springframework.util.ObjectUtils;

/**
 * @author Keith Donald
 */
public class TargetableActionCommand extends ActionCommand {
    private CommandDelegate commandDelegate;

    private ValueListener guardRelay;

    public TargetableActionCommand() {
        this(null);
    }

    public TargetableActionCommand(String commandId) {
        this(commandId, null);
    }

    public TargetableActionCommand(String commandId, CommandDelegate delegate) {
        super(commandId);
        setEnabled(false);
        setCommandDelegate(delegate);
    }

    public void setCommandDelegate(CommandDelegate delegate) {
        if (ObjectUtils.nullSafeEquals(this.commandDelegate, delegate)) { return; }
        if (delegate == null) {
            removeCommandDelegate();
        }
        else {
            if (this.commandDelegate instanceof GuardedCommandDelegate) {
                unsubscribeFromGuardedCommandDelegate();
            }
            this.commandDelegate = delegate;
            handlerSet();
        }
    }

    private void handlerSet() {
        if (this.commandDelegate instanceof GuardedCommandDelegate) {
            GuardedCommandDelegate dynamicHandler = (GuardedCommandDelegate)commandDelegate;
            setEnabled(dynamicHandler.isEnabled());
            subscribeToGuardedCommandDelegate();
        }
        else {
            setEnabled(true);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Command delegate '" + this.commandDelegate
                    + "' attached.");
        }
    }

    private void subscribeToGuardedCommandDelegate() {
        this.guardRelay = new ValueListener() {
            public void valueChanged() {
                setEnabled(((GuardedCommandDelegate)commandDelegate)
                        .isEnabled());
            }
        };
        ((GuardedCommandDelegate)commandDelegate)
                .addEnabledListener(guardRelay);
    }

    public void removeCommandDelegate() {
        if (this.commandDelegate instanceof GuardedCommandDelegate) {
            unsubscribeFromGuardedCommandDelegate();
        }
        this.commandDelegate = null;
        setEnabled(false);
        logger.debug("Command delegate detached.");
    }

    private void unsubscribeFromGuardedCommandDelegate() {
        ((GuardedCommandDelegate)this.commandDelegate)
                .removeEnabledListener(guardRelay);
    }

    protected void doExecuteCommand() {
        if (commandDelegate instanceof ParameterizedCommandDelegate) {
            ((ParameterizedCommandDelegate)commandDelegate)
                    .execute(getParameters());
        }
        else {
            commandDelegate.execute();
        }
    }

}