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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandButtonIconInfo;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.CommandFaceDescriptorRegistry;
import org.springframework.richclient.command.support.CommandFaceButtonManager;
import org.springframework.richclient.core.SecurityControllable;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;
import org.springframework.util.StringUtils;

public abstract class AbstractCommand extends AbstractPropertyChangePublisher implements InitializingBean,
        BeanNameAware, GuardedActionCommandExecutor, SecurityControllable {

    public static final String ENABLED_PROPERTY_NAME = "enabled";

    public static final String VISIBLE_PROPERTY_NAME = "visible";

    private static final String DEFAULT_FACE_DESCRIPTOR_ID = "default";

    private String id;

    private String defaultFaceDescriptorId = DEFAULT_FACE_DESCRIPTOR_ID;

    private boolean enabled = true;

    private boolean visible = true;
    
    private boolean authorized = true;
    
    private String securityControllerId = null;

    private Map faceButtonManagers;

    private CommandServices commandServices;

    private CommandFaceDescriptorRegistry faceDescriptorRegistry;

    private Boolean oldEnabledState;

    private Boolean oldVisibleState;

    protected AbstractCommand() {
        this(null);
    }

    protected AbstractCommand(String id) {
        super();
        setId(id);
        addEnabledListener(new ButtonEnablingListener()); // keep track of enable state for buttons
        addPropertyChangeListener(VISIBLE_PROPERTY_NAME, new ButtonVisibleListener()); // keep track of visible state for buttons
    }

    protected AbstractCommand(String id, String encodedLabel) {
        this(id, new CommandFaceDescriptor(encodedLabel));
    }

    protected AbstractCommand(String id, String encodedLabel, Icon icon, String caption) {
        this(id, new CommandFaceDescriptor(encodedLabel, icon, caption));
    }

    protected AbstractCommand(String id, CommandFaceDescriptor faceDescriptor) {
        this(id);
        if (faceDescriptor != null) {
            setFaceDescriptor(faceDescriptor);
        }
    }

    protected AbstractCommand(String id, Map faceDescriptors) {
        this(id);
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
        setFaceDescriptor(getDefaultFaceDescriptorId(), faceDescriptor);
    }

    public void setFaceDescriptor(String faceDescriptorId, CommandFaceDescriptor faceDescriptor) {
        getButtonManager(faceDescriptorId).setFaceDescriptor(faceDescriptor);
    }

    public void setFaceDescriptors(Map faceDescriptors) {
        Assert.notNull(faceDescriptors);
        Iterator it = faceDescriptors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String faceDescriptorId = (String)entry.getKey();
            CommandFaceDescriptor faceDescriptor = (CommandFaceDescriptor)entry.getValue();
            setFaceDescriptor(faceDescriptorId, faceDescriptor);
        }
    }

    public void setDefaultFaceDescriptorId(String defaultFaceDescriptorId) {
        this.defaultFaceDescriptorId = defaultFaceDescriptorId;
    }

    public void setFaceDescriptorRegistry(CommandFaceDescriptorRegistry faceDescriptorRegistry) {
        this.faceDescriptorRegistry = faceDescriptorRegistry;
    }

    public void setCommandServices(CommandServices services) {
        this.commandServices = services;
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

    public void afterPropertiesSet() {
        if (getId() == null) {
            logger.info("Command " + this + " has no set id; note: anonymous commands cannot be used in registries.");
        }
        if (this instanceof ActionCommand && !isFaceConfigured()) {
            logger.warn("The face descriptor property is not yet set for action command '" + getId()
                    + "'; command won't render correctly until this is configured");
        }
    }

    private CommandFaceDescriptor getOrCreateFaceDescriptor() {
        if (!isFaceConfigured()) {
            if (logger.isInfoEnabled()) {
                logger.info("Lazily instantiating default face descriptor on behalf of caller to prevent npe; "
                        + "command is being configured manually, right?");
            }
            setFaceDescriptor(new CommandFaceDescriptor());
        }
        return getFaceDescriptor();
    }

    public String getDefaultFaceDescriptorId() {
        if (!StringUtils.hasText(defaultFaceDescriptorId)) {
            return DEFAULT_FACE_DESCRIPTOR_ID;
        }
        return defaultFaceDescriptorId;
    }

    protected CommandFaceDescriptor getFaceDescriptor() {
        return getDefaultButtonManager().getFaceDescriptor();
    }

    public boolean isFaceConfigured() {
        return getDefaultButtonManager().isFaceConfigured();
    }

    public String getText() {
        if (isFaceConfigured()) {
            return getFaceDescriptor().getText();
        }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getText();
    }

    public int getMnemonic() {
        if (isFaceConfigured()) {
            return getFaceDescriptor().getMnemonic();
        }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getMnemonic();
    }

    public int getMnemonicIndex() {
        if (isFaceConfigured()) {
            return getFaceDescriptor().getMnemonicIndex();
        }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getMnemonicIndex();
    }

    public KeyStroke getAccelerator() {
        if (isFaceConfigured()) {
            return getFaceDescriptor().getAccelerator();
        }
        return LabelInfoFactory.BLANK_BUTTON_LABEL.getAccelerator();
    }

    public CommandFaceDescriptorRegistry getFaceDescriptorRegistry() {
        return faceDescriptorRegistry;
    }

    protected CommandServices getCommandServices() {
        if (commandServices == null) {
            commandServices = (CommandServices) ApplicationServicesLocator.services().getService(CommandServices.class);
        }
        return this.commandServices;
    }

    /**
     * Set the Id of the security controller that should manage this object.
     * @param controllerId Id (bean name) of the security controller
     */
    public void setSecurityControllerId(String controllerId) {
        this.securityControllerId = controllerId;
    }

    /**
     * Get the id (bean name) of the security controller that should manage this object.
     * @return controller id
     */
    public String getSecurityControllerId() {
        return securityControllerId;
    }

    /**
     * Set the authorized state.  Setting authorized to false will override any call
     * to {@link #setEnabled(boolean)}.  As long as this object is unauthorized,
     * it can not be enabled.
     * @param authorized Pass <code>true</code> if the object is to be authorized
     */
    public void setAuthorized(boolean authorized) {
        boolean wasAuthorized = isAuthorized();
        if (hasChanged(wasAuthorized, authorized)) {
            this.authorized = authorized;
            firePropertyChange(AUTHORIZED_PROPERTY, wasAuthorized, authorized);
            updatedEnabledState();
        }
    }
    
    /**
     * Get the authorized state.
     * @return authorized
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Get the enabled state
     * @return if the command is enabled and {@link #isAuthorized()} returns true
     * @see #isAuthorized()
     */
    public boolean isEnabled() {
        return enabled && isAuthorized();
    }
    
    /**
     * This method is called when any predicate for enabled state has changed. This implemetation fires the enabled
     * changed event if the return value of {@link #isEnabled()} has changed.
     * <p>
     * Sublcasses which have an additional predicate to enabled state must call this method if the state of the
     * predicate changes.
     */
    protected void updatedEnabledState() {
        boolean isEnabled = isEnabled();
        if(oldEnabledState == null || hasChanged(oldEnabledState.booleanValue(), isEnabled)) {
            firePropertyChange(ENABLED_PROPERTY_NAME, oldEnabledState == null ? !isEnabled : oldEnabledState
                    .booleanValue(), isEnabled);
        }
        oldEnabledState = Boolean.valueOf(isEnabled);
    }

    /**
     * Set the enabled state of this command.  Note that if we are currently not
     * authorized, then the new value will just be recorded and no change in the
     * current enabled state will be made.
     * @param enabled state
     */
    public void setEnabled(boolean enabled) {
        if( hasChanged(this.enabled, enabled)) {
            this.enabled = enabled;
            updatedEnabledState();
        }
    }

    /**
     * Listener to keep track of enabled state. When enable on command changes,
     * each button has to be checked and set.
     */
    private class ButtonEnablingListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            // We need to keep the buttons in sync with the command, so go through the buttons and set Enabled state.
            // alternative is to add a listener to the enabled value and change buttons in that listener 
            // NOT redundant
            boolean enabled = evt.getNewValue() == Boolean.TRUE ? true : false;
            Iterator it = buttonIterator();
            while (it.hasNext()) 
            {
                 AbstractButton button = (AbstractButton)it.next();
                 button.setEnabled(enabled);
            }
        }
    }

    /**
     * Listener to keep track of visible state. When visible on command changes,
     * each button has to be checked and set.
     */
    private class ButtonVisibleListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            // We need to keep the buttons in sync with the command, so go through the buttons and set visible state.
            // alternative is to add a listener to the visible value and change buttons in that listener 
            // NOT redundant
            boolean enabled = evt.getNewValue() == Boolean.TRUE ? true : false;
            Iterator it = buttonIterator();
            while (it.hasNext()) 
            {
                 AbstractButton button = (AbstractButton)it.next();
                 button.setVisible(enabled);
            }
        }
    }

    public void addEnabledListener(PropertyChangeListener listener) {
        addPropertyChangeListener(ENABLED_PROPERTY_NAME, listener);
    }

    public void removeEnabledListener(PropertyChangeListener listener) {
        removePropertyChangeListener(ENABLED_PROPERTY_NAME, listener);
    }

    protected final Iterator defaultButtonIterator() {
        return getDefaultButtonManager().iterator();
    }
    
    protected final Iterator buttonIterator() {
        
        if (this.faceButtonManagers == null) 
            return Collections.EMPTY_SET.iterator();
        
        return new NestedButtonIterator(this.faceButtonManagers.values().iterator());
    }
    
    private static final class NestedButtonIterator implements Iterator
    {
        private final Iterator managerIterator;
        private Iterator currentButtonIterator;
        private AbstractButton nextButton;
        
        NestedButtonIterator(Iterator it)
        {
            this.managerIterator = it;
            preFetchNextButton();
        }
        
        public boolean hasNext()
        {
            return nextButton != null;
        }

        public Object next()
        {
            if (nextButton == null) {
                throw new NoSuchElementException();
            }
            AbstractButton lastButton = nextButton;
            preFetchNextButton();
            return lastButton;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Can't use a button-iterator on AbstractCommand to remove buttons.");   
        }
        
        private void preFetchNextButton()
        {
            while (this.currentButtonIterator == null || !this.currentButtonIterator.hasNext())
            {
                if (this.managerIterator.hasNext()){
                    CommandFaceButtonManager cfbm = (CommandFaceButtonManager)this.managerIterator.next();
                    this.currentButtonIterator = cfbm.iterator();
                }
                else
                {
                    this.currentButtonIterator = null;
                    this.nextButton = null;
                    return;
                }
            }
                
            if (this.currentButtonIterator.hasNext())
                nextButton = (AbstractButton)this.currentButtonIterator.next();
            else
                nextButton = null;            
        }
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
            updatedVisibleState();
        }
    }
    
    /**
     * This method is called when any predicate for visible state has changed. This implemetation fires the visible
     * changed event if the return value of {@link #isVisible()} has changed.
     * <p>
     * Sublcasses which have an additional predicate to visible state must call this method if the state of the
     * predicate changes.
     */
    protected void updatedVisibleState() {
        boolean isVisible = isVisible();
        if(oldVisibleState == null || hasChanged(oldVisibleState.booleanValue(), isVisible)) {
            firePropertyChange(VISIBLE_PROPERTY_NAME, oldVisibleState == null ? !isVisible : oldVisibleState.booleanValue(), isVisible);
        }
        oldVisibleState = Boolean.valueOf(isVisible);
    }

    public final AbstractButton createButton() {
        return createButton(getDefaultFaceDescriptorId(), getButtonFactory(), getDefaultButtonConfigurer());
    }

    public final AbstractButton createButton(String faceDescriptorId) {
        return createButton(faceDescriptorId, getButtonFactory(), getDefaultButtonConfigurer());
    }

    public final AbstractButton createButton(ButtonFactory buttonFactory) {
        return createButton(getDefaultFaceDescriptorId(), buttonFactory, getDefaultButtonConfigurer());
    }

    public final AbstractButton createButton(String faceDescriptorId, ButtonFactory buttonFactory) {
        return createButton(faceDescriptorId, buttonFactory, getDefaultButtonConfigurer());
    }

    public final AbstractButton createButton(ButtonFactory buttonFactory, CommandButtonConfigurer buttonConfigurer) {
        return createButton(getDefaultFaceDescriptorId(), buttonFactory, buttonConfigurer);
    }

    public AbstractButton createButton(String faceDescriptorId, ButtonFactory buttonFactory,
            CommandButtonConfigurer buttonConfigurer) {
        JButton button = buttonFactory.createButton();
        attach(button, faceDescriptorId, buttonConfigurer);
        return button;
    }

    public final JMenuItem createMenuItem() {
        return createMenuItem(getDefaultFaceDescriptorId(), getMenuFactory(), getMenuItemButtonConfigurer());
    }

    public final JMenuItem createMenuItem(String faceDescriptorId) {
        return createMenuItem(faceDescriptorId, getMenuFactory(), getMenuItemButtonConfigurer());
    }

    public final JMenuItem createMenuItem(MenuFactory menuFactory) {
        return createMenuItem(getDefaultFaceDescriptorId(), menuFactory, getMenuItemButtonConfigurer());
    }

    public final JMenuItem createMenuItem(String faceDescriptorId, MenuFactory menuFactory) {
        return createMenuItem(faceDescriptorId, menuFactory, getMenuItemButtonConfigurer());
    }

    public final JMenuItem createMenuItem(MenuFactory menuFactory, CommandButtonConfigurer buttonConfigurer) {
        return createMenuItem(getDefaultFaceDescriptorId(), menuFactory, buttonConfigurer);
    }

    public JMenuItem createMenuItem(String faceDescriptorId, MenuFactory menuFactory,
            CommandButtonConfigurer buttonConfigurer) {
        JMenuItem menuItem = menuFactory.createMenuItem();
        attach(menuItem, faceDescriptorId, buttonConfigurer);
        return menuItem;
    }

    public void attach(AbstractButton button) {
        attach(button, getDefaultFaceDescriptorId(), getCommandServices().getDefaultButtonConfigurer());
    }

    public void attach(AbstractButton button, CommandButtonConfigurer configurer) {
        attach(button, getDefaultFaceDescriptorId(), configurer);
    }

    public void attach(AbstractButton button, String faceDescriptorId, CommandButtonConfigurer configurer) {
        getButtonManager(faceDescriptorId).attachAndConfigure(button, configurer);
        onButtonAttached(button);
    }

    protected void onButtonAttached(AbstractButton button) {
        if (logger.isDebugEnabled()) {
            logger.debug("Configuring newly attached button for command '" + getId() + "' enabled=" + isEnabled()
                    + ", visible=" + isVisible());
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
        // default no op, subclasses may override
    }

    private CommandFaceButtonManager getDefaultButtonManager() {
        return getButtonManager(getDefaultFaceDescriptorId());
    }

    private CommandFaceButtonManager getButtonManager(String faceDescriptorId) {
        if (this.faceButtonManagers == null) {
            this.faceButtonManagers = new CachingMapDecorator() {
                protected Object create(Object key) {
                    return new CommandFaceButtonManager(AbstractCommand.this, (String)key);
                }
            };
        }
        CommandFaceButtonManager m = (CommandFaceButtonManager)this.faceButtonManagers.get(faceDescriptorId);
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
        return false;
    }

    public AbstractButton getButtonIn(Container container) {
        Iterator it = buttonIterator();
        while (it.hasNext()) {
            AbstractButton button = (AbstractButton)it.next();
            if (SwingUtilities.isDescendingFrom(button, container)) {
                return button;
            }
        }
        return null;
    }

    public String toString() {
        return new ToStringCreator(this).append("id", getId()).append("enabled", enabled).append("visible", visible)
                .append("defaultFaceDescriptorId", defaultFaceDescriptorId).toString();
    }

}