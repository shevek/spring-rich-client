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

import java.util.Iterator;
import java.util.List;

import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.util.Assert;

public class InlinedGroupMember extends GroupMember {
    private CommandGroup parent;

    private CommandGroup inlinedGroup;

    public InlinedGroupMember(CommandGroup parentGroup,
            CommandGroup inlinedGroup) {
        Assert.notNull(inlinedGroup);
        this.parent = parentGroup;
        this.inlinedGroup = inlinedGroup;
        if (!parentGroup.isAllowedMember(inlinedGroup))
            throw new IllegalArgumentException("Inlined group " + inlinedGroup
                    + " is not allowed in group " + parentGroup);
    }

    public void setEnabled(boolean enabled) {
        inlinedGroup.setEnabled(enabled);
    }

    protected void fill(GroupContainerPopulator parentContainerPopulator,
            Object controlFactory, CommandButtonConfigurer buttonConfigurer,
            List previousButtons) {
        for (Iterator i = inlinedGroup.memberIterator(); i.hasNext();) {
            GroupMember member = (GroupMember)i.next();
            member.fill(parentContainerPopulator, controlFactory,
                    buttonConfigurer, previousButtons);
        }
    }

    public boolean managesCommandDirectly(String commandId) {
        return this.inlinedGroup.getId().equals(commandId);
    }

    public boolean managesCommand(String commandId) {
        if (this.inlinedGroup.getId().equals(commandId)) { return true; }
        return inlinedGroup.contains(commandId);
    }

}