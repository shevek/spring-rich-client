/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.command;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public class CommandGroupFactoryBean implements BeanNameAware, FactoryBean {
    public static final String GLUE_MEMBER_CODE = "glue";

    public static final String SEPARATOR_MEMBER_CODE = "separator";

    public static final String COMMAND_MEMBER_CODE = "command://";

    public static final String GROUP_MEMBER_CODE = "group://";

    private String groupId;

    private CommandRegistry commandRegistry;

    private CommandConfigurer commandConfigurer;

    private Object[] encodedMembers;

    private boolean exclusive;

    private boolean allowsEmptySelection;

    private CommandGroup commandGroup;

    public CommandGroupFactoryBean() {

    }

    public CommandGroupFactoryBean(String groupId, Object[] encodedMembers) {
        this(groupId, null, null, encodedMembers);
    }

    public CommandGroupFactoryBean(String groupId,
            CommandRegistry commandRegistry, Object[] encodedMembers) {
        this(groupId, commandRegistry, null, encodedMembers);
    }

    public CommandGroupFactoryBean(String groupId,
            CommandRegistry commandRegistry, CommandConfigurer configurer,
            Object[] encodedMembers) {
        setBeanName(groupId);
        setCommandRegistry(commandRegistry);
        setMembers(encodedMembers);
        setCommandConfigurer(configurer);
    }

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void setCommandConfigurer(CommandConfigurer configurer) {
        this.commandConfigurer = configurer;
    }

    public void setMembers(Object[] encodedMembers) {
        this.encodedMembers = encodedMembers;
    }

    public void setBeanName(String beanName) {
        this.groupId = beanName;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public void setAllowsEmptySelection(boolean allowsEmptySelection) {
        this.allowsEmptySelection = allowsEmptySelection;
    }

    public Object getObject() throws Exception {
        return getCommandGroup();
    }

    public CommandGroup getCommandGroup() {
        if (commandGroup == null) {
            commandGroup = createCommandGroup();
        }
        return commandGroup;
    }

    protected CommandGroup createCommandGroup() {
        CommandGroup group;
        if (isExclusive()) {
            ExclusiveCommandGroup g = new ExclusiveCommandGroup(groupId, commandRegistry);
            g.setAllowsEmptySelection(allowsEmptySelection);
            group = g;
        }
        else {
            group = new CommandGroup(groupId, commandRegistry);
        }
        initCommandGroupMembers(group);
        return group;
    }

    private void initCommandGroupMembers(CommandGroup group) {
        for (int i = 0; i < encodedMembers.length; i++) {
            Object o = encodedMembers[i];
            if (o instanceof AbstractCommand) {
                group.addInternal((AbstractCommand)o);
                configureIfNecessary((AbstractCommand)o);
            }
            else if (o instanceof String) {
                String str = (String)o;
                if (str.equalsIgnoreCase(SEPARATOR_MEMBER_CODE)) {
                    group.addSeparatorInternal();
                }
                else if (str.equalsIgnoreCase(GLUE_MEMBER_CODE)) {
                    group.addGlueInternal();
                }
                else if (str.startsWith(COMMAND_MEMBER_CODE)) {
                    addCommandMember(str.substring(10), false, group);
                }
                else if (str.startsWith(GROUP_MEMBER_CODE)) {
                    addCommandMember(str.substring(8), true, group);
                }
                else {
                    addCommandMember(str, false, group);
                }
            }
        }
    }

    private void addCommandMember(String commandId, boolean isGroup,
            CommandGroup group) {
        EncodedGroupMemberInfo info = parseEncodedCommandInfo(commandId);
        AbstractCommand command = null;
        if (commandRegistry != null) {
            if (isGroup) {
                command = commandRegistry.getCommandGroup(info.commandId);
                if (command != null) {
                    if (info.inlined) {
                        group.addInlinedInternal((CommandGroup)command);
                    }
                    else {
                        group.addInternal(command);
                    }
                }
            }
            else {
                command = commandRegistry.getActionCommand(info.commandId);
                if (command != null) {
                    group.addInternal(command);
                }
            }
        }
        if (command == null) {
            group.addLazyInternal(info.commandId, info.inlined);
        }
    }

    private EncodedGroupMemberInfo parseEncodedCommandInfo(String str) {
        String[] info = StringUtils.commaDelimitedListToStringArray(str);
        if (info.length == 1) {
            return new EncodedGroupMemberInfo(info[0], false);
        }
        else if (info.length == 2) {
            return new EncodedGroupMemberInfo(info[0], Boolean.valueOf(info[1])
                    .booleanValue());
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid encoded command group info '" + str
                            + "'; code should be of format <groupId>,[inlined]");
        }
    }

    protected void configureIfNecessary(AbstractCommand command) {
        if (commandConfigurer != null) {
            if (!command.isFaceConfigured()) {
                commandConfigurer.configure(command);
            }
        }
    }

    private static final class EncodedGroupMemberInfo {
        private String commandId;

        private boolean inlined;

        public EncodedGroupMemberInfo(String commandId, boolean inlined) {
            this.commandId = commandId;
            this.inlined = inlined;
        }
    }

    public Class getObjectType() {
        return CommandGroup.class;
    }

    public boolean isSingleton() {
        return true;
    }

}