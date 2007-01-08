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
package org.springframework.richclient.application.config;

import java.awt.Image;
import java.util.Locale;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ServiceNotFoundException;
import org.springframework.richclient.command.config.CommandButtonIconInfo;
import org.springframework.richclient.command.config.CommandIconConfigurable;
import org.springframework.richclient.command.config.CommandLabelConfigurable;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.LabelConfigurable;
import org.springframework.richclient.core.SecurityControllable;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.image.config.IconConfigurable;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.richclient.security.SecurityController;
import org.springframework.richclient.security.SecurityControllerManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The default implementation of the {@link ApplicationObjectConfigurer} interface. 
 * 
 * This class makes use of several application services in order to determine the property values 
 * to be applied to objects being configured. For example, some string properties will be retrieved 
 * from the application's message resource bundle using a {@link MessageSource}. To configure an
 * object with images and icons, an {@link ImageSource} and {@link IconSource} respectively will
 * be used. Subclasses can modify this behaviour by overriding the 
 * {@link #configure(Object, String)} method but it may be more convenient to override some of
 * the various methods that deal specificly with objects that implement certain 'configurable' 
 * interfaces, such as {@link LabelConfigurable} or {@link TitleConfigurable}. See the javadoc of
 * the {@link #configure(Object, String)} method for more details.
 *   
 *  
 * @author Keith Donald
 * @author Kevin Stembridge
 */
public class DefaultApplicationObjectConfigurer implements ApplicationObjectConfigurer, BeanPostProcessor {
    
    private final Log logger = LogFactory.getLog(getClass());

    private static final String PRESSED_ICON_KEY = "pressedIcon";

    private static final String DISABLED_ICON_KEY = "disabledIcon";

    private static final String ROLLOVER_ICON_KEY = "rolloverIcon";

    private static final String SELECTED_ICON_KEY = "selectedIcon";

    private static final String ICON_KEY = "icon";

    private static final String IMAGE_KEY = "image";

    private static final String DESCRIPTION_KEY = "description";

    private static final String CAPTION_KEY = "caption";

    private boolean loadOptionalIcons = true;

    private MessageSource messageSource;

    private ImageSource imageSource;

    private IconSource iconSource;

    /**
     * Creates a new {@code DefaultApplicationObjectConfigurer} that will obtain required services
     * from the application services locator. 
     */
    public DefaultApplicationObjectConfigurer() {
        //do nothing
    }

    /**
     * Creates a new {@code DefaultApplicationObjectConfigurer} that will use the given message 
     * source. Other application services will be retrieved using the application services locator.
     *
     * @param messageSource The message source. May be null.
     */
    public DefaultApplicationObjectConfigurer(MessageSource messageSource) {
        this(messageSource, null, null);
    }

    /**
     * Creates a new {@code DefaultApplicationObjectConfigurer} that will use the given message
     * and image sources. Other application services will be retrieved using the application 
     * services locator.
     *
     * @param messageSource The message source. May be null.
     * @param imageSource The image source. May be null.
     */
    public DefaultApplicationObjectConfigurer(MessageSource messageSource, ImageSource imageSource) {
        this(messageSource, imageSource, null);
    }

    /**
     * Creates a new {@code DefaultApplicationObjectConfigurer} that will use the given message,
     * image and icon sources. If any of these services are null, they will be retrieved using the
     * application services locator.
     *
     * @param messageSource The message source. May be null.
     * @param imageSource The image source. May be null.
     * @param iconSource The icon source. May be null.
     */
    public DefaultApplicationObjectConfigurer(MessageSource messageSource, 
                                              ImageSource imageSource, 
                                              IconSource iconSource) {
        
        this.messageSource = messageSource;
        this.imageSource = imageSource;
        this.iconSource = iconSource;
        
    }

    public void setLoadOptionalIcons(boolean loadOptionalIcons) {
        this.loadOptionalIcons = loadOptionalIcons;
    }

    /**
     * Returns this instance's message source. If a source was not provided at construction, it will
     * be retrieved by the application services locator.
     *
     * @return The message source, never null.
     * 
     * @throws ServiceNotFoundException if a source was not provided at construction time and 
     * the application services locator cannot find an instance of a message source.
     */
    protected MessageSource getMessageSource() {
        
        if (messageSource == null) {
            messageSource = (MessageSource) ApplicationServicesLocator.services().getService(MessageSource.class);
        }
        
        return messageSource;
        
    }

    /**
     * Returns this instance's icon source. If a source was not provided at construction, it will
     * be retrieved by the application services locator.
     *
     * @return The icon source, never null.
     * 
     * @throws ServiceNotFoundException if a source was not provided at construction time and 
     * the application services locator cannot find an instance of an icon source.
     */
    protected IconSource getIconSource() {
        
        if (iconSource == null) {
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        }
        
        return iconSource;
        
    }

    /**
     * Returns this instance's image source. If a source was not provided at construction, it will
     * be retrieved by the application services locator.
     *
     * @return The image source, never null.
     * 
     * @throws ServiceNotFoundException if a source was not provided at construction time and 
     * the application services locator cannot find an instance of an image source.
     */
    protected ImageSource getImageSource() {
        
        if (imageSource == null) {
            imageSource = (ImageSource) ApplicationServicesLocator.services().getService(ImageSource.class);
        }
        
        return imageSource;
        
    }

    public Object configure(Object object, String objectName) {
        configureTitle(object, objectName);
        configureLabel(object, objectName);
        configureDescription(object, objectName);
        configureImageIcons(object, objectName);
        configureSecurityController(object, objectName);
        return object;
    }

    private void configureTitle(Object object, String objectName) {
        if (object instanceof TitleConfigurable) {
            TitleConfigurable titleable = (TitleConfigurable)object;
            titleable.setTitle(loadMessage(objectName, "title"));
        }
    }

    private void configureLabel(Object object, String objectName) {
        if (object instanceof LabelConfigurable) {
            LabelConfigurable labelable = (LabelConfigurable)object;
            String labelStr = loadMessage(objectName, "label");
            labelable.setLabelInfo(new LabelInfoFactory(labelStr).createLabelInfo());
        }
        else {
            if (object instanceof CommandLabelConfigurable) {
                CommandLabelConfigurable labelable = (CommandLabelConfigurable)object;
                String labelStr = loadMessage(objectName, "label");
                labelable.setLabelInfo(new LabelInfoFactory(labelStr).createButtonLabelInfo());
            }
        }
    }

    private void configureDescription(Object object, String objectName) {
        if (object instanceof DescriptionConfigurable) {
            DescriptionConfigurable config = (DescriptionConfigurable)object;
            String caption = loadMessage(objectName, CAPTION_KEY);
            if (StringUtils.hasText(caption)) {
                config.setCaption(caption);
            }
            String description = loadMessage(objectName, DESCRIPTION_KEY);
            if (StringUtils.hasText(description)) {
                config.setDescription(description);
            }
        }
    }

    private void configureImageIcons( Object object, String objectName ) {
        if( object instanceof ImageConfigurable ) {
            if( getImageSource() != null ) {
                ImageConfigurable imageable = (ImageConfigurable) object;
                imageable.setImage(loadImage(objectName, IMAGE_KEY));
            }
        }
        if( object instanceof IconConfigurable ) {
            if( getIconSource() != null ) {
                IconConfigurable iconable = (IconConfigurable) object;
                iconable.setIcon(loadOptionalIcon(objectName, ICON_KEY));
            }
        }
        if( object instanceof CommandIconConfigurable ) {
            if( getIconSource() != null ) {
                setIconInfo((CommandIconConfigurable) object, objectName);
                setLargeIconInfo((CommandIconConfigurable) object, objectName);
            }
        }
    }

    /**
     * Associates the given object with a security controller if it implements the
     * {@link SecurityControllable} interface.
     * @param object The object to be configured.
     * @param objectName The name (id) of the object.
     * @throws BeansException if a referenced security controller is not found or is of
     *             the wrong type
     */
    private void configureSecurityController(Object object, String objectName) throws BeansException {
        
        if (object instanceof SecurityControllable) {
            SecurityControllable controllable = (SecurityControllable) object;
            String controllerId = controllable.getSecurityControllerId();

            if (controllerId != null) {
                // Find the referenced controller.
                SecurityControllerManager manager = (SecurityControllerManager)ApplicationServicesLocator.services().getService(SecurityControllerManager.class);
                SecurityController controller = manager.getSecurityController( controllerId );

                if (logger.isDebugEnabled()) {
                    logger.debug("Lookup SecurityController with id [" + controllerId + "]");
                }

                // And add the object to the controlled object set
                if (controller != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("configuring SecurityControllable [" 
                                     + objectName 
                                     + "]; security controller id='"
                                     + controllerId 
                                     + "'");
                    }
                    controller.addControlledObject( controllable );
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("configuring SecurityControllable [" 
                                     + objectName 
                                     + "]; no security controller for id='"
                                     + controllerId 
                                     + "'");
                    }
                }
            } else {
                
                if (logger.isDebugEnabled()) {
                    logger.debug("configuring SecurityControllable [" 
                                 + objectName
                                 + "]; no security controller Id specified");
                }
                
            }
            
        }
        
    }

    public void setIconInfo(CommandIconConfigurable object, String objectName) {
        Icon icon = loadOptionalIcon(objectName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalIcon(objectName, SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalIcon(objectName, ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalIcon(objectName, DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(objectName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            object.setIconInfo(iconInfo);
        }
    }

    public void setLargeIconInfo(CommandIconConfigurable object, String objectName) {
        Icon icon = loadOptionalLargeIcon(objectName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalLargeIcon(objectName, SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalLargeIcon(objectName, ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalLargeIcon(objectName, DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(objectName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            object.setLargeIconInfo(iconInfo);
        }
    }

    /**
     * Configures the given object.
     * @see #configure(Object, String)
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return configure(bean, beanName);
    }

    private String loadMessage(String objectName, String messageType) {
        Assert.notNull(objectName, "The bean's object name must be provided");
        String labelCode = objectName + "." + messageType;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Resolving label with code '" + labelCode + "'");
        }
        
        try {
            return getMessageSource().getMessage(labelCode, null, getLocale());
        }
        catch (NoSuchMessageException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Labeled property message code '" + labelCode
                        + "' does not exist in message bundle; continuing...");
            }
            return null;
        }
    }

    /**
     * Returns the system default locale.
     *
     * @return The system default locale, never null.
     */
    protected Locale getLocale() {
        return Locale.getDefault();
    }

    private Icon loadOptionalIcon(String objectName, String iconType) {
        String key = objectName + "." + iconType;
        return getIconSource().getIcon(key);
    }

    private Icon loadOptionalLargeIcon(String objectName, String iconType) {
        String key = objectName + ".large." + iconType;
        return getIconSource().getIcon(key);
    }

    private Image loadImage(String objectName, String imageType) {
        String key = objectName + "." + imageType;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Resolving optional image with code '" + key + "'");
            }
            return getImageSource().getImage(key);
        }
        catch (NoSuchImageResourceException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Labelable object's image '" + key + "' does not exist in image bundle; continuing...");
            }
            return null;
        }
    }

    /**
     * A default implemenation, performing no operation.
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
