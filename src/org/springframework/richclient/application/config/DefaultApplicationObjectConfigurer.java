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
import org.springframework.richclient.application.Application;
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
 * @author Keith Donald
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

    public DefaultApplicationObjectConfigurer(MessageSource messageSource) {
        this(messageSource, null, null);
    }

    public DefaultApplicationObjectConfigurer(MessageSource messageSource, ImageSource imageSource) {
        this(messageSource, imageSource, null);
    }

    public DefaultApplicationObjectConfigurer(MessageSource messageSource, ImageSource imageSource,
            IconSource iconSource) {
        Assert.notNull(messageSource, "The message source is required");
        this.messageSource = messageSource;
        this.imageSource = imageSource;
        this.iconSource = iconSource;
    }

    public void setLoadOptionalIcons(boolean loadOptionalIcons) {
        this.loadOptionalIcons = loadOptionalIcons;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected IconSource getIconSource() {
        return iconSource;
    }

    protected ImageSource getImageSource() {
        return imageSource;
    }

    public Object configure(Object bean, String beanName) {
        configureTitle(bean, beanName);
        configureLabel(bean, beanName);
        configureDescription(bean, beanName);
        configureImageIcons(bean, beanName);
        configureSecurityController(bean, beanName);
        return bean;
    }

    private void configureTitle(Object bean, String beanName) {
        if (bean instanceof TitleConfigurable) {
            TitleConfigurable titleable = (TitleConfigurable)bean;
            titleable.setTitle(loadMessage(beanName, "title"));
        }
    }

    private void configureLabel(Object bean, String beanName) {
        if (bean instanceof LabelConfigurable) {
            LabelConfigurable labelable = (LabelConfigurable)bean;
            String labelStr = loadMessage(beanName, "label");
            labelable.setLabelInfo(new LabelInfoFactory(labelStr).createLabelInfo());
        }
        else {
            if (bean instanceof CommandLabelConfigurable) {
                CommandLabelConfigurable labelable = (CommandLabelConfigurable)bean;
                String labelStr = loadMessage(beanName, "label");
                labelable.setLabelInfo(new LabelInfoFactory(labelStr).createButtonLabelInfo());
            }
        }
    }

    private void configureDescription(Object bean, String beanName) {
        if (bean instanceof DescriptionConfigurable) {
            DescriptionConfigurable config = (DescriptionConfigurable)bean;
            String caption = loadMessage(beanName, CAPTION_KEY);
            if (StringUtils.hasText(caption)) {
                config.setCaption(caption);
            }
            String description = loadMessage(beanName, DESCRIPTION_KEY);
            if (StringUtils.hasText(description)) {
                config.setDescription(description);
            }
        }
    }

    private void configureImageIcons(Object bean, String beanName) {
        if (getImageSource() != null) {
            if (bean instanceof ImageConfigurable) {
                ImageConfigurable imageable = (ImageConfigurable)bean;
                imageable.setImage(loadImage(beanName, IMAGE_KEY));
            }
        }
        if (getIconSource() != null) {
            if (bean instanceof IconConfigurable) {
                IconConfigurable iconable = (IconConfigurable)bean;
                iconable.setIcon(loadOptionalIcon(beanName, ICON_KEY));
            }
            else if (bean instanceof CommandIconConfigurable) {
                setIconInfo((CommandIconConfigurable)bean, beanName);
                setLargeIconInfo((CommandIconConfigurable)bean, beanName);
            }
        }
    }

    /**
     * Associate an object with a security controller if it implements the
     * {@link SecurityControllable} interface.
     * @param bean to configure
     * @param beanName Name (id) of bean
     * @throws BeansException if a referenced security controller is not found or is of
     *             the wrong type
     */
    private void configureSecurityController(Object bean, String beanName) throws BeansException {
        if( bean instanceof SecurityControllable ) {
            SecurityControllable controllable = (SecurityControllable) bean;
            String controllerId = controllable.getSecurityControllerId();

            if( controllerId != null ) {
                // Find the referenced controller.
                SecurityControllerManager manager = Application.services().getSecurityControllerManager();
                SecurityController controller = manager.getSecurityController( controllerId );

                if( logger.isDebugEnabled() ) {
                    logger.debug( "Lookup SecurityController with id [" + controllerId + "]" );
                }

                // And add the bean to the controlled object set
                if( controller != null ) {
                    if( logger.isDebugEnabled() ) {
                        logger.debug( "configuring SecurityControllable [" + beanName + "]; security controller id='"
                                + controllerId + "'" );
                    }
                    controller.addControlledObject( controllable );
                } else {
                    if( logger.isDebugEnabled() ) {
                        logger.debug( "configuring SecurityControllable [" + beanName + "]; no security controller for id='"
                                + controllerId + "'" );
                    }
                }
            } else {
                if( logger.isDebugEnabled() ) {
                    logger.debug( "configuring SecurityControllable [" + beanName
                            + "]; no security controller Id specified" );
                }
            }
        }
    }

    public void setIconInfo(CommandIconConfigurable bean, String beanName) {
        Icon icon = loadOptionalIcon(beanName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalIcon(beanName, SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalIcon(beanName, ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalIcon(beanName, DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(beanName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            ((CommandIconConfigurable)bean).setIconInfo(iconInfo);
        }
    }

    public void setLargeIconInfo(CommandIconConfigurable bean, String beanName) {
        Icon icon = loadOptionalLargeIcon(beanName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalLargeIcon(beanName, SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalLargeIcon(beanName, ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalLargeIcon(beanName, DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(beanName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            ((CommandIconConfigurable)bean).setLargeIconInfo(iconInfo);
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return configure(bean, beanName);
    }

    private String loadMessage(String beanName, String messageType) {
        Assert.notNull(beanName, "The bean's object name must be provided");
        String labelCode = beanName + "." + messageType;
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

    protected Locale getLocale() {
        return Locale.getDefault();
    }

    private Icon loadOptionalIcon(String beanName, String iconType) {
        String key = beanName + "." + iconType;
        return getIconSource().getIcon(key);
    }

    private Icon loadOptionalLargeIcon(String beanName, String iconType) {
        String key = beanName + ".large." + iconType;
        return getIconSource().getIcon(key);
    }

    private Image loadImage(String beanName, String imageType) {
        String key = beanName + "." + imageType;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Resolving optional image with code '" + key + "'");
            }
            return getImageSource().getImage(key);
        }
        catch (NoSuchImageResourceException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Labelable bean's image '" + key + "' does not exist in image bundle; continuing...");
            }
            return null;
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}