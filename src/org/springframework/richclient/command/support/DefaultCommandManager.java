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
package org.springframework.richclient.command.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.command.ActionCommandInterceptor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandGroupFactoryBean;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.command.CommandRegistryListener;
import org.springframework.richclient.command.ExclusiveCommandGroup;
import org.springframework.richclient.command.TargetableActionCommand;
import org.springframework.richclient.command.config.ApplicationCommandConfigurer;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class DefaultCommandManager implements CommandManager, BeanPostProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    private List globalCommands;

    private DefaultCommandRegistry commandRegistry = new DefaultCommandRegistry();

    private ButtonFactory buttonFactory;

    private MenuFactory menuFactory;

    private CommandConfigurer commandConfigurer = new ApplicationCommandConfigurer(
            this, this);

    private CommandButtonConfigurer defaultButtonConfigurer;

    private CommandButtonConfigurer toolBarButtonConfigurer;

    private CommandButtonConfigurer menuItemButtonConfigurer;

    private CommandButtonConfigurer pullDownMenuButtonConfigurer;

    public DefaultCommandManager() {
        this(null);
    }

    public DefaultCommandManager(CommandRegistry parent) {
        setParent(parent);
    }

    public void setParent(CommandRegistry parent) {
        this.commandRegistry.setParent(parent);
    }

    public void setGlobalCommandIds(String[] globalCommandIds) {
        if (globalCommandIds.length == 0) {
            globalCommands = Collections.EMPTY_LIST;
        }
        else {
            this.globalCommands = new ArrayList(globalCommandIds.length);
            for (int i = 0; i < globalCommandIds.length; i++) {
                ActionCommand globalCommand = createTargetableActionCommand(
                        globalCommandIds[i], null);
                globalCommands.add(globalCommand);
            }
        }
    }

    public void setButtonFactory(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public ButtonFactory getButtonFactory() {
        if (buttonFactory == null) { return DefaultCommandServices.instance()
                .getButtonFactory(); }
        return buttonFactory;
    }

    public MenuFactory getMenuFactory() {
        if (menuFactory == null) { return DefaultCommandServices.instance()
                .getMenuFactory(); }
        return menuFactory;
    }

    public void setToolBarButtonConfigurer(CommandButtonConfigurer configurer) {
        this.toolBarButtonConfigurer = configurer;
    }

    public void setMenuItemButtonConfigurer(CommandButtonConfigurer configurer) {
        this.menuItemButtonConfigurer = configurer;
    }

    public CommandButtonConfigurer getDefaultButtonConfigurer() {
        return DefaultCommandServices.instance().getDefaultButtonConfigurer();
    }

    public CommandButtonConfigurer getToolBarButtonConfigurer() {
        if (toolBarButtonConfigurer == null) { return DefaultCommandServices
                .instance().getToolBarButtonConfigurer(); }
        return toolBarButtonConfigurer;
    }

    public CommandButtonConfigurer getMenuItemButtonConfigurer() {
        if (menuItemButtonConfigurer == null) { return DefaultCommandServices
                .instance().getMenuItemButtonConfigurer(); }
        return menuItemButtonConfigurer;
    }

    public CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
        if (pullDownMenuButtonConfigurer == null) { return DefaultCommandServices
                .instance().getPullDownMenuButtonConfigurer(); }
        return pullDownMenuButtonConfigurer;
    }

    public ActionCommand getActionCommand(String commandId) {
        return commandRegistry.getActionCommand(commandId);
    }

    public Iterator getGlobalCommands() {
        if (globalCommands == null) { return Collections.EMPTY_LIST.iterator(); }
        return globalCommands.iterator();
    }

    public CommandGroup getCommandGroup(String groupId) {
        return commandRegistry.getCommandGroup(groupId);
    }

    public boolean containsCommandGroup(String groupId) {
        return commandRegistry.containsCommandGroup(groupId);
    }

    public boolean containsActionCommand(String commandId) {
        return commandRegistry.containsActionCommand(commandId);
    }

    /**
     * Adds a new command to this manager's registry; also configures the
     * command on behalf of the caller.
     * 
     * @param command
     *            the new command to be configured and registered
     */
    public void addNewCommand(AbstractCommand command) {
        addNewCommand(command, command.getId());
    }

    /**
     * Adds a new command to this manager's registry; also configures the
     * command on behalf of the caller. Configures the command's face (visual
     * appearance) using the provided face configuration key, which delegates to
     * a configured ObjectConfigurer.
     * 
     * @param command
     * @param faceConfigurationKey
     */
    public void addNewCommand(AbstractCommand command,
            String faceConfigurationKey) {
        if (logger.isDebugEnabled()) {
            logger.debug("Configuring and adding new command '"
                    + command.getId() + "'");
        }
        configure(command, faceConfigurationKey);
        registerCommand(command);
    }

    public void addCommandInterceptor(String commandId,
            ActionCommandInterceptor interceptor) {
        getActionCommand(commandId).addCommandInterceptor(interceptor);
    }

    public void removeCommandInterceptor(String commandId,
            ActionCommandInterceptor interceptor) {
        getActionCommand(commandId).removeCommandInterceptor(interceptor);
    }

    public void registerCommand(AbstractCommand command) {
        commandRegistry.registerCommand(command);
    }

    public void setTargetableActionCommandExecutor(String commandId,
            ActionCommandExecutor delegate) {
        commandRegistry.setTargetableActionCommandExecutor(commandId, delegate);
    }

    public void addCommandRegistryListener(CommandRegistryListener l) {
        this.commandRegistry.addCommandRegistryListener(l);
    }

    public void removeCommandRegistryListener(CommandRegistryListener l) {
        this.commandRegistry.removeCommandRegistryListener(l);
    }

    public TargetableActionCommand createTargetableActionCommand(
            String commandId, ActionCommandExecutor delegate) {
        Assert.notNull(commandId,
                "Registered targetable action commands must have an id.");
        TargetableActionCommand newCommand = new TargetableActionCommand(
                commandId, delegate);
        configure(newCommand);
        registerCommand(newCommand);
        return newCommand;
    }

    public CommandGroup createCommandGroup(String groupId, Object[] members) {
        Assert.notNull(groupId, "Registered command groups must have an id.");
        CommandGroup newGroup = new CommandGroupFactoryBean(groupId,
                this.commandRegistry, this, members).getCommandGroup();
        addNewCommand(newGroup);
        return newGroup;
    }

    public ExclusiveCommandGroup createExclusiveCommandGroup(String groupId,
            Object[] members) {
        Assert.notNull(groupId,
                "Registered exclusive command groups must have an id.");
        CommandGroupFactoryBean newGroupFactory = new CommandGroupFactoryBean(
                groupId, this.commandRegistry, this, members);
        newGroupFactory.setExclusive(true);
        addNewCommand(newGroupFactory.getCommandGroup());
        return (ExclusiveCommandGroup)newGroupFactory.getCommandGroup();
    }

    public AbstractCommand configure(AbstractCommand command) {
        return commandConfigurer.configure(command);
    }

    public AbstractCommand configure(AbstractCommand command,
            String faceConfigurationKey) {
        return commandConfigurer.configure(command, faceConfigurationKey);
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof CommandGroupFactoryBean) {
            CommandGroupFactoryBean factory = (CommandGroupFactoryBean)bean;
            CommandGroup group = factory.getCommandGroup();
            addNewCommand(group);
        }
        else if (bean instanceof AbstractCommand) {
            registerCommand((AbstractCommand)bean);
        }
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof CommandGroupFactoryBean) {
            CommandGroupFactoryBean factory = (CommandGroupFactoryBean)bean;
            factory.setCommandRegistry(commandRegistry);
        }
        else if (bean instanceof AbstractCommand) {
            configure((AbstractCommand)bean);
        }
        return bean;
    }

}