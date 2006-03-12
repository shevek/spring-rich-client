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

import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.util.Assert;

public class LazyGroupMember extends GroupMember {
    private CommandGroup parentGroup;

    private String lazyCommandId;

    private boolean addedLazily;

    private GroupMember loadedMember;

    public LazyGroupMember(CommandGroup parentGroup, String lazyCommandId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Lazy group member '" + lazyCommandId + "' instantiated for group '" + parentGroup.getId()
                    + "'");
        }
        this.parentGroup = parentGroup;
        this.lazyCommandId = lazyCommandId;
    }

    public void setEnabled(boolean enabled) {
        if (loadedMember != null) {
            loadedMember.setEnabled(enabled);
        }
    }

    protected void fill(GroupContainerPopulator parentContainerPopulator, Object controlFactory,
            CommandButtonConfigurer buttonConfigurer, List previousButtons) {
        if (lazyLoaded()) {
            loadedMember.fill(parentContainerPopulator, controlFactory, buttonConfigurer, previousButtons);
        }
    }

    private boolean lazyLoaded() {
        if (loadedMember != null) {
            return true;
        }
        doLazyLoad();
        return loadedMember != null;
    }

    private void doLazyLoad() {
        CommandRegistry commandRegistry = parentGroup.getCommandRegistry();

        Assert.isTrue(parentGroup.getCommandRegistry() != null, "Command registry must be set for group '"
                + parentGroup.getId() + "' in order to load lazy command '" + lazyCommandId + "'.");

        if (commandRegistry.containsCommandGroup(lazyCommandId)) {
            CommandGroup group = commandRegistry.getCommandGroup(lazyCommandId);
            loadedMember = new SimpleGroupMember(parentGroup, group);
        }
        else if (commandRegistry.containsActionCommand(lazyCommandId)) {
            ActionCommand command = commandRegistry.getActionCommand(lazyCommandId);
            loadedMember = new SimpleGroupMember(parentGroup, command);
        }
        else {
            logger.warn("Lazy command '" + lazyCommandId
                    + "' was asked to display; however, no backing command instance exists in registry.");
        }

        if (addedLazily && loadedMember != null) {
            loadedMember.onAdded();
        }
    }

    public boolean managesCommand(String commandId) {
        return this.lazyCommandId.equals(commandId);
    }

    protected void onAdded() {
        if (loadedMember != null) {
            loadedMember.onAdded();
        }
        else {
            addedLazily = true;
        }
    }

    protected void onRemoved() {
        if (loadedMember != null) {
            loadedMember.onRemoved();
        }
        else {
            addedLazily = false;
        }
    }

}