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

/**
 * A command group member that represents a separator between other members of the group.
 */
public class SeparatorGroupMember extends GroupMember {

    /**
     * Creates a new {@code SeparatorGroupMember}.
     */
    public SeparatorGroupMember() {
        //do nothing
    }

    /**
     * Asks the given container populator to add a separator to its underlying container.
     */
    protected void fill(GroupContainerPopulator container, 
                        Object factory, 
                        CommandButtonConfigurer configurer,
                        List previousButtons) {
        container.addSeparator();
    }

    /**
     * Always returns false.
     * @return false always.
     */
    public final boolean managesCommand(String commandId) {
        return false;
    }

    /**
     * Default implementation, performs no operation.
     */
    public void setEnabled(boolean enabled) {
        //do nothing
    }

}
