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

import java.awt.Container;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandButtonIconInfo;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.support.CommandButtonManager;
import org.springframework.richclient.command.support.DefaultCommandServices;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractCommand extends AbstractPropertyChangePublisher
        implements InitializingBean, BeanNameAware, Guarded {

    protected final Log logger = LogFactory.getLog(getClass());

    public static final String ENABLED_PROPERTY_NAME = "enabled";

    public static final String VISIBLE_PROPERTY_NAME = "visible";

    private String id;

    private boolean enabled = true;

    private boolean visible = true;

    private CommandButtonManager buttonManager;

    private CommandFaceDescriptor faceDescriptor;

    private CommandServices commandServices = DefaultCommandServices.instance();

    private SwingPropertyChangeSupport pcs;

    protected AbstractCommand() {
    }

    protected AbstractCommand(String id) {
        this(id, (CommandFaceDescriptor)null);
    }

    protected AbstractCommand(String id, String encodedLabel) {
        this(id, new CommandFaceDescriptor(encodedLabel));
    }

    protected AbstractCommand(String id, String encodedLabel, Icon icon,
            String caption) {
        this(id, new CommandFaceDescriptor(encodedLabel, icon, caption));
    }

    protected AbstractCommand(String id, CommandFaceDescriptor face) {
        super();
        setId(id);
        if (face != null) {
            setFaceDescriptor(face);
        }
    }

    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        if (!StringUtils.hasText(id)) {
            id = null;
        }
        this.id = id;
    }

    public void setFaceDescriptor(CommandFaceDescriptor face) {
        Assert.notNull(face);
        this.faceDescriptor = face;
        if (buttonManager != null) {
            buttonManager.setFaceDescriptor(this.faceDescriptor);
        }
    }

    protected CommandFaceDescriptor getFaceDescriptor() {
        return faceDescriptor;
    }

    protected CommandFaceDescriptor getOrCreateFaceDescriptor() {
        if (faceDescriptor == null) {
            if (logger.isInfoEnabled()) {
                logger
                        .info("Lazily instantiating face descriptor on behalf of caller to prevent npe; "
                                + "command is being configured manually, right?");
            }
            setFaceDescriptor(new CommandFaceDescriptor());
        }
        return faceDescriptor;
    }

    public boolean isFaceConfigured() {
        return faceDescriptor != null;
    }

    public String getText() {
        if (faceDescriptor != null) { return getFaceDescriptor().getText(); }
        return CommandFaceDescriptor.EMPTY_LABEL.getText();
    }

    public void setLabel(String encodedLabel) {
        getOrCreateFaceDescriptor().setCommandButtonLabelInfo(encodedLabel);
    }

    public void setLabel(CommandButtonLabelInfo label) {
        getOrCreateFaceDescriptor().setCommandButtonLabelInfo(label);
    }

    public void setCaption(String shortDescription) {
        getOrCreateFaceDescriptor().setCaption(shortDescription);
    }

    public void setIcon(Icon icon) {
        getOrCreateFaceDescriptor().setIcon(icon);
    }

    public void setIconInfo(CommandButtonIconInfo iconInfo) {
        getOrCreateFaceDescriptor().setCommandButtonIconInfo(iconInfo);
    }

    public void setCommandServices(CommandServices services) {
        if (services == null) {
            this.commandServices = DefaultCommandServices.instance();
        }
        else {
            this.commandServices = services;
        }
    }

    protected CommandServices getCommandServices() {
        return this.commandServices;
    }

    public void afterPropertiesSet() {
        if (getId() == null) {
            logger
                    .info("Command "
                            + this
                            + " has no set id; note: anonymous commands cannot be used in registries.");
        }
        Assert.notNull(commandServices,
            "The commandServices property must be set and non-null.");
        if (this instanceof ActionCommand && faceDescriptor == null) {
            logger
                    .warn("The face descriptor property is not yet set for action command '"
                            + getId()
                            + "'; command won't render correctly until this is configured!");
        }
    }

    public void setBeanName(String name) {
        if (getId() == null) {
            setId(name);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            Iterator it = buttonIterator();
            while (it.hasNext()) {
                AbstractButton button = (AbstractButton)it.next();
                button.setEnabled(enabled);
            }
            firePropertyChange(ENABLED_PROPERTY_NAME, !enabled, enabled);
        }
    }

    protected Iterator buttonIterator() {
        return getButtonManager().iterator();
    }

    public boolean isAnonymous() {
        return id == null;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean value) {
        if (visible != value) {
            this.visible = value;
            for (Iterator it = buttonIterator(); it.hasNext();) {
                AbstractButton button = (AbstractButton)it.next();
                button.setVisible(visible);
            }
            firePropertyChange(VISIBLE_PROPERTY_NAME, !visible, visible);
        }
    }

    public AbstractButton createButton() {
        return createButton(getButtonFactory());
    }

    public AbstractButton createButton(ButtonFactory buttonFactory) {
        return createButton(buttonFactory, getDefaultButtonConfigurer());
    }

    public AbstractButton createButton(ButtonFactory buttonFactory,
            CommandButtonConfigurer buttonConfigurer) {
        JButton button = buttonFactory.createButton();
        attach(button, buttonConfigurer);
        return button;
    }

    public JMenuItem createMenuItem() {
        return createMenuItem(getMenuFactory());
    }

    public JMenuItem createMenuItem(MenuFactory menuFactory) {
        JMenuItem menuItem = menuFactory.createMenuItem();
        attach(menuItem, getMenuItemButtonConfigurer());
        return menuItem;
    }

    public void attach(AbstractButton button) {
        attach(button, getCommandServices().getDefaultButtonConfigurer());
    }

    public void attach(AbstractButton button, CommandButtonConfigurer configurer) {
        getButtonManager().attachAndConfigure(button, configurer);
        onButtonAttached(button);
    }

    protected void onButtonAttached(AbstractButton button) {
        if (logger.isDebugEnabled()) {
            logger.debug("Configuring newly attached button for command '"
                    + getId() + "' enabled=" + isEnabled() + ", visible="
                    + isVisible());
        }
        button.setEnabled(isEnabled());
        button.setVisible(isVisible());
    }

    public void detach(AbstractButton button) {
        if (getButtonManager().isAttachedTo(button)) {
            getButtonManager().detach(button);
            onButtonDetached();
        }
    }

    public boolean isAttached(AbstractButton b) {
        return getButtonManager().isAttachedTo(b);
    }

    protected void onButtonDetached() {

    }

    private CommandButtonManager getButtonManager() {
        if (buttonManager == null) {
            buttonManager = new CommandButtonManager(faceDescriptor);
        }
        return buttonManager;
    }

    protected CommandButtonConfigurer getDefaultButtonConfigurer() {
        return getCommandServices().getDefaultButtonConfigurer();
    }

    protected CommandButtonConfigurer getToolBarButtonConfigurer() {
        return getCommandServices().getToolBarButtonConfigurer();
    }

    protected CommandButtonConfigurer getMenuItemButtonConfigurer() {
        return getCommandServices().getMenuItemButtonConfigurer();
    }

    protected ButtonFactory getButtonFactory() {
        return getCommandServices().getButtonFactory();
    }

    protected MenuFactory getMenuFactory() {
        return getCommandServices().getMenuFactory();
    }

    public boolean requestFocusIn(Container container) {
        AbstractButton button = getButtonIn(container);
        if (button != null) {
            return button.requestFocusInWindow();
        }
        else {
            return false;
        }
    }

    public AbstractButton getButtonIn(Container container) {
        Iterator it = buttonIterator();
        while (it.hasNext()) {
            AbstractButton button = (AbstractButton)it.next();
            if (SwingUtilities.isDescendingFrom(button, container)) { return button; }
        }
        return null;
    }

}