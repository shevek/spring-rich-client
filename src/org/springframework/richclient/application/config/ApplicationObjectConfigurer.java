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
import org.springframework.richclient.command.config.CommandButtonLabelConfigurable;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.LabelConfigurable;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.factory.LabelInfo;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.image.config.IconConfigurable;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.richclient.image.config.ImageIconButtonConfigurable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public class ApplicationObjectConfigurer extends ApplicationObjectSupport
        implements ObjectConfigurer, BeanPostProcessor, InitializingBean {
    private MessageSource messages;

    private ImageSource images;

    private IconSource icons;

    public void setMessageSource(MessageSource messages) {
        Assert.notNull(messages);
        this.messages = messages;
    }

    public void setImageSource(ImageSource images) {
        Assert.notNull(images);
        this.images = images;
    }

    public void setImageIconSource(IconSource icons) {
        Assert.notNull(icons);
        this.icons = icons;
    }

    public void afterPropertiesSet() {
        if (messages == null) {
            messages = getApplicationContext();
        }
        if (icons == null) {
            icons = getIconSource();
        }
        if (images == null) {
            images = getImageSource();
        }
    }

    private IconSource getIconSource() {
        try {
            return (IconSource)getApplicationContext().getBean(
                    ApplicationServices.ICON_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private ImageSource getImageSource() {
        try {
            return (ImageSource)getApplicationContext().getBean(
                ApplicationServices.IMAGE_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    public Object configure(Object bean, String beanName) {
        if (bean instanceof TitleConfigurable) {
            TitleConfigurable titleable = (TitleConfigurable)bean;
            titleable.setTitle(loadMessage(beanName, "title"));
        }
        if (bean instanceof LabelConfigurable) {
            LabelConfigurable labelable = (LabelConfigurable)bean;
            String labelStr = loadMessage(beanName, "label");
            if (StringUtils.hasText(labelStr)) {
                LabelInfo info = new LabelInfoFactory(labelStr)
                        .createLabelInfo();
                labelable.setLabel(info);
            }
        }
        if (bean instanceof CommandButtonLabelConfigurable) {
            CommandButtonLabelConfigurable labelable = (CommandButtonLabelConfigurable)bean;
            String labelStr = loadMessage(beanName, "label");
            if (StringUtils.hasText(labelStr)) {
                CommandButtonLabelInfo info = new LabelInfoFactory(labelStr)
                        .createButtonLabelInfo();
                labelable.setCommandButtonLabelInfo(info);
            }
        }
        if (bean instanceof DescriptionConfigurable) {
            DescriptionConfigurable config = (DescriptionConfigurable)bean;
            String caption = loadMessage(beanName, "caption");
            if (StringUtils.hasText(caption)) {
                config.setCaption(caption);
            }
            String description = loadMessage(beanName, "description");
            if (StringUtils.hasText(description)) {
                config.setDescription(description);
            }
        }
        if (images != null) {
            if (bean instanceof ImageConfigurable) {
                ImageConfigurable imageable = (ImageConfigurable)bean;
                imageable.setImage(loadImage(beanName, "image"));
            }
        }
        if (icons != null) {
            if (bean instanceof org.springframework.richclient.command.config.CommandButtonIconInfoConfigurable) {
                CommandButtonIconInfo iconInfo;
                Icon icon = loadOptionalIcon(beanName, "icon");
                Icon selectedIcon = loadOptionalIcon(beanName, "selected");
                Icon rolloverIcon = loadOptionalIcon(beanName, "rollover");
                Icon disabledIcon = loadOptionalIcon(beanName, "disabled");
                Icon pressedIcon = loadOptionalIcon(beanName, "pressed");
                if (icon != null) {
                    iconInfo = new CommandButtonIconInfo(icon, selectedIcon,
                            rolloverIcon, disabledIcon, pressedIcon);
                    ((org.springframework.richclient.command.config.CommandButtonIconInfoConfigurable)bean)
                            .setCommandButtonIconInfo(iconInfo);
                }
            }

            if (bean instanceof IconConfigurable) {
                IconConfigurable iconable = (IconConfigurable)bean;
                iconable.setIcon(loadOptionalIcon(beanName, "icon"));
                if (bean instanceof ImageIconButtonConfigurable) {
                    ImageIconButtonConfigurable buttonIconable = (ImageIconButtonConfigurable)iconable;
                    buttonIconable.setRolloverIcon(loadOptionalIcon(beanName,
                            "rollover"));
                    buttonIconable.setPressedIcon(loadOptionalIcon(beanName,
                            "pressed"));
                    buttonIconable.setDisabledIcon(loadOptionalIcon(beanName,
                            "disabled"));
                }
            }
        }
        return bean;
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
            return messages.getMessage(labelCode, null, Locale.getDefault());
        }
        catch (NoSuchMessageException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Labeled property message code '" + labelCode
                        + "' does not exist in message bundle; continuing...");
            }
            return null;
        }
    }

    private Icon loadOptionalIcon(String beanName, String iconType) {
        String key = beanName + "." + iconType;
        return icons.getIcon(key);
    }

    private Image loadImage(String beanName, String imageType) {
        String key = beanName + "." + imageType;
        try {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("Resolving optional image with code '" + key
                                + "'");
            }
            return images.getImage(key);
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