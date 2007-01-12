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
import org.springframework.richclient.application.PropertyNotSetException;
import org.springframework.richclient.command.config.CommandConfigurer;


/**
 * Provides a suite of unit tests for the {@link CommandGroupFactoryBean} class.
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public class CommandGroupFactoryBeanTests extends TestCase {
    
    private AbstractCommand noOpCommand = new AbstractCommand() {
        public void execute() {
            //do nothing
        }

        /**
         * {@inheritDoc}
         */
        public String getId() {
            return "noOpCommand";
        }
        
    };
    
    private ToggleCommand toggleCommand = new ToggleCommand() {
        
        /**
         * {@inheritDoc}
         */
        public String getId() {
            return "toggleCommand";
        }
        
    };

    /**
     * Creates a new uninitialized {@code CommandGroupFactoryBeanTests}.
     */
    public CommandGroupFactoryBeanTests() {
        super();
    }
    
    /**
     * Confirms that an exception is thrown from the afterPropertiesSet method if the
     * encodedMembers property has not been set.
     */
    public void testForEncodedMembersNotSet() {
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean();
        
        try {
            factoryBean.afterPropertiesSet();
            Assert.fail("Should have thrown a PropertyNotSetException");
        }
        catch (PropertyNotSetException e) {
            Assert.assertEquals("members", e.getPropertyName());
        }
        
    }

    /**
     * Tests the constructor that takes the group id and members array.
     * @throws Exception 
     */
    public final void testConstructorTakingGroupIdAndMembersArray() throws Exception {
        
        String groupId = "groupId";
        Object[] members = null;
        
        try {
            new CommandGroupFactoryBean(groupId, members);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            //do nothing, test passes
        }
        
        members = new Object[] {noOpCommand};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean(groupId, members);
        CommandGroup commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertEquals(groupId, commandGroup.getId());
        Assert.assertEquals(1, commandGroup.size());
        
    }

    /**
     * Test method for {@link CommandGroupFactoryBean#setMembers(java.lang.Object[])}.
     */
    public final void testSetMembers() {
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean();
        
        try {
            factoryBean.setMembers(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        factoryBean.setMembers(new Object[] {});
        
    }

    /**
     * Test method for {@link org.springframework.richclient.command.CommandGroupFactoryBean#setBeanName(java.lang.String)}.
     * @throws Exception 
     */
    public final void testSetBeanName() throws Exception {
        
        String groupId = "bogusGroupId";
        String beanName = "bogusBeanName";
        Object[] members = new Object[] {noOpCommand};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean(groupId, members);
        CommandGroup commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertEquals(groupId, commandGroup.getId());
        
        //confirm that setting the beanName will override the groupId
        factoryBean = new CommandGroupFactoryBean(groupId, members);
        factoryBean.setBeanName(beanName);
        commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertEquals(beanName, commandGroup.getId());
        
    }

    /**
     * Confirms that an exception is thrown if the 'group:' prefix appears in the members list
     * with no following command name.
     */
    public void testInvalidGroupPrefix() {
        
        Object[] members = new Object[] {CommandGroupFactoryBean.GROUP_MEMBER_PREFIX};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean();
        factoryBean.setMembers(members);
        
        try {
            factoryBean.getCommandGroup();
            Assert.fail("Should have thrown an InvalidGroupMemberEncodingException");
        }
        catch (InvalidGroupMemberEncodingException e) {
            Assert.assertEquals(CommandGroupFactoryBean.GROUP_MEMBER_PREFIX, e.getEncodedString());
        }
        
    }
    
    /**
     * Confirms that an exception is thrown if the 'command:' prefix appears in the members list
     * with no following command name.
     */
    public void testInvalidCommandPrefix() {
        
        Object[] members = new Object[] {CommandGroupFactoryBean.COMMAND_MEMBER_PREFIX};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean();
        factoryBean.setMembers(members);
        
        try {
            factoryBean.getCommandGroup();
            Assert.fail("Should have thrown an InvalidGroupMemberEncodingException");
        }
        catch (InvalidGroupMemberEncodingException e) {
            Assert.assertEquals(CommandGroupFactoryBean.COMMAND_MEMBER_PREFIX, e.getEncodedString());
        }
        
    }

    /**
     * Test method for {@link CommandGroupFactoryBean#createCommandGroup()}.
     * @throws Exception 
     */
    public final void testCreateCommandGroup() throws Exception {
        
        String groupId = "bogusGroupId";
        String securityId = "bogusSecurityId";
        Object[] members = new Object[] {toggleCommand};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean(groupId, members);
        factoryBean.setSecurityControllerId(securityId);
        CommandGroup commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertEquals(securityId , commandGroup.getSecurityControllerId());
        Assert.assertFalse("Assert command group not exclusive", commandGroup instanceof ExclusiveCommandGroup);
        Assert.assertEquals(1, commandGroup.size());
        
        factoryBean = new CommandGroupFactoryBean(groupId, members);
        factoryBean.setExclusive(true);
        factoryBean.setAllowsEmptySelection(true);
        commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertTrue("Assert command group is exclusive", commandGroup instanceof ExclusiveCommandGroup);
        Assert.assertTrue("Assert allows empty selection is true", 
                          ((ExclusiveCommandGroup) commandGroup).getAllowsEmptySelection());
        
        
    }

    /**
     * Test method for {@link CommandGroupFactoryBean#configureIfNecessary(AbstractCommand)}.
     */
    public final void testConfigureIfNecessary() {
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean();
        
        try {
            factoryBean.configureIfNecessary(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        AbstractCommand command = new AbstractCommand() {
            public void execute() {
                //do nothing
            }
        };
        
        //no configurer has been set, confirming that this doesn't throw an exception
        factoryBean.configureIfNecessary(command);
        
        CommandConfigurer configurer = (CommandConfigurer) EasyMock.createMock(CommandConfigurer.class);
        EasyMock.expect(configurer.configure(command)).andReturn(command);
        
        EasyMock.replay(configurer);
        
        factoryBean.setCommandConfigurer(configurer);
        factoryBean.configureIfNecessary(command);        
        
        EasyMock.verify(configurer);
        
    }

    /**
     * Test method for {@link CommandGroupFactoryBean#getObjectType()}.
     */
    public final void testGetObjectType() {
        Assert.assertEquals(CommandGroup.class, new CommandGroupFactoryBean().getObjectType());
    }

    /**
     * Confirms that the command group is assigned the security controller id of the factory bean.
     * @throws Exception 
     */
    public final void testSecurityControllerIdIsApplied() throws Exception {
        
        String groupId = "bogusGroupId";
        String securityId = "bogusSecurityId";
        Object[] members = new Object[] {noOpCommand};
        
        CommandGroupFactoryBean factoryBean = new CommandGroupFactoryBean(groupId, members);
        factoryBean.setSecurityControllerId(securityId);
        CommandGroup commandGroup = (CommandGroup) factoryBean.getObject();
        Assert.assertEquals(securityId , commandGroup.getSecurityControllerId());
        
    }

}
