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

import org.easymock.EasyMock;

import junit.framework.Assert;
import junit.framework.TestCase;


/**
 * Provides unit tests for the {@link CommandRegistryEvent} class.
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public class CommandRegistryEventTests extends TestCase {

    /**
     * Creates a new uninitialized {@code CommandRegistryEventTests}.
     */
    public CommandRegistryEventTests() {
        super();
    }

    /**
     * Tests that the event object can be created and its properties correctly retrieved. 
     */
    public final void testAll() {
        
        AbstractCommand command = new AbstractCommand("noOpCommand") {
            public void execute() {
                //do nothing
            }
        };
        
        CommandRegistry registry = (CommandRegistry) EasyMock.createMock(CommandRegistry.class);
        
        try {
            new CommandRegistryEvent(null, command);
            Assert.fail("Should have thrown an IllegalArgumentException for null registry");
        } 
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        try {
            new CommandRegistryEvent(registry, null);
            Assert.fail("Should have thrown an IllegalArgumentException for null command");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }

        CommandRegistryEvent event = new CommandRegistryEvent(registry, command);
        
        Assert.assertEquals(command, event.getCommand());
        Assert.assertEquals(registry, event.getSource());
        
    }

}
