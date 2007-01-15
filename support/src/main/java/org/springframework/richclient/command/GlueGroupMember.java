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

import javax.swing.Box;

import org.springframework.richclient.command.config.CommandButtonConfigurer;

/**
 * A member of a {@link CommandGroup} that represents a 'glue' component between other members
 * of the group. 
 * 
 * <p>
 * A glue component is most often used as a filler between other components in a layout where those
 * components cannot expand beyound a maximum height or width. As the layout area expands, the glue 
 * component will expand to take up the space.
 * </p>
 *
 * @see Box#createGlue()
 */
public class GlueGroupMember extends GroupMember {
    
    /**
     * Creates a new uninitialized {@code GlueGroupMember}.
     */
    public GlueGroupMember() {
        //do nothing
    }

    /**
     * Adds a glue component using the given container populator.
     * 
     * {@inheritDoc}
     */
    protected void fill(GroupContainerPopulator parentContainer, 
                        Object factory, 
                        CommandButtonConfigurer configurer,
                        List previousButtons) {
        parentContainer.add(Box.createGlue());
    }

    /**
     * Always returns false. A glue group member does not manage any commands.
     * @return false always.
     */
    public final boolean managesCommand(String commandId) {
        return false;
    }

    /**
     * Default implemenation, performs no operation.
     */
    public void setEnabled(boolean enabled) {
        // do nothing
    }
    
}