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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.richclient.command.config.CommandButtonConfigurer;

public class ExpansionPointGroupMember extends GroupMember {
    private static final String DEFAULT_EXPANSION_POINT_NAME = "default";

    private HashSet members = new LinkedHashSet(5);

    private String expansionGroupName;

    private boolean leadingSeparator;

    private boolean endingSeparator;

    protected ExpansionPointGroupMember() {
        expansionGroupName = DEFAULT_EXPANSION_POINT_NAME;
    }

    protected ExpansionPointGroupMember(String name) {
        this.expansionGroupName = name;
    }

    public boolean isLeadingSeparator() {
        return leadingSeparator;
    }

    public void setLeadingSeparator(boolean leadingSeparator) {
        this.leadingSeparator = leadingSeparator;
    }

    public boolean isEndingSeparator() {
        return endingSeparator;
    }

    public void setEndingSeparator(boolean endingSeparator) {
        this.endingSeparator = endingSeparator;
    }

    public String getExpansionGroupName() {
        return expansionGroupName;
    }

    protected void add(GroupMember member) {
        if (members.add(member)) {
            member.onAdded();
        }
    }

    public void remove(GroupMember member) {
        if (members.remove(member)) {
            member.onRemoved();
        }
    }

    protected void clear() {
        members.clear();
    }

    protected void fill(GroupContainerPopulator parent, Object factory,
            CommandButtonConfigurer configurer, List previousButtons) {
        if (members.size() > 0 && isLeadingSeparator()) {
            addSeparator(parent);
        }

        for (Iterator iterator = members.iterator(); iterator.hasNext();) {
            GroupMember member = (GroupMember)iterator.next();
            member.fill(parent, factory, configurer, previousButtons);
        }

        if (members.size() > 0 && isEndingSeparator()) {
            addSeparator(parent);
        }
    }

    public boolean managesCommandDirectly(String commandId) {
        return getMemberFor(commandId) != null;
    }

    public GroupMember getMemberFor(String commandId) {
        for (Iterator it = members.iterator(); it.hasNext();) {
            GroupMember member = (GroupMember)it.next();
            if (member.managesCommand(commandId)) { return member; }
        }
        return null;
    }

    public boolean managesCommand(String commandId) {
        for (Iterator iterator = members.iterator(); iterator.hasNext();) {
            GroupMember member = (GroupMember)iterator.next();
            if (member.managesCommand(commandId))
                return true;
        }

        return false;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

}