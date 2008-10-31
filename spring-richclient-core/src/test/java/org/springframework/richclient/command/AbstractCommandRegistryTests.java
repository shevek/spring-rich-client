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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.springframework.util.ObjectUtils;


/**
 * This is an abstract test case for implementations of the {@link CommandRegistry} interface. 
 * Subclasses only need to override the {@link #getCommandRegistry()} method to return the 
 * concrete implementation to be tested.
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public abstract class AbstractCommandRegistryTests extends TestCase {

    /**
     * Creates a new uninitialized {@code AbstractCommandRegistryTests}.
     */
    protected AbstractCommandRegistryTests() {
        super();
    }
    
    /**
     * Subclasses must override this method to provide the concrete implementation of the registry
     * to be tested. A new, empy registry must be provided. This method may be called often, so 
     * subclasses should take care to not repeat any unnecessary initialization.
     *
     * @return The registry implementation to be tested, never null.
     */
    protected abstract CommandRegistry getCommandRegistry();
    
    /**
     * Tests the {@link CommandRegistry#registerCommand(AbstractCommand)} method.
     */
    public void testRegisterCommand() {
        
        CommandRegistry registry = getCommandRegistry();
        CommandRegistryListener listener 
                = (CommandRegistryListener) EasyMock.createStrictMock(CommandRegistryListener.class);
        registry.addCommandRegistryListener(listener);
        
        EasyMock.replay(listener);

        try {
            registry.registerCommand(null);
            Assert.fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        EasyMock.verify(listener);
        EasyMock.reset(listener);
        EasyMock.replay(listener);
        
        try {
            registry.registerCommand(new TestCommand());
            Assert.fail("Should throw IllegalArgumentException because commandId has not been set");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }

        EasyMock.verify(listener);
        EasyMock.reset(listener);
        
        TestCommand command1 = new TestCommand("command");
        CommandRegistryEvent event = new CommandRegistryEvent(registry, command1);
        
        listener.commandRegistered(matchEvent(event));
        
        EasyMock.replay(listener);
        registry.registerCommand(command1);
        EasyMock.verify(listener);

        Assert.assertTrue("command1 not registered", registry.containsCommand(command1.getId()));
        Assert.assertEquals("command1 not registered", command1, registry.getCommand(command1.getId()));

        TestCommand command2 = new TestCommand(command1.getId());
        event = new CommandRegistryEvent(registry, command2);
        
        EasyMock.reset(listener);
        
        listener.commandRegistered(matchEvent(event));
        EasyMock.replay(listener);
        registry.registerCommand(command2);
        EasyMock.verify(listener);
        
        Assert.assertTrue(registry.containsCommand(command2.getId()));
        Assert.assertEquals("command1 not overridden", command2, registry.getCommand(command2.getId()));

    }
    
    /**
     * Confirms that if a CommandGroup is being registered, it will obtain a reference to the 
     * registry that it is being added to.
     */
    public void testRegisterCommandGroup() {
        
        CommandRegistry registry = getCommandRegistry();

        CommandGroup commandGroup = new CommandGroup("testCommandGroup");
        registry.registerCommand(commandGroup);

        assertTrue("commandgroup not registered", registry.containsCommand("testCommandGroup"));
        assertEquals("commandgroup not registered", commandGroup, registry.getCommand("testCommandGroup"));
        
        Assert.assertEquals(registry, commandGroup.getCommandRegistry());
        
    }
    
    /**
     * Tests the {@link CommandRegistry#isTypeMatch(String, Class)} method.
     */
    public final void testIsTypeMatch() {
        
        CommandRegistry registry = getCommandRegistry();
        
        try {
            registry.isTypeMatch(null, Object.class);
            Assert.fail("Should have thrown IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        try {
            registry.isTypeMatch("bogusCommandId", null);
            Assert.fail("Should have thrown IllegalArgumentException for null targetType");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        //Add a command to the registry which is a subclass of AbstractCommand
        TestCommand testCommand = new TestCommand("testCommand");
        registry.registerCommand(testCommand);
        
        Assert.assertTrue("Assert isTypeMatch", registry.isTypeMatch(testCommand.getId(), TestCommand.class));
        Assert.assertTrue("Assert isTypeMatch", registry.isTypeMatch(testCommand.getId(), AbstractCommand.class));
        Assert.assertFalse("Assert isTypeMatch", registry.isTypeMatch(testCommand.getId(), String.class));
        
    }
    
    /**
     * Tests the {@link CommandRegistry#containsCommand(String)} method.
     */
    public final void testContainsCommand() {
     
        CommandRegistry registry = getCommandRegistry();
        
        try {
            registry.containsCommand(null);
            Assert.fail("Should have thrown an IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        Assert.assertFalse("Assert registry does not contain a command", registry.containsCommand("bogusCommandId"));
        
        TestCommand testCommand = new TestCommand("testCommand");
        registry.registerCommand(testCommand);
        
        Assert.assertTrue("Assert registry contains command", registry.containsCommand(testCommand.getId()));
        
    }

    /**
     * Tests the {@link CommandRegistry#getCommand(String)} method.
     */
    public final void testGetCommandById() {
        
        CommandRegistry registry = getCommandRegistry();
        
        try {
            registry.getCommand(null);
            Assert.fail("Should have thrown an IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        Assert.assertNull("getCommand should return null", registry.getCommand("bogusCommandId"));
        
        TestCommand testCommand = new TestCommand("testCommand");
        registry.registerCommand(testCommand);
        
        Assert.assertEquals(testCommand, registry.getCommand(testCommand.getId()));
           
    }

    /**
     * Tests the {@link CommandRegistry#getCommand(String, Class)} method.
     */
    public final void testGetCommandByIdAndRequiredType() {
        
        CommandRegistry registry = getCommandRegistry();
        
        try {
            registry.getCommand(null, Object.class);
            Assert.fail("Should have thrown an IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }

        Assert.assertNull(registry.getCommand("bogusCommandId", null));
        
        Assert.assertNull("getCommand should return null", registry.getCommand("bogusCommandId", TestCommand.class));
        
        TestCommand testCommand = new TestCommand("testCommand");
        TestCommand2 testCommand2 = new TestCommand2("testCommand2");
        registry.registerCommand(testCommand);
        registry.registerCommand(testCommand2);
        
        Assert.assertEquals(testCommand, registry.getCommand(testCommand.getId(), TestCommand.class));
        Assert.assertEquals(testCommand, registry.getCommand(testCommand.getId(), AbstractCommand.class));
        
        try {
            registry.getCommand(testCommand.getId(), TestCommand2.class);
            Assert.fail("Should have thrown CommandNotOfRequiredTypeException");
        }
        catch (CommandNotOfRequiredTypeException e) {
            Assert.assertEquals(testCommand.getId(), e.getCommandId());
            Assert.assertEquals(TestCommand2.class, e.getRequiredType());
            Assert.assertEquals(TestCommand.class, e.getActualType());
        }
        
    }

    /**
     * Tests the {@link CommandRegistry#getType(String)} method.
     */
    public final void testGetType() {
        
        CommandRegistry registry = getCommandRegistry();
        
        try {
            registry.getType(null);
            Assert.fail("Should have thrown an IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }

        Assert.assertNull("getType should return null", registry.getType("bogusCommandId"));
        
        TestCommand testCommand = new TestCommand("testCommand");
        TestCommand2 testCommand2 = new TestCommand2("testCommand2");
        registry.registerCommand(testCommand);
        registry.registerCommand(testCommand2);
        
        Assert.assertEquals(testCommand.getClass(), registry.getType(testCommand.getId()));
        Assert.assertEquals(testCommand2.getClass(), registry.getType(testCommand2.getId()));
        
    }
    
    /**
     * Tests the {@link CommandRegistry#setTargetableActionCommandExecutor(String, ActionCommandExecutor)}
     * method.
     */
    public void testSetTargetableActionCommandExecutor() {
        
        CommandRegistry registry = getCommandRegistry();
        TestTargetableActionCommand targetableCommand = new TestTargetableActionCommand();
        targetableCommand.setId("bogusId");
        registry.registerCommand(targetableCommand);
        ActionCommandExecutor executor = (ActionCommandExecutor) EasyMock.createMock(ActionCommandExecutor.class);
        
        try {
            registry.setTargetableActionCommandExecutor(null, null);
            Assert.fail("Should have thrown an IllegalArgumentException for null commandId");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        registry.setTargetableActionCommandExecutor(targetableCommand.getId(), executor);
        
        Assert.assertEquals(executor, targetableCommand.getCommandExecutor());
        
        registry.setTargetableActionCommandExecutor(targetableCommand.getId(), null);
        
        Assert.assertEquals(null, targetableCommand.getCommandExecutor());
        
    }
    
    private static class TestCommand extends AbstractCommand {
        
        private String id;
        
        /**
         * Creates a new uninitialized {@code TestCommand}.
         *
         */
        public TestCommand() {
            //do nothing
        }

        /**
         * Creates a new uninitialized {@code TestCommand}.
         *
         * @param id
         */
        public TestCommand(String id) {
            this.id = id;
        }
        
        public String getId() {
            return this.id;
        }

        /**
         * {@inheritDoc}
         */
        public void execute() {
            //do nothing
        }
        
    }

    private static class TestCommand2 extends AbstractCommand {
        
        private String id;

        /**
         * Creates a new uninitialized {@code TestCommand2}.
         *
         */
        public TestCommand2() {
            //do nothing
        }

        /**
         * Creates a new uninitialized {@code TestCommand2}.
         *
         * @param id
         */
        public TestCommand2(String id) {
            this.id = id;
        }
        
        public String getId() {
            return this.id;
        }

        /**
         * {@inheritDoc}
         */
        public void execute() {
            //do nothing
        }
        
    }
    
    private static CommandRegistryEvent matchEvent(CommandRegistryEvent event) {
        EasyMock.reportMatcher(new CommandRegistryEventMatcher(event));
        return event;
    }
    
    private static class CommandRegistryEventMatcher implements IArgumentMatcher {
        
        private CommandRegistryEvent expectedEvent;
        
        /**
         * Creates a new {@code CommandRegistryEventMatcher}.
         *
         * @param expectedEvent
         */
        public CommandRegistryEventMatcher(CommandRegistryEvent expectedEvent) {
            this.expectedEvent = expectedEvent;
        }
        
        /**
         * {@inheritDoc}
         */
        public void appendTo(StringBuffer buffer) {
            buffer.append("(");
            buffer.append(expectedEvent.getClass().getName());
            buffer.append(" with message \"");
            buffer.append(expectedEvent.getSource());
            buffer.append("\"\")");
            
        }

        /**
         * {@inheritDoc}
         */
        public boolean matches(Object argument) {
            
            if (!(argument instanceof CommandRegistryEvent)) {
                return false;
            }
            
            CommandRegistryEvent other = (CommandRegistryEvent) argument;
            
            if (!ObjectUtils.nullSafeEquals(expectedEvent.getSource(), other.getSource())) {
                return false;
            }
            
            if (!ObjectUtils.nullSafeEquals(expectedEvent.getCommand(), other.getCommand())) {
                return false;
            }
            
            return true;
            
        }
        
    }
    
    private static class TestTargetableActionCommand extends TargetableActionCommand {
        
        private ActionCommandExecutor executor;
        
        /**
         * {@inheritDoc}
         */
        public void setCommandExecutor(ActionCommandExecutor commandExecutor) {
            this.executor = commandExecutor;
        }

        public ActionCommandExecutor getCommandExecutor() {
            return this.executor;
        }
        
    }

}
