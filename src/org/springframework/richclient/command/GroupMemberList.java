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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.config.CommandButtonConfigurer;

public class GroupMemberList {
    private static final Log logger = LogFactory.getLog(GroupMemberList.class);

    private List members = new ArrayList(9);

    private Map builders = new WeakHashMap(6);

    private ExpansionPointGroupMember expansionPoint;

    public GroupMemberList() {

    }

    public void add(GroupMember member) {
        if (members.add(member)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Member '" + member + "' added to memberList");
            }
            member.onAdded();
        }
    }

    public void append(GroupMember member) {
        getExpansionPoint().add(member);
    }

    public ExpansionPointGroupMember getExpansionPoint() {
        if (expansionPoint == null) {
            expansionPoint = new ExpansionPointGroupMember();
            add(expansionPoint);
        }
        return expansionPoint;
    }

    public int size() {
        return members.size();
    }

    public Iterator iterator() {
        return Collections.unmodifiableList(members).iterator();
    }

    public void setContainersVisible(boolean visible) {
        Iterator it = builders.values().iterator();
        while (it.hasNext()) {
            GroupMemberContainerManager gcm = (GroupMemberContainerManager)it.next();
            gcm.setVisible(visible);
        }
    }

    protected void bindMembers(Object owner, GroupContainerPopulator container, Object factory,
            CommandButtonConfigurer configurer) {
        GroupMemberContainerManager builder = new GroupMemberContainerManager(container, factory, configurer);
        builder.rebuildControlsFor(members);
        builders.put(owner, builder);
    }

    protected void rebuildControls() {
        Iterator iter = builders.values().iterator();
        while (iter.hasNext()) {
            GroupMemberContainerManager builder = (GroupMemberContainerManager)iter.next();
            if (builder != null) {
                builder.rebuildControlsFor(members);
            }
        }
    }

    public boolean contains(AbstractCommand command) {
        for (int i = 0; i < members.size(); i++) {
            GroupMember member = (GroupMember)members.get(i);
            if (member.managesCommand(command.getId())) {
                return true;
            }
        }
        return false;
    }

}