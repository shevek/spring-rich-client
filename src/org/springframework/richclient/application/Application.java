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
import java.util.Observable;
import java.util.Observer;

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
import org.springframework.richclient.application.config.ApplicationLifecycle;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.application.config.JGoodiesLooksConfigurer;
import org.springframework.richclient.application.config.ObjectConfigurer;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.ApplicationCommandConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.factory.DefaultComponentFactory;
import org.springframework.richclient.image.AwtImageResource;
import org.springframework.richclient.image.AwtImageSource;
import org.springframework.richclient.image.DefaultAwtImageSource;
import org.springframework.richclient.image.DefaultIconSource;
import org.springframework.richclient.image.IconSource;
import org.springframework.rules.DefaultRulesSource;
import org.springframework.rules.Rules;
import org.springframework.rules.RulesSource;
import org.springframework.rules.predicates.beans.BeanPropertyExpression;
import org.springframework.util.Assert;

/**
 * A singleton workbench or shell of a rich client application.
 * 
 * The application provides a point of reference and context for an entire
 * application. It provides an interface to open application windows, and
 * provides data about the application: name, version, and build ID. It also
 * acts as service locator / facade for a number of commonly needed rich client
 * interfaces.
 * 
 * @author Keith Donald
 */
public class Application extends ApplicationObjectSupport implements
        MessageSource, AwtImageSource, IconSource, RulesSource,
        ObjectConfigurer, CommandConfigurer {

    private final Log logger = LogFactory.getLog(getClass());

    public static final String IMAGE_SOURCE_BEAN_KEY = "imageSource";

    public static final String ICON_SOURCE_BEAN_KEY = "iconSource";

    public static final String RULES_SOURCE_BEAN_KEY = "rulesSource";

    public static final String OBJECT_CONFIGURER_BEAN_KEY = "applicationObjectConfigurer";

    public static final String COMMAND_CONFIGURER_BEAN_KEY = "commandConfigurer";

    public static final String LOOK_AND_FEEL_CONFIGURER_BEAN_KEY = "lookAndFeelConfigurer";

    private static Application sharedInstance;

    private ObjectConfigurer objectConfigurer;

    private CommandConfigurer commandConfigurer;

    private ComponentFactory componentFactory;

    private RulesSource rulesSource;

    private ViewRegistry viewRegistry;

    private AwtImageSource imageSource;

    private IconSource iconSource;

    private ApplicationWindow activeWindow;

    private WindowManager windowManager = new WindowManager();

    private ApplicationLifecycle lifecycle;

    private ApplicationInfo applicationInfo;

    private Map attributes;
    
    public Application(ApplicationLifecycle lifecycle) {
        setLifecycle(lifecycle);
        Assert.isTrue(sharedInstance == null,
                "Only one instance of a RCP Application allowed per VM.");
        load(this);
    }

    public static void load(Application instance) {
        sharedInstance = instance;
    }

    /**
     * Return the single application instance.
     * 
     * @return The application
     */
    public static Application locator() {
        Assert
                .notNull(sharedInstance,
                        "The global application instance has not yet been initialized.");
        return sharedInstance;
    }

    public String getName() {
        if (applicationInfo != null) {
            return applicationInfo.getDisplayName();
        }
        else {
            return "Spring Rich Client Application";
        }
    }

    public Image getImage() {
        if (applicationInfo != null) {
            return applicationInfo.getImage();
        }
        else {
            return null;
        }
    }

    void setLifecycle(ApplicationLifecycle lifecycle) {
        Assert.notNull(lifecycle);
        this.lifecycle = lifecycle;
    }

    ApplicationLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public ComponentFactory getComponentFactory() {
        return componentFactory;
    }

    public void setComponentFactory(ComponentFactory factory) {
        Assert.notNull(factory);
        this.componentFactory = factory;
    }

    public ViewRegistry getViewRegistry() {
        return viewRegistry;
    }

    public void setViewRegistry(ViewRegistry registry) {
        Assert.notNull(registry);
        this.viewRegistry = registry;
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
        getLifecycle().onPreInitialize(this);
        initStandardServices();
        getLifecycle().onPreStartup();
        openFirstTimeApplicationWindow();
        getLifecycle().onStarted();
    }

    protected void initStandardServices() {
        initImageSource();
        initIconSource();
        initComponentFactory();
        initViewRegistry();
        initRulesSource();
        initObjectConfigurer();
        initCommandConfigurer();
        initLookAndFeelConfigurer();
    }

    private void initImageSource() {
        try {
            this.imageSource = (AwtImageSource)getApplicationContext().getBean(
                    IMAGE_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No image source bean found in context under name '"
                    + IMAGE_SOURCE_BEAN_KEY + "'; configuring defaults.");
            this.imageSource = new DefaultAwtImageSource(new HashMap());
        }
    }

    private void initIconSource() {
        try {
            this.iconSource = (IconSource)getApplicationContext().getBean(
                    ICON_SOURCE_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger.info("No icon source bean found under name "
                    + ICON_SOURCE_BEAN_KEY
                    + "; creating using existing image source.");
            this.iconSource = new DefaultIconSource(imageSource);
        }
    }

    private void initComponentFactory() {
        if (componentFactory == null) {
            DefaultComponentFactory f = new DefaultComponentFactory();
            f.setApplicationContext(getApplicationContext());
            this.componentFactory = f;
        }
    }

    private void initViewRegistry() {
        if (viewRegistry == null) {
            ApplicationContextViewRegistry r = new ApplicationContextViewRegistry();
            r.setApplicationContext(getApplicationContext());
            this.viewRegistry = r;
        }
    }

    private void initRulesSource() {
        if (rulesSource == null) {
            try {
                rulesSource = (RulesSource)getApplicationContext().getBean(
                        RULES_SOURCE_BEAN_KEY);
            }
            catch (NoSuchBeanDefinitionException e) {
                logger.info("No rule source found in context under name '"
                        + RULES_SOURCE_BEAN_KEY + "'; configuring defaults.");
                this.rulesSource = new DefaultRulesSource();
            }
        }
    }

    private void initObjectConfigurer() {
        if (objectConfigurer == null) {
            try {
                objectConfigurer = (ObjectConfigurer)getApplicationContext()
                        .getBean(OBJECT_CONFIGURER_BEAN_KEY);
            }
            catch (NoSuchBeanDefinitionException e) {
                logger
                        .info("No object configurer found in context under name '"
                                + OBJECT_CONFIGURER_BEAN_KEY
                                + "'; configuring defaults.");
                ApplicationObjectConfigurer objectConfigurer = new ApplicationObjectConfigurer();
                objectConfigurer.setApplicationContext(getApplicationContext());
                objectConfigurer.afterPropertiesSet();
                this.objectConfigurer = objectConfigurer;
            }
        }
    }

    private void initCommandConfigurer() {
        if (commandConfigurer == null) {
            try {
                commandConfigurer = (CommandConfigurer)getApplicationContext()
                        .getBean(COMMAND_CONFIGURER_BEAN_KEY);
            }
            catch (NoSuchBeanDefinitionException e) {
                logger
                        .info("No command configurer found in context under name '"
                                + COMMAND_CONFIGURER_BEAN_KEY
                                + "'; configuring defaults.");
                this.commandConfigurer = new ApplicationCommandConfigurer();
            }
        }
    }

    private void initLookAndFeelConfigurer() {
        try {
            getApplicationContext().getBean(LOOK_AND_FEEL_CONFIGURER_BEAN_KEY);
        }
        catch (NoSuchBeanDefinitionException e) {
            logger
                    .info("No look and feel configurer found in context under name '"
                            + LOOK_AND_FEEL_CONFIGURER_BEAN_KEY
                            + "'; configuring defaults.");
            new JGoodiesLooksConfigurer().setPlasticLookAndFeel(null);
        }
    }

    protected void openFirstTimeApplicationWindow() {
        ApplicationWindow mainWindow = createNewWindow();
        mainWindow.openPage(getLifecycle().getStartingPageId());
        this.activeWindow = mainWindow;
    }

    private ApplicationWindow createNewWindow() {
        ApplicationWindow newWindow = new ApplicationWindow(windowManager
                .size());
        windowManager.add(newWindow);
        windowManager.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (windowManager.getWindows().length == 0) {
                    close();
                }
            }
        });
        return newWindow;
    }

    public ApplicationWindow getActiveWindow() {
        return activeWindow;
    }

    public void close() {
        System.exit(0);
    }

    public void openWindow(String pageId) {
        ApplicationWindow newWindow = createNewWindow();
        newWindow.openPage(pageId);
        // @TODO track active window...
        this.activeWindow = newWindow;
    }

    // facades to various rich client service interfaces

    public AbstractCommand configure(AbstractCommand command) {
        return commandConfigurer.configure(command);
    }

    public AbstractCommand configure(AbstractCommand command,
            String faceConfigurationKey) {
        return commandConfigurer.configure(command, faceConfigurationKey);
    }

    public Object configure(Object applicationObject, String objectName) {
        return objectConfigurer.configure(applicationObject, objectName);
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
        return imageSource.getImage(key);
    }

    public AwtImageResource getImageResource(String key) {
        return imageSource.getImageResource(key);
    }

    public Icon getIcon(String key) {
        return iconSource.getIcon(key);
    }

    public BeanPropertyExpression getRules(Class beanClass, String propertyName) {
        return rulesSource.getRules(beanClass, propertyName);
    }

    public Rules getRules(Class bean) {
        return rulesSource.getRules(bean);
    }

}