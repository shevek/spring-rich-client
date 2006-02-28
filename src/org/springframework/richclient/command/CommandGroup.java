/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command;

import java.awt.Container;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.support.ButtonBarGroupContainerPopulator;
import org.springframework.richclient.command.support.ButtonStackGroupContainerPopulator;
import org.springframework.richclient.command.support.SimpleGroupContainerPopulator;
import org.springframework.richclient.command.support.ToggleButtonPopupListener;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.jgoodies.forms.layout.Size;

/**
 * @author Keith Donald
 */
public class CommandGroup extends AbstractCommand {

    private EventListenerList listenerList;

    private GroupMemberList memberList = new GroupMemberList();

    private CommandRegistry commandRegistry;

    public CommandGroup() {
        super();
    }

    public CommandGroup(String groupId) {
        super(groupId);
    }

    public CommandGroup(String groupId, CommandFaceDescriptor face) {
        super(groupId, face);
    }

    public CommandGroup(String groupId, CommandRegistry commandRegistry) {
        super(groupId);
        setCommandRegistry(commandRegistry);
    }

    public CommandGroup(String id, String encodedLabel) {
        super(id, encodedLabel);
    }

    public CommandGroup(String id, String encodedLabel, Icon icon, String caption) {
        super(id, encodedLabel, icon, caption);
    }

    /**
     * Creates a command group with a single command member.
     *
     * @param member the command to put in the CommandGroup
     *
     * @return never null
     */
    public static CommandGroup createCommandGroup(AbstractCommand member) {
        return createCommandGroup(null, new Object[] { member });
    }

    public static CommandGroup createCommandGroup(Object[] members) {
        return createCommandGroup(null, members, false, null);
    }

    public static CommandGroup createCommandGroup(String groupId, Object[] members) {
        return createCommandGroup(groupId, members, false, null);
    }

    public static CommandGroup createCommandGroup(String groupId, Object[] members, CommandConfigurer configurer) {
        return createCommandGroup(groupId, members, false, configurer);
    }

    public static CommandGroup createExclusiveCommandGroup(Object[] members) {
        return createCommandGroup(null, members, true, null);
    }

    public static CommandGroup createExclusiveCommandGroup(String groupId, Object[] members) {
        return createCommandGroup(groupId, members, true, null);
    }

    public static CommandGroup createExclusiveCommandGroup(String groupId, Object[] members,
            CommandConfigurer configurer) {
        return createCommandGroup(groupId, members, true, configurer);
    }

    /**
     * Creates a command group, configuring the group using the ObjectConfigurer
     * service (pulling visual configuration properties from an external
     * source). This method will also auto-configure contained Command members
     * that have not yet been configured.
     *
     * @param groupId
     * @param members
     * @return
     */
    private static CommandGroup createCommandGroup(final String groupId, final Object[] members,
                                                   final boolean exclusive,
                                                   final CommandConfigurer configurer) {
        final CommandConfigurer theConfigurer = (configurer != null) ?
                configurer : Application.services();

        final CommandGroupFactoryBean groupFactory =
                new CommandGroupFactoryBean(groupId, null, theConfigurer, members);
        groupFactory.setExclusive(exclusive);
        return groupFactory.getCommandGroup();
    }

    protected void addInternal(AbstractCommand command) {
        this.memberList.add(new SimpleGroupMember(this, command));
    }

    protected void addLazyInternal(String commandId) {
        this.memberList.add(new LazyGroupMember(this, commandId));
    }

    protected void addSeparatorInternal() {
        this.memberList.add(new SeparatorGroupMember());
    }

    protected void addGlueInternal() {
        this.memberList.add(new GlueGroupMember());
    }

