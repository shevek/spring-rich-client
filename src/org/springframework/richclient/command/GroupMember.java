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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.config.CommandButtonConfigurer;

public abstract class GroupMember {

    protected final Log logger = LogFactory.getLog(getClass());

    protected abstract void fill(GroupContainerPopulator parentContainerPopulator, Object controlFactory,
            CommandButtonConfigurer buttonConfigurer, List previousButtons);

    public void setEnabled(boolean enabled) {

    }

    /**
     * Returns <code>true</code> if this member manages a command and its
     * managed command id equals the specified <code>commandId</code>. This
     * method should also traverse nested commands, if the command managed by
     * this member is a <code>CommandGroup</code>.
     * 
     * @param commandId
     *            the command Id
     * @return true or false
     */
    public boolean managesCommand(String commandId) {
        return false;
    }

    public AbstractCommand getCommand() {
        return null;
    }

    protected void addSeparator(GroupContainerPopulator parentContainerPopulator) {
        parentContainerPopulator.addSeparator();
    }

    protected void onAdded() {
    }

    protected void onRemoved() {
    }
}