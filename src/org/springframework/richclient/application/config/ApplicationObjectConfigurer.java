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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.command.config.CommandButtonIconInfo;
import org.springframework.richclient.command.config.CommandIconable;
import org.springframework.richclient.command.config.CommandLabelable;
import org.springframework.richclient.core.Describable;
import org.springframework.richclient.core.Labelable;
import org.springframework.richclient.core.Titleable;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.image.config.Iconable;
import org.springframework.richclient.image.config.Imageable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public class ApplicationObjectConfigurer extends ApplicationObjectSupport
        implements ObjectConfigurer, BeanPostProcessor, InitializingBean {
    private static final String PRESSED_ICON_KEY = "pressedIcon";

    private static final String DISABLED_ICON_KEY = "disabledIcon";

    private static final String ROLLOVER_ICON_KEY = "rolloverIcon";

    private static final String SELECTED_ICON_KEY = "selectedIcon";

    private static final String ICON_KEY = "icon";

    private static final String IMAGE_KEY = "image";

    private static final String DESCRIPTION_KEY = "description";

    private static final String CAPTION_KEY = "caption";

    private MessageSource messageSource;

    private ImageSource imageSource;

    private IconSource iconSource;

    private boolean loadOptionalIcons = true;

    public void setMessageSource(MessageSource messages) {
        Assert.notNull(messages);
        this.messageSource = messages;
    }

    public void setImageSource(ImageSource images) {
        Assert.notNull(images);
        this.imageSource = images;
    }

    public void setImageIconSource(IconSource icons) {
        Assert.notNull(icons);
        this.iconSource = icons;
    }

    public void setLoadOptionalIcons(boolean loadOptionalIcons) {
        this.loadOptionalIcons = loadOptionalIcons;
    }

    public void afterPropertiesSet() {
        if (messageSource == null) {
            messageSource = getApplicationContext();
        }
        if (iconSource == null) {
            iconSource = findIconSource();
        }
        if (imageSource == null) {
            imageSource = findImageSource();
        }
    }

    private IconSource findIconSource() {
        try {
            return (IconSource)getApplicationContext().getBean(
                    ApplicationServices.ICON_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private ImageSource findImageSource() {
        try {
            return (ImageSource)getApplicationContext().getBean(
                    ApplicationServices.IMAGE_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    public Object configure(Object bean, String beanName) {
        configureTitle(bean, beanName);
        configureLabel(bean, beanName);
        configureDescription(bean, beanName);
        configureImageIcons(bean, beanName);
        return bean;
    }

    private void configureTitle(Object bean, String beanName) {
        if (bean instanceof Titleable) {
            Titleable titleable = (Titleable)bean;
            titleable.setTitle(loadMessage(beanName, "title"));
        }
    }

    private void configureLabel(Object bean, String beanName) {
        if (bean instanceof Labelable) {
            Labelable labelable = (Labelable)bean;
            String labelStr = loadMessage(beanName, "label");
            labelable.setLabelInfo(new LabelInfoFactory(labelStr)
                    .createLabelInfo());
        }
        else {
            if (bean instanceof CommandLabelable) {
                CommandLabelable labelable = (CommandLabelable)bean;
                String labelStr = loadMessage(beanName, "label");
                labelable.setLabelInfo(new LabelInfoFactory(labelStr)
                        .createButtonLabelInfo());
            }
        }
    }

    private void configureDescription(Object bean, String beanName) {
        if (bean instanceof Describable) {
            Describable config = (Describable)bean;
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
        if (imageSource != null) {
            if (bean instanceof Imageable) {
                Imageable imageable = (Imageable)bean;
                imageable.setImage(loadImage(beanName, IMAGE_KEY));
            }
        }
        if (iconSource != null) {
            if (bean instanceof Iconable) {
                Iconable iconable = (Iconable)bean;
                iconable.setIcon(loadOptionalIcon(beanName, ICON_KEY));
            }
            else if (bean instanceof CommandIconable) {
                setIconInfo((CommandIconable)bean, beanName);
                setLargeIconInfo((CommandIconable)bean, beanName);
            }
        }
    }

    public void setIconInfo(CommandIconable bean, String beanName) {
        Icon icon = loadOptionalIcon(beanName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalIcon(beanName,
                        SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalIcon(beanName,
                        ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalIcon(beanName,
                        DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(beanName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon,
                        rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            ((CommandIconable)bean).setIconInfo(iconInfo);
        }
    }

    public void setLargeIconInfo(CommandIconable bean, String beanName) {
        Icon icon = loadOptionalLargeIcon(beanName, ICON_KEY);
        if (icon != null) {
            CommandButtonIconInfo iconInfo;
            if (loadOptionalIcons) {
                Icon selectedIcon = loadOptionalLargeIcon(beanName,
                        SELECTED_ICON_KEY);
                Icon rolloverIcon = loadOptionalLargeIcon(beanName,
                        ROLLOVER_ICON_KEY);
                Icon disabledIcon = loadOptionalLargeIcon(beanName,
                        DISABLED_ICON_KEY);
                Icon pressedIcon = loadOptionalIcon(beanName, PRESSED_ICON_KEY);
                iconInfo = new CommandButtonIconInfo(icon, selectedIcon,
                        rolloverIcon, disabledIcon, pressedIcon);
            }
            else {
                iconInfo = new CommandButtonIconInfo(icon);
            }
            ((CommandIconable)bean).setLargeIconInfo(iconInfo);
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return configure(bean, beanName);
    }

    private String loadMessage(String beanName, String messageType) {
        Assert.notNull(beanName, "The bean's object name must be provided");
        String labelCode = beanName + "." + messageType;
        if (logger.isDebugEnabled()) {
            logger.debug("Resolving label with code '" + labelCode + "'");
        }
        try {
            return messageSource.getMessage(labelCode, null, getLocale());
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
        return iconSource.getIcon(key);
    }

    private Icon loadOptionalLargeIcon(String beanName, String iconType) {
        String key = beanName + ".large." + iconType;
        return iconSource.getIcon(key);
    }

    private Image loadImage(String beanName, String imageType) {
        String key = beanName + "." + imageType;
        try {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("Resolving optional image with code '" + key
                                + "'");
            }
            return imageSource.getImage(key);
        }
        catch (NoSuchImageResourceException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Labelable bean's image '" + key
                        + "' does not exist in image bundle; continuing...");
            }
            return null;
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

}