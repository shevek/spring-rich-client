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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
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
import org.springframework.richclient.command.config.CommandFaceDescriptorRegistry;
import org.springframework.richclient.command.support.CommandButtonManager;
import org.springframework.richclient.command.support.DefaultCommandServices;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.ToStringCreator;

public abstract class AbstractCommand extends AbstractPropertyChangePublisher
        implements InitializingBean, BeanNameAware, Guarded {

    protected final Log logger = LogFactory.getLog(getClass());

    public static final String ENABLED_PROPERTY_NAME = "enabled";

    public static final String VISIBLE_PROPERTY_NAME = "visible";

    private static final String DEFAULT_FACE_DESCRIPTOR_KEY = "default";

    private String id;

    private String defaultFaceDescriptorKey = DEFAULT_FACE_DESCRIPTOR_KEY;

    private boolean enabled = true;

    private boolean visible = true;

    private Map faceButtonManagers;

    private CommandServices commandServices;

    private CommandFaceDescriptorRegistry faceDescriptorRegistry;

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

    protected AbstractCommand(String id, CommandFaceDescriptor faceDescriptor) {
        super();
        setId(id);
        if (faceDescriptor != null) {
            setFaceDescriptor(faceDescriptor);
        }
    }

    protected AbstractCommand(String id, Map faceDescriptors) {
        super();
        setId(id);
        setFaceDescriptors(faceDescriptors);
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

    public void setBeanName(String name) {
        if (getId() == null) {
            setId(name);
        }
    }

    public void setFaceDescriptor(CommandFaceDescriptor faceDescriptor) {
        setFaceDescriptor(getDefaultFaceDescriptorKey(), faceDescriptor);
    }

    public String getDefaultFaceDescriptorKey() {
        if (!StringUtils.hasText(defaultFaceDescriptorKey)) { return DEFAULT_FACE_DESCRIPTOR_KEY; }
        return defaultFaceDescriptorKey;
    }

    public void setDefaultFaceDescriptorKey(String defaultFaceDescriptorKey) {
        this.defaultFaceDescriptorKey = defaultFaceDescriptorKey;
    }

    public void setFaceDescriptors(Map faceDescriptors) {
        Iterator it = faceDescriptors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String faceDescriptorKey = (String)entry.getKey();
            CommandFaceDescriptor faceDescriptor = (CommandFaceDescriptor)entry
                    .getValue();
            setFaceDescriptor(faceDescriptorKey, faceDescriptor);
        }
    }

    public void setFaceDescriptor(String faceDescriptorKey,
            CommandFaceDescriptor faceDescriptor) {
        getButtonManager(faceDescriptorKey).setFaceDescriptor(faceDescriptor);
    }

    protected CommandFaceDescriptor getFaceDescriptor() {
        return getDefaultButtonManager().getFaceDescriptor();
    }

    public boolean isFaceConfigured() {
        return getDefaultButtonManager().isFaceSet();
    }

    public String getText() {
        if (isFaceConfigured()) { return getFaceDescriptor().getText(); }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getText();
    }

    public int getMnemonic() {
        if (isFaceConfigured()) { return getFaceDescriptor().getMnemonic(); }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getMnemonic();
    }

    public int getMnemonicIndex() {
        if (isFaceConfigured()) { return getFaceDescriptor().getMnemonicIndex(); }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getMnemonicIndex();
    }

    public KeyStroke getAccelerator() {
        if (isFaceConfigured()) { return getFaceDescriptor().getAccelerator(); }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getAccelerator();
    }

    public void setLabel(String encodedLabel) {
        getOrCreateFaceDescriptor().setButtonLabelInfo(encodedLabel);
    }

    public void setLabel(CommandButtonLabelInfo label) {
        getOrCreateFaceDescriptor().setLabelInfo(label);
    }

    public void setCaption(String shortDescription) {
        getOrCreateFaceDescriptor().setCaption(shortDescription);
    }

    public void setIcon(Icon icon) {
        getOrCreateFaceDescriptor().setIcon(icon);
    }

    public void setIconInfo(CommandButtonIconInfo iconInfo) {
        getOrCreateFaceDescriptor().setIconInfo(iconInfo);
    }

    private CommandFaceDescriptor getOrCreateFaceDescriptor() {
        if (!isFaceConfigured()) {
            if (logger.isInfoEnabled()) {
                logger
                        .info("Lazily instantiating default face descriptor on behalf of caller to prevent npe; "
                                + "command is being configured manually, right?");
            }
            setFaceDescriptor(new CommandFaceDescriptor());
        }
        return getFaceDescriptor();
    }

    public void setCommandServices(CommandServices services) {
        this.commandServices = services;
    }

    public CommandFaceDescriptorRegistry getFaceDescriptorRegistry() {
        Assert.state(faceDescriptorRegistry != null,
                "The faceDescriptorRegistry property is not set");
        return faceDescriptorRegistry;
    }

    public void setFaceDescriptorRegistry(
            CommandFaceDescriptorRegistry faceDescriptorRegistry) {
        this.faceDescriptorRegistry = faceDescriptorRegistry;
    }

    protected CommandServices getCommandServices() {
        if (commandServices == null) { return DefaultCommandServices.instance(); }
        return this.commandServices;
    }

    public void afterPropertiesSet() {
        if (getId() == null) {
            logger
                    .info("Command "
                            + this
                            + " has no set id; note: anonymous commands cannot be used in registries.");
        }
        if (this instanceof ActionCommand && !isFaceConfigured()) {
            logger
                    .warn("The face descriptor property is not yet set for action command '"
                            + getId()
                            + "'; command won't render correctly until this is configured");
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (hasChanged(this.enabled, enabled)) {
            this.enabled = enabled;
            Iterator it = buttonIterator();
            while (it.hasNext()) {
                AbstractButton button = (AbstractButton)it.next();
                button.setEnabled(enabled);
            }
            firePropertyChange(ENABLED_PROPERTY_NAME, !enabled, enabled);
        }
    }

    protected final Iterator buttonIterator() {
        return getDefaultButtonManager().iterator();
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
        return createButton(getDefaultFaceDescriptorKey(), getButtonFactory(),
                getDefaultButtonConfigurer());
    }

    public AbstractButton createButton(String faceDescriptorKey) {
        return createButton(faceDescriptorKey, getButtonFactory(),
                getDefaultButtonConfigurer());
    }

    public AbstractButton createButton(ButtonFactory buttonFactory) {
        return createButton(getDefaultFaceDescriptorKey(), buttonFactory,
                getDefaultButtonConfigurer());
    }

    public AbstractButton createButton(String faceDescriptorKey,
            ButtonFactory buttonFactory) {
        return createButton(faceDescriptorKey, buttonFactory,
                getDefaultButtonConfigurer());
    }

    public AbstractButton createButton(ButtonFactory buttonFactory,
            CommandButtonConfigurer buttonConfigurer) {
        return createButton(getDefaultFaceDescriptorKey(), buttonFactory,
                buttonConfigurer);
    }

    public AbstractButton createButton(String faceDescriptorKey,
            ButtonFactory buttonFactory,
            CommandButtonConfigurer buttonConfigurer) {
        JButton button = buttonFactory.createButton();
        attach(button, buttonConfigurer);
        return button;
    }

    public JMenuItem createMenuItem() {
        return createMenuItem(getDefaultFaceDescriptorKey(), getMenuFactory(),
                getMenuItemButtonConfigurer());
    }

    public JMenuItem createMenuItem(String faceDescriptorKey) {
        return createMenuItem(faceDescriptorKey, getMenuFactory(),
                getMenuItemButtonConfigurer());
    }

    public JMenuItem createMenuItem(MenuFactory menuFactory) {
        return createMenuItem(getDefaultFaceDescriptorKey(), menuFactory,
                getMenuItemButtonConfigurer());
    }

    public JMenuItem createMenuItem(String faceDescriptorKey,
            MenuFactory menuFactory) {
        return createMenuItem(faceDescriptorKey, menuFactory,
                getMenuItemButtonConfigurer());
    }

    public JMenuItem createMenuItem(MenuFactory menuFactory,
            CommandButtonConfigurer buttonConfigurer) {
        return createMenuItem(getDefaultFaceDescriptorKey(), menuFactory,
                buttonConfigurer);
    }

    public JMenuItem createMenuItem(String faceDescriptorKey,
            MenuFactory menuFactory, CommandButtonConfigurer buttonConfigurer) {
        JMenuItem menuItem = menuFactory.createMenuItem();
        attach(menuItem, buttonConfigurer);
        return menuItem;
    }

    public void attach(AbstractButton button) {
        attach(button, getDefaultFaceDescriptorKey(), getCommandServices()
                .getDefaultButtonConfigurer());
    }

    public void attach(AbstractButton button, CommandButtonConfigurer configurer) {
        attach(button, getDefaultFaceDescriptorKey(), configurer);
    }

    public void attach(AbstractButton button, String faceDescriptorKey,
            CommandButtonConfigurer configurer) {
        getButtonManager(faceDescriptorKey).attachAndConfigure(button,
                configurer);
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
        if (getDefaultButtonManager().isAttachedTo(button)) {
            getDefaultButtonManager().detach(button);
            onButtonDetached();
        }
    }

    public boolean isAttached(AbstractButton b) {
        return getDefaultButtonManager().isAttachedTo(b);
    }

    protected void onButtonDetached() {

    }

    private CommandButtonManager getDefaultButtonManager() {
        return getButtonManager(getDefaultFaceDescriptorKey());
    }

    private CommandButtonManager getButtonManager(String faceDescriptorKey) {
        if (this.faceButtonManagers == null) {
            this.faceButtonManagers = new HashMap();
            /*
             * this.faceButtonManagers = new CachingMapTemplate() { protected
             * Object create(Object key) { System.out.println(id + ":'" + key +
             * "'"); return new CommandButtonManager(AbstractCommand.this,
             * (String)key); } };
             */
        }
        CommandButtonManager m = (CommandButtonManager)this.faceButtonManagers
                .get(faceDescriptorKey);
        if (m == null) {
            m = new CommandButtonManager(AbstractCommand.this,
                    faceDescriptorKey);
            faceButtonManagers.put(faceDescriptorKey, m);
        }
        return m;
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

    public String toString() {
        return new ToStringCreator(this).append("id", getId()).append(
                "enabled", enabled).append("visible", visible).append(
                "defaultFaceDescriptorKey", defaultFaceDescriptorKey)
                .toString();
    }

}