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
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.ExclusiveCommandGroup;
import org.springframework.richclient.command.TargetableActionCommand;
import org.springframework.richclient.command.config.ApplicationCommandConfigurer;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class DefaultCommandManager implements CommandManager, BeanPostProcessor {
	private final Log logger = LogFactory.getLog(getClass());

	private DefaultCommandRegistry commandRegistry = new DefaultCommandRegistry();

	private CommandServices commandServices = DefaultCommandServices.instance();

	private CommandConfigurer commandConfigurer = new ApplicationCommandConfigurer(this);

	public DefaultCommandManager() {

	}

	public DefaultCommandManager(CommandRegistry parent) {
		setParent(parent);
	}

	public DefaultCommandManager(CommandServices commandServices) {
		setCommandServices(commandServices);
	}

	public void setCommandServices(CommandServices commandServices) {
		Assert.notNull(commandServices, "A command services implementation is required");
		this.commandServices = commandServices;
	}

	public void setParent(CommandRegistry parent) {
		commandRegistry.setParent(parent);
	}

	public ButtonFactory getButtonFactory() {
		return commandServices.getButtonFactory();
	}

	public MenuFactory getMenuFactory() {
		return commandServices.getMenuFactory();
	}

	public CommandButtonConfigurer getDefaultButtonConfigurer() {
		return commandServices.getDefaultButtonConfigurer();
	}

	public CommandButtonConfigurer getToolBarButtonConfigurer() {
		return commandServices.getToolBarButtonConfigurer();
	}

	public CommandButtonConfigurer getMenuItemButtonConfigurer() {
		return commandServices.getMenuItemButtonConfigurer();
	}

	public CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
		return commandServices.getPullDownMenuButtonConfigurer();
	}

	public CommandFaceDescriptor getFaceDescriptor(AbstractCommand command, String faceDescriptorKey) {
		// TODO
		return null;
	}

	public ActionCommand getActionCommand(String commandId) {
		return commandRegistry.getActionCommand(commandId);
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
	 * @param faceDescriptorKey
	 */
	public void addNewCommand(AbstractCommand command, String faceDescriptorKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("Configuring and adding new command '" + command.getId() + "'");
		}
		configure(command, faceDescriptorKey);
		registerCommand(command);
	}

	public void addCommandInterceptor(String commandId, ActionCommandInterceptor interceptor) {
		getActionCommand(commandId).addCommandInterceptor(interceptor);
	}

	public void removeCommandInterceptor(String commandId, ActionCommandInterceptor interceptor) {
		getActionCommand(commandId).removeCommandInterceptor(interceptor);
	}

	public void registerCommand(AbstractCommand command) {
		commandRegistry.registerCommand(command);
	}

	public void setTargetableActionCommandExecutor(String commandId, ActionCommandExecutor executor) {
		commandRegistry.setTargetableActionCommandExecutor(commandId, executor);
	}

	public void addCommandRegistryListener(CommandRegistryListener l) {
		this.commandRegistry.addCommandRegistryListener(l);
	}

	public void removeCommandRegistryListener(CommandRegistryListener l) {
		this.commandRegistry.removeCommandRegistryListener(l);
	}

	public TargetableActionCommand createTargetableActionCommand(String commandId, ActionCommandExecutor delegate) {
		Assert.notNull(commandId, "Registered targetable action commands must have an id.");
		TargetableActionCommand newCommand = new TargetableActionCommand(commandId, delegate);
		configure(newCommand);
		registerCommand(newCommand);
		return newCommand;
	}

	public CommandGroup createCommandGroup(String groupId, Object[] members) {
		Assert.notNull(groupId, "Registered command groups must have an id.");
		CommandGroup newGroup = new CommandGroupFactoryBean(groupId, this.commandRegistry, this, members).getCommandGroup();
		addNewCommand(newGroup);
		return newGroup;
	}

	public ExclusiveCommandGroup createExclusiveCommandGroup(String groupId, Object[] members) {
		Assert.notNull(groupId, "Registered exclusive command groups must have an id.");
		CommandGroupFactoryBean newGroupFactory = new CommandGroupFactoryBean(groupId, this.commandRegistry, this, members);
		newGroupFactory.setExclusive(true);
		addNewCommand(newGroupFactory.getCommandGroup());
		return (ExclusiveCommandGroup)newGroupFactory.getCommandGroup();
	}

	public AbstractCommand configure(AbstractCommand command) {
		return commandConfigurer.configure(command);
	}

	public AbstractCommand configure(AbstractCommand command, String faceDescriptorKey) {
		return commandConfigurer.configure(command, faceDescriptorKey);
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
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