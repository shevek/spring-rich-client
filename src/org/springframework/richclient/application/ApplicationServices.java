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
package org.springframework.richclient.application;

import java.awt.Image;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.application.config.ObjectConfigurer;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.ApplicationCommandConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.factory.DefaultComponentFactory;
import org.springframework.richclient.image.AwtImageResource;
import org.springframework.richclient.image.DefaultIconSource;
import org.springframework.richclient.image.DefaultImageSource;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.rules.DefaultRulesSource;
import org.springframework.rules.Rules;
import org.springframework.rules.RulesSource;
import org.springframework.rules.predicates.beans.BeanPropertyExpression;

/**
 * A singleton service locator of a rich client application.
 *
 * The application provides a point of reference and context for an entire
 * application. It provides data about the application: name, version, and build
 * ID. It also acts as service locator / facade for a number of commonly needed
 * rich client interfaces.
 *
 * @author Keith Donald
 */
public class ApplicationServices extends ApplicationObjectSupport implements
        MessageSource, ImageSource, IconSource, RulesSource, ObjectConfigurer,
        CommandConfigurer {

    public static final String IMAGE_SOURCE_BEAN_KEY = "imageSource";

    public static final String ICON_SOURCE_BEAN_KEY = "iconSource";

    public static final String COMPONENT_FACTORY_BEAN_KEY = "componentFactory";

    public static final String RULES_SOURCE_BEAN_KEY = "rulesSource";

    public static final String PROPERTY_EDITOR_REGISTRY_BEAN_KEY = "propertyEditorRegistry";

    public static final String OBJECT_CONFIGURER_BEAN_KEY = "applicationObjectConfigurer";

    public static final String COMMAND_CONFIGURER_BEAN_KEY = "commandConfigurer";

    public static final String LOOK_AND_FEEL_CONFIGURER_BEAN_KEY = "lookAndFeelConfigurer";

    private final Log logger = LogFactory.getLog(getClass());

    private ObjectConfigurer objectConfigurer;

    private CommandConfigurer commandConfigurer;

    private ComponentFactory componentFactory;

    private RulesSource rulesSource;

    private ViewRegistry viewRegistry;

    private ImageSource imageSource;

    private IconSource iconSource;

    private Map attributes;

    private PropertyEditorRegistry propertyEditorRegistry;

    private boolean lazyInit = true;

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public ComponentFactory getComponentFactory() {
        if (componentFactory == null) {
            initComponentFactory();
        }
        return componentFactory;
    }

    public void setComponentFactory(ComponentFactory factory) {
        this.componentFactory = factory;
    }

    public ViewRegistry getViewRegistry() {
        if (viewRegistry == null) {
            initViewRegistry();
        }
        return viewRegistry;
    }

    public void setViewRegistry(ViewRegistry registry) {
        this.viewRegistry = registry;
    }

    public ImageSource getImageSource() {
        if (imageSource == null) {
            initImageSource();
        }
        return imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    public IconSource getIconSource() {
        if (iconSource == null) {
            initIconSource();
        }
        return iconSource;
    }

    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }


    public PropertyEditorRegistry getPropertyEditorRegistry() {
        if (propertyEditorRegistry == null) {
            initPropertyEditorRegistry();
        }
        return propertyEditorRegistry;
    }


    public void setPropertyEditorRegistry(PropertyEditorRegistry preReg) {
        this.propertyEditorRegistry = preReg;
    }


    public ObjectConfigurer getObjectConfigurer() {
        if (objectConfigurer == null) {
            initObjectConfigurer();
        }
        return objectConfigurer;
    }

    public void setObjectConfigurer(ObjectConfigurer objectConfigurer) {
        this.objectConfigurer = objectConfigurer;
    }

    public CommandConfigurer getCommandConfigurer() {
        if (commandConfigurer == null) {
            initCommandConfigurer();
        }
        return commandConfigurer;
    }

    public void setCommandConfigurer(CommandConfigurer commandConfigurer) {
        this.commandConfigurer = commandConfigurer;
    }

    public RulesSource getRulesSource() {
        if (rulesSource == null) {
            initRulesSource();
        }
        return rulesSource;
    }

    public void setRulesSource(RulesSource rulesSource) {
        this.rulesSource = rulesSource;
    }

    protected Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;
    }

    public void setAttribute(String attributeName, Object attribute) {
        getAttributes().put(attributeName, attribute);
    }

    public Object getAttribute(String attributeName) {
        return getAttributes().get(attributeName);
    }

    protected void initApplicationContext() throws BeansException {
        if (!lazyInit) {
            initStandardServices();
        }
        initLookAndFeelConfigurer();
    }

    public void initStandardServices() {
        getImageSource();
        getIconSource();
        getComponentFactory();
        getViewRegistry();
        getRulesSource();
        getObjectConfigurer();
        getCommandConfigurer();
    }

    private void initImageSource() {
        try {
            this.imageSource = (ImageSource)getApplicationContext().getBean(
                IMAGE_SOURCE_BEAN_KEY, ImageSource.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No image source bean found in context under name '"
                    + IMAGE_SOURCE_BEAN_KEY + "'; configuring defaults.");
            this.imageSource = new DefaultImageSource(new HashMap());
        }
    }

    private void initIconSource() {
        try {
            this.iconSource = (IconSource)getApplicationContext().getBean(
                ICON_SOURCE_BEAN_KEY, IconSource.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No icon source bean found under name "
                    + ICON_SOURCE_BEAN_KEY
                    + "; creating using existing image source.");
            this.iconSource = new DefaultIconSource(getImageSource());
        }
    }

    private void initComponentFactory() {
        try {
            this.componentFactory = (ComponentFactory)getApplicationContext()
                    .getBean(COMPONENT_FACTORY_BEAN_KEY, ComponentFactory.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No component factory bean found under name "
                    + COMPONENT_FACTORY_BEAN_KEY
                    + "; creating using existing image source.");
            DefaultComponentFactory f = new DefaultComponentFactory();
            f.setApplicationContext(getApplicationContext());
            this.componentFactory = f;
        }
    }

    private void initViewRegistry() {
        ApplicationContextViewRegistry r = new ApplicationContextViewRegistry();
        r.setApplicationContext(getApplicationContext());
        this.viewRegistry = r;
    }

    private void initRulesSource() {
        try {
            this.rulesSource = (RulesSource)getApplicationContext().getBean(
                RULES_SOURCE_BEAN_KEY, RulesSource.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No rule source found in context under name '"
                    + RULES_SOURCE_BEAN_KEY + "'; configuring defaults.");
            this.rulesSource = new DefaultRulesSource();
        }
    }


    private void initPropertyEditorRegistry() {
        try {
            this.propertyEditorRegistry = (PropertyEditorRegistry)getApplicationContext().getBean(
                PROPERTY_EDITOR_REGISTRY_BEAN_KEY, PropertyEditorRegistry.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No rule source found in context under name '"
                    + PROPERTY_EDITOR_REGISTRY_BEAN_KEY + "'; configuring defaults.");
            this.propertyEditorRegistry = new DefaultPropertyEditorRegistry();
        }
    }

    private void initObjectConfigurer() {
        try {
            this.objectConfigurer = (ObjectConfigurer)getApplicationContext()
                    .getBean(OBJECT_CONFIGURER_BEAN_KEY, ObjectConfigurer.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No object configurer found in context under name '"
                    + OBJECT_CONFIGURER_BEAN_KEY + "'; configuring defaults.");
            ApplicationObjectConfigurer objectConfigurer = new ApplicationObjectConfigurer();
            objectConfigurer.setApplicationContext(getApplicationContext());
            objectConfigurer.afterPropertiesSet();
            this.objectConfigurer = objectConfigurer;
        }
    }

    private void initCommandConfigurer() {
        try {
            this.commandConfigurer = (CommandConfigurer)getApplicationContext()
                    .getBean(COMMAND_CONFIGURER_BEAN_KEY,
                        CommandConfigurer.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No command configurer found in context under name '"
                    + COMMAND_CONFIGURER_BEAN_KEY + "'; configuring defaults.");
            this.commandConfigurer = new ApplicationCommandConfigurer();
        }
    }

    public void initLookAndFeelConfigurer() {
        try {
            getApplicationContext().getBean(LOOK_AND_FEEL_CONFIGURER_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger
                    .info("No look and feel configurer found in context under name '"
                            + LOOK_AND_FEEL_CONFIGURER_BEAN_KEY
                            + "'; configuring defaults.");
        }
    }

    public AbstractCommand configure(AbstractCommand command) {
        return getCommandConfigurer().configure(command);
    }

    public AbstractCommand configure(AbstractCommand command,
            String faceConfigurationKey) {
        return getCommandConfigurer().configure(command, faceConfigurationKey);
    }

    public Object configure(Object applicationObject, String objectName) {
        return getObjectConfigurer().configure(applicationObject, objectName);
    }

    public MessageSourceAccessor getMessages() {
        return getMessageSourceAccessor();
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {
        return getApplicationContext().getMessage(resolvable, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale)
            throws NoSuchMessageException {
        return getApplicationContext().getMessage(code, args, locale);
    }

    public String getMessage(String code, Object[] args, String defaultMessage,
            Locale locale) {
        return getApplicationContext().getMessage(code, args, defaultMessage,
            locale);
    }

    public Image getImage(String key) {
        return getImageSource().getImage(key);
    }

    public AwtImageResource getImageResource(String key) {
        return getImageSource().getImageResource(key);
    }

    public Icon getIcon(String key) {
        return getIconSource().getIcon(key);
    }

    public BeanPropertyExpression getRules(Class beanClass, String propertyName) {
        return getRulesSource().getRules(beanClass, propertyName);
    }

    public Rules getRules(Class bean) {
        return getRulesSource().getRules(bean);
    }
}