    public final void setCommandRegistry(CommandRegistry registry) {
        if (!ObjectUtils.nullSafeEquals(this.commandRegistry, registry)) {

            //@TODO should groups listen to command registration events if
            // they've
            //got lazy members that haven't been instantiated? Or are
            // targetable
            //commands lightweight enough?
            if (logger.isDebugEnabled()) {
                logger.debug("Setting registry " + registry + " for command group '" + getId() + "'");
            }
            this.commandRegistry = registry;
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getMemberList().setContainersVisible(visible);
    }

    protected CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public void add(AbstractCommand command) {
        add(command, true);
    }

    public void add(AbstractCommand command, boolean rebuild) {
        if (command == null) {
            return;
        }
        if (contains(command)) {
            return;
        }
        getMemberList().append(new SimpleGroupMember(this, command));
        rebuildIfNecessary(rebuild);
    }

    public void add(String groupMemberPath, AbstractCommand command) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).add(command);
    }

    private void assertIsGroup(String groupMemberPath, AbstractCommand c) {
        Assert.notNull(c, "Command at path '" + groupMemberPath + "' does not exist.");
        Assert.isTrue((c instanceof CommandGroup), "Command at path '" + groupMemberPath + "' is not a group.");
    }

    public void add(String groupMemberPath, AbstractCommand command, boolean rebuild) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).add(command, rebuild);
    }

    public void remove(AbstractCommand command) {
        remove(command, true);
    }

    public void remove(AbstractCommand command, boolean rebuild) {
        if (command == null) {
            return;
        }
        ExpansionPointGroupMember expansionPoint = getMemberList().getExpansionPoint();
        GroupMember member = expansionPoint.getMemberFor(command.getId());
        if (member != null) {
            expansionPoint.remove(member);
            rebuildIfNecessary(rebuild);
        }
    }

    public void remove(String groupMemberPath, AbstractCommand command) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).remove(command);
    }

    public void remove(String groupMemberPath, AbstractCommand command, boolean rebuild) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).remove(command, rebuild);
    }

    public void addSeparator() {
        addSeparator(true);
    }

    public void addSeparator(boolean rebuild) {
        getMemberList().append(new SeparatorGroupMember());
        rebuildIfNecessary(rebuild);
    }

    public void addGlue() {
        addGlue(true);
    }

    public void addGlue(boolean rebuild) {
        getMemberList().append(new GlueGroupMember());
        rebuildIfNecessary(rebuild);
    }

    private void rebuildIfNecessary(boolean rebuild) {
        if (rebuild) {
            rebuildAllControls();
            fireMembersChanged();
        }
    }

    protected void rebuildAllControls() {
        if (logger.isDebugEnabled()) {
            logger.debug("Rebuilding all GUI controls for command group '" + getId() + "'");
        }
        getMemberList().rebuildControls();
    }

    protected GroupMemberList getMemberList() {
        return memberList;
    }

    protected Iterator memberIterator() {
        return getMemberList().iterator();
    }

    public int size() {
        return getMemberCount();
    }

    public boolean isAllowedMember(AbstractCommand proposedMember) {
        return true;
    }

    /**
     * Search for and return the command contained by this group with the
     * specified path. Nested paths should be deliniated by the "/" character;
     * for example, "fileGroup/newGroup/simpleFileCommand". The returned command
     * may be a group or an action command.
     *
     * @param memberPath
     *            the path of the command, with nested levels deliniated by the
     *            "/" path separator.
     * @return the command at the specified member path, or <code>null</code>
     *         if no was command found.
     */
    public AbstractCommand find(String memberPath) {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching for command with nested path '" + memberPath + "'");
        }
        String[] paths = StringUtils.delimitedListToStringArray(memberPath, "/");
        CommandGroup currentGroup = this;
        // fileGroup/newGroup/newJavaProject
        for (int i = 0; i < paths.length; i++) {
            String memberId = paths[i];
            if (i < paths.length - 1) {
                // must be a nested group
                currentGroup = currentGroup.findCommandGroupMember(memberId);
            }
            else {
                // is last path element; can be a group or action
                return currentGroup.findCommandMember(memberId);
            }
        }
        return null;
    }

    private CommandGroup findCommandGroupMember(String groupId) {
        AbstractCommand c = findCommandMember(groupId);
        Assert.isTrue((c instanceof CommandGroup), "Command with id '" + groupId + "' is not a group.");
        return (CommandGroup)c;
    }

    private AbstractCommand findCommandMember(String commandId) {
        Iterator it = memberList.iterator();
        while (it.hasNext()) {
            GroupMember member = (GroupMember)it.next();
            if (member.managesCommand(commandId)) {
                return member.getCommand();
            }
        }
        logger.warn("No command with id '" + commandId + "' is nested within this group (" + getId()
                + "); returning null");
        return null;
    }

    /**
     * Executes all the members of this group.
     */
    public void execute() {
        Iterator it = memberList.iterator();
        while (it.hasNext()) {
            GroupMember member = (GroupMember)it.next();
            member.getCommand().execute();
        }
    }

    public int getMemberCount() {
        return getMemberList().size();
    }

    public boolean contains(AbstractCommand command) {
        return getMemberList().contains(command);
    }

    public void reset() {
        ExpansionPointGroupMember expansionPoint = getMemberList().getExpansionPoint();
        if (!expansionPoint.isEmpty()) {
            expansionPoint.clear();
            rebuildIfNecessary(true);
        }
    }

    public AbstractButton createButton(String faceDescriptorId, ButtonFactory buttonFactory,
            CommandButtonConfigurer buttonConfigurer) {
        return createButton(getDefaultFaceDescriptorId(), buttonFactory, getMenuFactory(), buttonConfigurer);
    }

    public AbstractButton createButton(ButtonFactory buttonFactory, MenuFactory menuFactory) {
        return createButton(getDefaultFaceDescriptorId(), buttonFactory, menuFactory, getPullDownMenuButtonConfigurer());
    }

    public AbstractButton createButton(String faceDescriptorId, ButtonFactory buttonFactory, MenuFactory menuFactory) {
        return createButton(faceDescriptorId, buttonFactory, menuFactory, getPullDownMenuButtonConfigurer());
    }

    public AbstractButton createButton(ButtonFactory buttonFactory, MenuFactory menuFactory,
            CommandButtonConfigurer buttonConfigurer) {
        return createButton(getDefaultFaceDescriptorId(), buttonFactory, menuFactory, buttonConfigurer);
    }

    public AbstractButton createButton(String faceDescriptorId, ButtonFactory buttonFactory, MenuFactory menuFactory,
            CommandButtonConfigurer buttonConfigurer) {
        JToggleButton button = buttonFactory.createToggleButton();
        attach(button, buttonConfigurer);
        JPopupMenu popup = menuFactory.createPopupMenu();
        bindMembers(button, popup, menuFactory, getMenuItemButtonConfigurer());
        ToggleButtonPopupListener.bind(button, popup);
        return button;
    }

    protected CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
        return getCommandServices().getPullDownMenuButtonConfigurer();
    }

    public JMenuItem createMenuItem(String faceDescriptorId, MenuFactory factory,
            CommandButtonConfigurer buttonConfigurer) {
        JMenu menu = factory.createMenu();
        attach(menu);
        bindMembers(menu, menu, factory, buttonConfigurer);
        return menu;
    }

    public JPopupMenu createPopupMenu() {
        return createPopupMenu(getMenuFactory());
    }

    public JPopupMenu createPopupMenu(MenuFactory factory) {
        JPopupMenu popup = factory.createPopupMenu();
        bindMembers(popup, popup, factory, getMenuItemButtonConfigurer());
        return popup;
    }

    public JComponent createToolBar() {
        return createToolBar(getButtonFactory());
    }

    public JComponent createToolBar(ButtonFactory factory) {
        JComponent toolbar = createNewToolBar(getText());
        bindMembers(toolbar, toolbar, factory, getToolBarButtonConfigurer());
        toolbar.setEnabled(false);
        toolbar.setVisible(true);
        return toolbar;
    }

    public JMenuBar createMenuBar() {
        return createMenuBar(getMenuFactory());
    }

    public JMenuBar createMenuBar(MenuFactory factory) {
        JMenuBar menubar = factory.createMenuBar();
        bindMembers(menubar, menubar, factory, getMenuItemButtonConfigurer());
        return menubar;
    }

    protected JComponent createNewToolBar(String text) {
        JToolBar toolBar = new JToolBar(text);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        return toolBar;
    }

    /**
     * Create a button bar with buttons for all the commands in this.
     *
     * @return never null
     */
    public JComponent createButtonBar() {
        return createButtonBar(null);
    }

    /**
     * Create a button bar with buttons for all the commands in this.
     *
     * @param minimumButtonSize if null, then there is no minimum size
     *
     * @return never null
     */
    public JComponent createButtonBar(Size minimumButtonSize) {
        return createButtonBar(minimumButtonSize, GuiStandardUtils
                .createTopAndBottomBorder(UIConstants.TWO_SPACES));
    }

    /**
     * Create a button bar with buttons for all the commands in this.
     *
     * @param minimumButtonSize if null, then there is no minimum size
     * @param border            if null, then don't use a border
     *
     * @return never null
     */
    public JComponent createButtonBar(final Size minimumButtonSize, final Border border) {
        final ButtonBarGroupContainerPopulator container = new ButtonBarGroupContainerPopulator();
        container.setMinimumButtonSize(minimumButtonSize);
        addCommandsToGroupContainer(container);
        return GuiStandardUtils.attachBorder(container.getButtonBar(), border);
    }
    
    /**
     * Create a button stack with buttons for all the commands.
     * 
     * @return never null
     */
    public JComponent createButtonStack()
    {
        return createButtonStack(null);
    }
    
    /**
     * Create a button stack with buttons for all the commands.
     * 
     * @param minimumButtonSize Minimum size of the buttons (can be null)
     * @return  never null
     */
    public JComponent createButtonStack(final Size minimumButtonSize)
    {
        return createButtonStack(minimumButtonSize, GuiStandardUtils
                .createTopAndBottomBorder(UIConstants.TWO_SPACES));
    }

    /**
     * Create a button stack with buttons for all the commands.
     * 
     * @param minimumButtonSize Minimum size of the buttons (can be null)
     * @param border    Border to set around the stack.
     * @return  never null
     */
    public JComponent createButtonStack(final Size minimumButtonSize, final Border border)
    {
        final ButtonStackGroupContainerPopulator container = new ButtonStackGroupContainerPopulator();
        container.setMinimumButtonSize(minimumButtonSize);
        addCommandsToGroupContainer(container);
        return GuiStandardUtils.attachBorder(container.getButtonStack(), border);
    }

    /**
     * Create a container with the given GroupContainerPopulator which will hold the members of this group.
     *
     * @param groupContainerPopulator
     */
    protected void addCommandsToGroupContainer(final GroupContainerPopulator groupContainerPopulator) {
        final Iterator members = getMemberList().iterator();

        while (members.hasNext()) {
            final GroupMember member = (GroupMember)members.next();
            if (member.getCommand() instanceof CommandGroup) {
                member.fill(groupContainerPopulator, getButtonFactory(), getPullDownMenuButtonConfigurer(), Collections.EMPTY_LIST);
            }
            else {
                member.fill(groupContainerPopulator, getButtonFactory(), getDefaultButtonConfigurer(), Collections.EMPTY_LIST);
            }
        }
        groupContainerPopulator.onPopulated();
    }
    
    private void bindMembers(Object owner, Container memberContainer, Object controlFactory,
            CommandButtonConfigurer configurer) {
        getMemberList().bindMembers(owner, new SimpleGroupContainerPopulator(memberContainer), controlFactory,
                configurer);
    }

    public void addGroupListener(CommandGroupListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(CommandGroupListener.class, l);
    }

    public void removeGroupListener(CommandGroupListener l) {
        Assert.notNull(listenerList, "Listener list has not yet been instantiated!");
        listenerList.remove(CommandGroupListener.class, l);
    }

    protected void fireMembersChanged() {
        if (listenerList == null) {
            return;
        }
        CommandGroupEvent event = null;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CommandGroupListener.class) {
                if (event == null) {
                    event = new CommandGroupEvent(this);
                }
                ((CommandGroupListener)listeners[i + 1]).membersChanged(event);
            }
        }
    }
}