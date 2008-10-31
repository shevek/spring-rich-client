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

import java.util.ArrayList;
import java.util.List;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.AbstractCommandRegistryTests;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandNotOfRequiredTypeException;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.command.CommandRegistryListener;

/**
 * Provides unit tests for the {@link DefaultCommandRegistry} class.
 * 
 * @author Peter De Bruycker
 * @author Kevin Stembridge
 */
public class DefaultCommandRegistryTests extends AbstractCommandRegistryTests {
    
    /**
     * {@inheritDoc}
     */
    protected CommandRegistry getCommandRegistry() {
        return new DefaultCommandRegistry();
    }

    public void testConstructor() {
        DefaultCommandRegistry registry = new DefaultCommandRegistry();
        assertNull("parent must be null", registry.getParent());

        TestCommandRegistry parent = new TestCommandRegistry();
        registry = new DefaultCommandRegistry(parent);
        assertEquals("parent not set", parent, registry.getParent());
        assertEquals("registry not added to parent", 1, parent.addedListeners.size());
        assertTrue("registry not added to parent", parent.addedListeners.contains(registry));
    }

    public void testSetParent() {
        TestCommandRegistry parent = new TestCommandRegistry();
        TestCommandRegistry parent2 = new TestCommandRegistry();

        DefaultCommandRegistry registry = new DefaultCommandRegistry(parent);

        registry.setParent(parent2);

        assertEquals("parent not set", parent2, registry.getParent());
        assertEquals("registry not removed from parent", 1, parent.removedListeners.size());
        assertTrue("registry not removed from parent", parent.removedListeners.contains(registry));
        assertEquals("registry not added to parent", 1, parent2.addedListeners.size());
        assertTrue("registry not added to parent", parent2.addedListeners.contains(registry));

        // set same parent, nothing should happen
        registry.setParent(parent2);
        assertEquals("registry added twice to same parent", 1, parent2.addedListeners.size());
        assertTrue("registry removed from same parent", parent2.removedListeners.isEmpty());

        parent2.reset();

        // set parent to null
        registry.setParent(null);
        assertNull("parent not set to null", registry.getParent());
        assertEquals("registry not removed from parent", 1, parent2.removedListeners.size());
    }

    private static class TestCommandRegistry implements CommandRegistry {
        private List addedListeners = new ArrayList();

        private List removedListeners = new ArrayList();

        public ActionCommand getActionCommand(String commandId) {
            return null;
        }

        public CommandGroup getCommandGroup(String groupId) {
            return null;
        }

        public boolean containsActionCommand(String commandId) {
            return false;
        }

        public boolean containsCommandGroup(String groupId) {
            return false;
        }

        public void registerCommand(AbstractCommand command) {
        }

        public void setTargetableActionCommandExecutor(String targetableCommandId, ActionCommandExecutor commandExecutor) {
        }

        public void addCommandRegistryListener(CommandRegistryListener l) {
            addedListeners.add(l);
        }

        public void removeCommandRegistryListener(CommandRegistryListener l) {
            removedListeners.add(l);
        }

        public void reset() {
            addedListeners.clear();
            removedListeners.clear();
        }

        /**
         * {@inheritDoc}
         */
        public boolean containsCommand(String commandId) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public Object getCommand(String commandId) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Object getCommand(String commandId, Class requiredType) throws CommandNotOfRequiredTypeException {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Class getType(String commandId) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isTypeMatch(String commandId, Class targetType) {
            return false;
        }
        
    }
    
}
