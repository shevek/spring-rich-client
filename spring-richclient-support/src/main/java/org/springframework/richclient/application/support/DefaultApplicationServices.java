/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.application.support;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.form.FieldFaceSource;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.MessageSourceFieldFaceSource;
import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.support.DefaultValueChangeDetector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.ResourceMapFactoryBean;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.core.enums.StaticLabeledEnumResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindowFactory;
import org.springframework.richclient.application.DefaultConversionServiceFactoryBean;
import org.springframework.richclient.application.PageComponentPaneFactory;
import org.springframework.richclient.application.PageDescriptorRegistry;
import org.springframework.richclient.application.ServiceNotFoundException;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationObjectConfigurer;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.command.config.DefaultCommandConfigurer;
import org.springframework.richclient.command.support.DefaultCommandServices;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.factory.DefaultButtonFactory;
import org.springframework.richclient.factory.DefaultComponentFactory;
import org.springframework.richclient.factory.DefaultMenuFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.BindingFactoryProvider;
import org.springframework.richclient.form.binding.swing.SwingBinderSelectionStrategy;
import org.springframework.richclient.form.binding.swing.SwingBindingFactoryProvider;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.image.DefaultIconSource;
import org.springframework.richclient.image.DefaultImageSource;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.security.ApplicationSecurityManager;
import org.springframework.richclient.security.SecurityControllerManager;
import org.springframework.richclient.security.support.DefaultApplicationSecurityManager;
import org.springframework.richclient.security.support.DefaultSecurityControllerManager;
import org.springframework.richclient.util.Assert;
import org.springframework.rules.RulesSource;
import org.springframework.rules.reporting.DefaultMessageTranslatorFactory;
import org.springframework.rules.reporting.MessageTranslatorFactory;
import org.springframework.rules.support.DefaultRulesSource;
import org.springframework.util.ClassUtils;

/**
 * A default implementation of the ApplicationServices (service locator) interface. This implementation allows for the
 * direct registration of service implementations by using various setter methods (like
 * {@link #setImageSource(ImageSource)}). Service registry entries can also be added in bulk using the
 * {@link #setRegistryEntries(Map)} method.
 * <p>
 * Except in testing environments, this class will typically be instantiated in the application context and the various
 * service implementations will be set <b>BY ID</b>. The use of service bean ids instead of direct bean references is
 * to avoid numerous problems with cyclic dependencies and other order dependent operations. So, a typical incarnation
 * might look like this:
 *
 * <pre>
 *       &lt;bean id=&quot;applicationServices&quot;
 *           class=&quot;org.springframework.richclient.application.support.DefaultApplicationServices&quot;&gt;
 *           &lt;property name=&quot;applicationObjectConfigurerId&quot;&gt;&lt;idref bean=&quot;applicationObjectConfigurer&quot; /&gt;&lt;/property&gt;
 *           &lt;property name=&quot;imageSourceId&quot;&gt;&lt;idref bean=&quot;imageSource&quot;/&gt;&lt;/property&gt;
 *           &lt;property name=&quot;rulesSourceId&quot;&gt;&lt;idref bean=&quot;rulesSource&quot;/&gt;&lt;/property&gt;
 *           &lt;property name=&quot;conversionServiceId&quot;&gt;&lt;idref bean=&quot;conversionService&quot;/&gt;&lt;/property&gt;
 *           &lt;property name=&quot;formComponentInterceptorFactoryId&quot;&gt;&lt;idref bean=&quot;formComponentInterceptorFactory&quot;/&gt;&lt;/property&gt;
 *       &lt;/bean&gt;
 * </pre>
 *
 * Note the use of the <code>idref</code> form instead of just using a string value. This is the preferred syntax in
 * order to avoid having misspelled bean names go unreported.
 * <p>
 * Which service implementation is returned by {@link #getService(Class)} will be determined through the following
 * steps:
 * <ol>
 * <li>Consult the current registry of service implementations which have been explicitly defined through bean
 * definition. If a registry entry was made using a bean id, this is the point at which it will be dereferenced into the
 * actual bean implementation. So, the bean impementation will not be referenced until it is requested.</li>
 * <li>If the service impl. is not found yet the short string name of the service' Java class in decapitalized
 * JavaBeans property format will be used to lookup the service implementation in the current application context.<br/>
 * If the service class is <code>org.springframework.richclient.factory.MenuFactory</code> the bean name
 * <code>menuFactory</code> will be used to find the bean</li>
 * <li>If the service impl. is not found yet and a default implementation can be provided, it will be constructed at
 * that time. Default implementations are provided for essentially all services referenced by the platform.</li>
 * </ol>
 *
 * @author Larry Streepy
 */
public class DefaultApplicationServices implements ApplicationServices, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog( DefaultApplicationServices.class );

    /** Map of services, keyed by service type (class). */
    private final Map services = Collections.synchronizedMap( new HashMap() );

    /** Map of service types to default implementation builders. */
    private static final Map serviceImplBuilders = new HashMap();

    /** Application context with needed bean definitions. */
    private ApplicationContext applicationContext;

    /** ID of the ApplicationObjectConfigurer bean. */
    private String applicationObjectConfigurerBeanId;

    /**
     * Default Constructor.
     */
    public DefaultApplicationServices() {
    }

    /**
     * Constuct using the given application context.
     *
     * @param applicationContext to use for locating named services (beans)
     */
    public DefaultApplicationServices( ApplicationContext applicationContext ) {
        setApplicationContext( applicationContext );
    }

    /**
     * Set the application context. We are ApplicationContextAware so this will happen
     * automatically if we are defined in the context. If not, then this method should be
     * called directly.
     */
    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    /**
     * @return application context
     */
    public ApplicationContext getApplicationContext() {
    	if (applicationContext == null) {
    		applicationContext = new GenericApplicationContext();
    	}
        return applicationContext;
    }

    /**
     * Get a service of the indicated type. If no service definition for the requested
     * type is found in the application context, then a reasonable default implementation
     * will be created.
     *
     * @param serviceType Type of service being requested
     * @return Service instance
     * @throws ServiceNotFoundException if the service is not found and no suitable
     *         default implementation is available.
     */
    public synchronized Object getService( Class serviceType ) {
        Assert.required( serviceType, "serviceType" );
        Object service = services.get( serviceType );
        if( service == null ) {
            service = getServiceForClassType(serviceType);
            if (service == null) {
                service = getDefaultImplementation(serviceType);
            }
            if (service != null) {
                services.put(serviceType, service);
            }
        } else {
            // Runtime derefence of refid's
            if( service instanceof String ) {
                service = getApplicationContext().getBean( (String) service, serviceType );
                services.put( serviceType, service );
            }
        }

        // If we still don't have an implementation, then it's a bust
        if( service == null ) {
            throw new ServiceNotFoundException(serviceType);
        }
        return service;
    }

    public boolean containsService( Class serviceType ) {
        Assert.required( serviceType, "serviceType" );
        return services.containsKey( serviceType ) || containsServiceForClassType(serviceType) || containsDefaultImplementation( serviceType );
    }

    /**
     * Add entries to the service registry. This is typically called from a bean
     * definition in the application context. The entryMap parameter must be a map with
     * keys that are either class instances (the serviceType) or the String name of the
     * class and values that are the implementation to use for that service or an idref to
     * a bean that is the implementation (passed as a String).
     *
     * @param entryMap Map of entries
     */
    public void setRegistryEntries( Map entryMap ) {
        Iterator iter = entryMap.entrySet().iterator();

        while( iter.hasNext() ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Class serviceType = null;
            Object key = entry.getKey();

            // If the key is a String, convert it to a class
            if( key instanceof String ) {
                try {
                    serviceType = Class.forName( (String) key );
                } catch( ClassNotFoundException e ) {
                    logger.error( "Unable to convert key to Class", e );
                }
            } else if( key instanceof Class ) {
                serviceType = (Class) key;
            } else {
                logger.error( "Invalid service entry key; must be String or Class, got: " + key.getClass() );
            }

            // If we got something usable, then add the map entry
            if( serviceType != null ) {
                services.put( serviceType, entry.getValue() );
            }
        }
    }

    /**
     * Set the application object configurer service implementation.
     *
     * @param applicationObjectConfigurer
     */
    public void setApplicationObjectConfigurer( ApplicationObjectConfigurer applicationObjectConfigurer ) {
        services.put( ApplicationObjectConfigurer.class, applicationObjectConfigurer );
    }

    /**
     * Set the application object configurer service implementation bean id
     *
     * @param applicationObjectConfigurerId bean id
     */
    public void setApplicationObjectConfigurerId( String applicationObjectConfigurerId ) {
        services.put( ApplicationObjectConfigurer.class, applicationObjectConfigurerId );
    }

    /**
     * Set the application security manager service implementation.
     *
     * @param applicationSecurityManager instance to use
     */
    public void setApplicationSecurityManager( ApplicationSecurityManager applicationSecurityManager ) {
        services.put( ApplicationSecurityManager.class, applicationSecurityManager );
    }

    /**
     * Set the application security manager service implementation bean id
     *
     * @param applicationSecurityManagerId bean id
     */
    public void setApplicationSecurityManagerId( String applicationSecurityManagerId ) {
        services.put( ApplicationSecurityManager.class, applicationSecurityManagerId );
    }

    /**
     * Set the <code>ApplicationWindow</code> factory service implementation
     *
     * @param factory
     */
    public void setApplicationWindowFactory( ApplicationWindowFactory factory ) {
        services.put( ApplicationWindowFactory.class, factory );
    }

    /**
     * Set the <code>ApplicationWindow</code> factory service implementation bean id
     *
     * @param factoryId bean id
     */
    public void setApplicationWindowFactoryId( String factoryId ) {
        services.put( ApplicationWindowFactory.class, factoryId );
    }

    /**
     * Set the <code>ApplicationPage</code> factory service implementation
     *
     * @param factory
     */
    public void setApplicationPageFactory( ApplicationPageFactory factory ) {
        services.put( ApplicationPageFactory.class, factory );
    }

    /**
     * Set the <code>ApplicationPage</code> factory service implementation bean id
     *
     * @param factoryId bean id
     */
    public void setApplicationPageFactoryId( String factoryId ) {
        services.put( ApplicationPageFactory.class, factoryId );
    }

    /**
     * Set the <code>PageComponentPane</code> factory service implementation bean
     *
     * @param factory bean id
     */
    public void setPageComponentPaneFactory( PageComponentPaneFactory factory ) {
        services.put( PageComponentPaneFactory.class, factory );
    }

    /**
     * Set the <code>PageComponentPane</code> factory service implementation bean id
     *
     * @param factoryId bean id
     */
    public void setPageComponentPaneFactoryId( String factoryId ) {
        services.put( PageComponentPaneFactory.class, factoryId );
    }

    /**
     * Set the binder selection strategy service implementation
     *
     * @param binderSelectionStrategy
     */
    public void setBinderSelectionStrategy( BinderSelectionStrategy binderSelectionStrategy ) {
        services.put( BinderSelectionStrategy.class, binderSelectionStrategy );
    }

    /**
     * Set the binder selection strategy service implementation bean id
     *
     * @param binderSelectionStrategyId bean id
     */
    public void setBinderSelectionStrategyId( String binderSelectionStrategyId ) {
        services.put( BinderSelectionStrategy.class, binderSelectionStrategyId );
    }

    /**
     * Set the binding factory provider service implementation
     *
     * @param bindingFactoryProvider
     */
    public void setBindingFactoryProvider( BindingFactoryProvider bindingFactoryProvider ) {
        services.put( BindingFactoryProvider.class, bindingFactoryProvider );
    }

    /**
     * Set the binding factory provider service implementation bean id
     *
     * @param bindingFactoryProviderId bean id
     */
    public void setBindingFactoryProviderId( String bindingFactoryProviderId ) {
        services.put( BindingFactoryProvider.class, bindingFactoryProviderId );
    }

    /**
     * Set the command services service implementation
     *
     * @param commandServices
     */
    public void setCommandServices( CommandServices commandServices ) {
        services.put( CommandServices.class, commandServices );
    }

    /**
     * Set the command services service implementation bean id
     *
     * @param commandServicesId bean id
     */
    public void setCommandServicesId( String commandServicesId ) {
        services.put( CommandServices.class, commandServicesId );
    }

    /**
     * Set the command configurer service implementation
     *
     * @param commandConfigurer
     */
    public void setCommandConfigurer( CommandConfigurer commandConfigurer ) {
        services.put( CommandConfigurer.class, commandConfigurer );
    }

    /**
     * Set the command configurer service implementation bean id
     *
     * @param commandConfigurerId bean id
     */
    public void setCommandConfigurerId( String commandConfigurerId ) {
        services.put( CommandConfigurer.class, commandConfigurerId );
    }

    /**
     * Set the button factory service implementation
     *
     * @param buttonFactory
     */
    public void setButtonFactory( ButtonFactory buttonFactory ) {
        services.put( ButtonFactory.class, buttonFactory );
    }

    /**
     * Set the button factory service implementation bean id
     *
     * @param buttonFactoryId bean id
     */
    public void setButtonFactoryId( String buttonFactoryId ) {
        services.put( ButtonFactory.class, buttonFactoryId );
    }

    /**
     * Set the menu factory service implementation
     *
     * @param menuFactory
     */
    public void setMenuFactory( MenuFactory menuFactory ) {
        services.put( MenuFactory.class, menuFactory );
    }

    /**
     * Set the menu factory service implementation bean id
     *
     * @param menuFactoryId bean id
     */
    public void setMenuFactoryId( String menuFactoryId ) {
        services.put( MenuFactory.class, menuFactoryId );
    }

    /**
     * Set the component factory service implementation
     *
     * @param componentFactory
     */
    public void setComponentFactory( ComponentFactory componentFactory ) {
        services.put( ComponentFactory.class, componentFactory );
    }

    /**
     * Set the component factory service implementation bean id
     *
     * @param componentFactoryId bean id
     */
    public void setComponentFactoryId( String componentFactoryId ) {
        services.put( ComponentFactory.class, componentFactoryId );
    }

    /**
     * Set the conversion service service implementation
     *
     * @param conversionService
     */
    public void setConversionService( ConversionService conversionService ) {
        services.put( ConversionService.class, conversionService );
    }

    /**
     * Set the conversion service service implementation bean id
     *
     * @param conversionServiceId bean id
     */
    public void setConversionServiceId( String conversionServiceId ) {
        services.put( ConversionService.class, conversionServiceId );
    }

    /**
     * Set the form component interceptor factory service implementation
     *
     * @param formComponentInterceptorFactory
     */
    public void setFormComponentInterceptorFactory( FormComponentInterceptorFactory formComponentInterceptorFactory ) {
        services.put( FormComponentInterceptorFactory.class, formComponentInterceptorFactory );
    }

    /**
     * Set the form component interceptor factory service implementation bean id
     *
     * @param formComponentInterceptorFactoryId bean id
     */
    public void setFormComponentInterceptorFactoryId( String formComponentInterceptorFactoryId ) {
        services.put( FormComponentInterceptorFactory.class, formComponentInterceptorFactoryId );
    }

    /**
     * Set the field face descriptor source service implementation
     *
     * @param fieldFaceSource
     */
    public void setFieldFaceSource( FieldFaceSource fieldFaceSource ) {
        services.put( FieldFaceSource.class, fieldFaceSource );
    }

    /**
     * Set the field face descriptor source service implementation bean id
     *
     * @param fieldFaceSourceId bean id
     */
    public void setFieldFaceSourceId( String fieldFaceSourceId ) {
        services.put( FieldFaceSource.class, fieldFaceSourceId );
    }

    /**
     * Set the icon source service implementation
     *
     * @param iconSource
     */
    public void setIconSource( IconSource iconSource ) {
        services.put( IconSource.class, iconSource );
    }

    /**
     * Set the icon source service implementation bean id
     *
     * @param iconSourceId bean id
     */
    public void setIconSourceId( String iconSourceId ) {
        services.put( IconSource.class, iconSourceId );
    }

    /**
     * Set the image source service implementation
     *
     * @param imageSource
     */
    public void setImageSource( ImageSource imageSource ) {
        services.put( ImageSource.class, imageSource );
    }

    /**
     * Set the image source service implementation bean id
     *
     * @param imageSourceId bean id
     */
    public void setImageSourceId( String imageSourceId ) {
        services.put( ImageSource.class, imageSourceId );
    }

    /**
     * Set the labeled enum resolver service implementation
     *
     * @param labeledEnumResolver
     */
    public void setLabeledEnumResolver( LabeledEnumResolver labeledEnumResolver ) {
        services.put( LabeledEnumResolver.class, labeledEnumResolver );
    }

    /**
     * Set the labeled enum resolver service implementation bean id
     *
     * @param labeledEnumResolverId bean id
     */
    public void setLabeledEnumResolverId( String labeledEnumResolverId ) {
        services.put( LabeledEnumResolver.class, labeledEnumResolverId );
    }

    /**
     * Set the message source service implementation
     *
     * @param messageSource
     */
    public void setMessageSource( MessageSource messageSource ) {
        services.put( MessageSource.class, messageSource );
    }

    /**
     * Set the message source service implementation bean id
     *
     * @param messageSourceId bean id
     */
    public void setMessageSourceId( String messageSourceId ) {
        services.put( MessageSource.class, messageSourceId );
    }

    /**
     * Set the message source accessor service implementation
     *
     * @param messageSourceAccessor
     */
    public void setMessageSourceAccesor( MessageSourceAccessor messageSourceAccessor ) {
        services.put( MessageSourceAccessor.class, messageSourceAccessor );
    }

    /**
     * Set the message source accessor service implementation bean id
     *
     * @param messageSourceAccessorId bean id
     */
    public void setMessageSourceAccesorId( String messageSourceAccessorId ) {
        services.put( MessageSourceAccessor.class, messageSourceAccessorId );
    }

    /**
     * Set the rules source service implementation
     *
     * @param rulesSource
     */
    public void setRulesSource( RulesSource rulesSource ) {
        services.put( RulesSource.class, rulesSource );
    }

    /**
     * Set the rules source service implementation bean id
     *
     * @param rulesSourceId bean id
     */
    public void setRulesSourceId( String rulesSourceId ) {
        services.put( RulesSource.class, rulesSourceId );
    }

    /**
     * Set the security controller manager service implementation
     *
     * @param securityControllerManager instance to use
     */
    public void setSecurityControllerManager( SecurityControllerManager securityControllerManager ) {
        services.put( SecurityControllerManager.class, securityControllerManager );
    }

    /**
     * Set the security controller manager service implementation bean id
     *
     * @param securityControllerManagerId bean id
     */
    public void setSecurityControllerManagerId( String securityControllerManagerId ) {
        services.put( SecurityControllerManager.class, securityControllerManagerId );
    }

    /**
     * Set the value change detector service imlpementation.
     *
     * @param valueChangeDetector instance to use
     */
    public void setValueChangeDetector( ValueChangeDetector valueChangeDetector ) {
        services.put( ValueChangeDetector.class, valueChangeDetector );
    }

    /**
     * Set the value change detector service imlpementation bean id
     *
     * @param valueChangeDetectorId bean id
     */
    public void setValueChangeDetectorId( String valueChangeDetectorId ) {
        services.put( ValueChangeDetector.class, valueChangeDetectorId );
    }

    /**
     * Set the view descriptor registry service implementation
     *
     * @param viewDescriptorRegistry
     */
    public void setViewDescriptorRegistry( ViewDescriptorRegistry viewDescriptorRegistry ) {
        services.put( ViewDescriptorRegistry.class, viewDescriptorRegistry );
    }

    /**
     * Set the page descriptor registry service implementation
     *
     * @param pageDescriptorRegistry
     */
    public void setPageDescriptorRegistry( PageDescriptorRegistry pageDescriptorRegistry ) {
        services.put( PageDescriptorRegistry.class, pageDescriptorRegistry );
    }

    /**
     * Set the message translator registry service implementation
     *
     * @param messageTranslatorFactory
     */
    public void setMessageTranslatorFactory( MessageTranslatorFactory messageTranslatorFactory ) {
        services.put( MessageTranslatorFactory.class, messageTranslatorFactory );
    }

    /**
     * Set the message translator registry service implementation bean id
     *
     * @param messageTranslatorFactory
     */
    public void setMessageTranslatorFactoryId( String messageTranslatorFactoryId ) {
        services.put( MessageTranslatorFactory.class, messageTranslatorFactoryId );
    }

    /**
     * Set the view descriptor registry service implementation bean id
     *
     * @param viewDescriptorRegistryId bean id
     */
    public void setViewDescriptorRegistryId( String viewDescriptorRegistryId ) {
        services.put( ViewDescriptorRegistry.class, viewDescriptorRegistryId );
    }

    /**
     * Set the page descriptor registry service implementation bean id
     *
     * @param pageDescriptorRegistryId bean id
     */
    public void setPageDescriptorRegistryId( String pageDescriptorRegistryId ) {
        services.put( PageDescriptorRegistry.class, pageDescriptorRegistryId );
    }

    /**
     * Get the implementation of a service by using the decapitalized shortname of the serviceType class name.
     *
     * @param serviceType
     *            the service class to lookup the bean definition
     * @return the found service implementation if a bean definition can be found and it implements the required service
     *         type, otherwise null
     * @see ClassUtils#getShortNameAsProperty(Class)
     */
    protected Object getServiceForClassType(Class serviceType) {
        String lookupName = ClassUtils.getShortNameAsProperty(serviceType);
        ApplicationContext ctx = getApplicationContext();
        if (ctx.containsBean(lookupName)) {
            Object bean = ctx.getBean(lookupName);
            if (serviceType.isAssignableFrom(bean.getClass())) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Using bean '" + lookupName + "' (" + bean.getClass().getName() + ") for service " + serviceType.getName());
                }
                return bean;
            }
            else if(logger.isDebugEnabled()){
                logger.debug("Bean with id '" + lookupName + "' (" + bean.getClass().getName() + ") does not implement " + serviceType.getName());
            }
        } else if(logger.isDebugEnabled()){
            logger.debug("No Bean with id '" + lookupName + "' found for service " + serviceType.getName());
        }
        return null;
    }


    /**
     * Get the default implementation of a service according to the service type. If no
     * default implementation is available, then a null is returned.
     *
     * @param serviceType Type of service requested
     * @return Default service implementation, or null if none defined
     */
    protected Object getDefaultImplementation( Class serviceType ) {
        Object impl = null;
        ImplBuilder builder = (ImplBuilder) serviceImplBuilders.get( serviceType );
        if( builder != null ) {
            impl = builder.build( this );
        }
        return impl;
    }

    /**
     * Tests if the application context contains a bean definition by using the decapitalized shortname of the serviceType class name
     * @param serviceType the service class to lookup the bean definition
     * @return true if a bean definition is found in the current application context, otherwise false
     *
     * @see ClassUtils#getShortNameAsProperty(Class)
     */
    protected boolean containsServiceForClassType(Class serviceType) {
        return getApplicationContext().containsBean(ClassUtils.getShortNameAsProperty(serviceType));
    }

    /**
     * Tests if a default implementation for the requested service type is available
     *
     * @param serviceType the requested service type
     * @return true if a default implementation is available otherwise false.
     */
    protected boolean containsDefaultImplementation( Class serviceType ) {
        return serviceImplBuilders.containsKey( serviceType );
    }

    /**
     * Internal interface used to provide default implementation builders.
     */
    protected interface ImplBuilder {
        /**
         * Build the service implementation.
         *
         * @param applicationServices reference to service locator
         * @return service implementation
         */
        Object build( DefaultApplicationServices applicationServices );
    }

    protected static final ImplBuilder applicationContextImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            return applicationServices.getApplicationContext();
        }
    };

    protected static final ImplBuilder menuFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: MenuFactory" );
            return new DefaultMenuFactory();
        }
    };

    protected static final ImplBuilder buttonFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ButtonFactory" );
            return new DefaultButtonFactory();
        }
    };

    protected static final ImplBuilder commandServicesImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: CommandServices" );
            return new DefaultCommandServices();
        }
    };

    protected static final ImplBuilder componentFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ComponentFactory" );
            return new DefaultComponentFactory();
        }
    };

    protected static final ImplBuilder formComponentInterceptorFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: FormComponentInterceptorFactory" );
            return new FormComponentInterceptorFactory() {
                public FormComponentInterceptor getInterceptor( FormModel formModel ) {
                    return null;
                }
            };
        }
    };

    protected static final ImplBuilder applicationObjectConfigurerImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            // First see if there is an AOC in the context, if not construct a default
            Object impl = null;
            String aocBeanId = applicationServices.applicationObjectConfigurerBeanId;
            if( aocBeanId != null ) {
                try {
                    impl = applicationServices.getApplicationContext().getBean( aocBeanId,
                            ApplicationObjectConfigurer.class );
                } catch( NoSuchBeanDefinitionException e ) {
                    logger.info( "No object configurer found in context under name '" + aocBeanId
                            + "'; configuring defaults." );
                    impl = new DefaultApplicationObjectConfigurer((MessageSource)applicationServices.getService(MessageSource.class));
                }
            } else {
                logger.info( "No object configurer bean Id has been set; configuring defaults." );
                impl = new DefaultApplicationObjectConfigurer((MessageSource)applicationServices.getService(MessageSource.class));
            }
            return impl;
        }
    };

    protected static final ImplBuilder commandConfigurerImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: CommandConfigurer" );
            return new DefaultCommandConfigurer();
        }
    };

    protected static final ImplBuilder imageSourceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ImageSource" );
            try {
                ResourceMapFactoryBean imageResourcesFactory = new ResourceMapFactoryBean();
                imageResourcesFactory.setLocation(new ClassPathResource("org/springframework/richclient/image/images.properties"));
                imageResourcesFactory.afterPropertiesSet();
				return new DefaultImageSource((Map)imageResourcesFactory.getObject());
			}
			catch (IOException e) {
				return new DefaultImageSource(new HashMap());
			}
        }
    };

    protected static final ImplBuilder iconSourceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: IconSource" );
            return new DefaultIconSource();
        }
    };

    protected static final ImplBuilder rulesSourceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: RulesSource" );
            return new DefaultRulesSource();
        }
    };

    protected static final ImplBuilder conversionServiceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ConversionService" );
            return new DefaultConversionServiceFactoryBean().getConversionService();
        }
    };

    protected static final ImplBuilder binderSelectionStrategyImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: BinderSelectionStrategy" );
            return new SwingBinderSelectionStrategy();
        }
    };

    protected static final ImplBuilder FieldFaceSourceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: FieldFaceSource" );
            return new MessageSourceFieldFaceSource();
        }
    };

    protected static final ImplBuilder bindingFactoryProviderImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: BindingFactoryProvider" );
            return new SwingBindingFactoryProvider();
        }
    };

    protected static final ImplBuilder valueChangeDetectorImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ValueChangeDetector" );
            return new DefaultValueChangeDetector();
        }
    };

    protected static final ImplBuilder applicationSecurityManagerImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ApplicationSecurityManager" );
            return new DefaultApplicationSecurityManager( true );
        }
    };

    protected static final ImplBuilder SecurityControllerManagerImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: SecurityControllerManager" );
            return new DefaultSecurityControllerManager();
        }
    };

    protected static final ImplBuilder viewDescriptorRegistryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ViewDescriptorRegistry" );
            BeanFactoryViewDescriptorRegistry impl = new BeanFactoryViewDescriptorRegistry();
            impl.setApplicationContext( applicationServices.getApplicationContext() );
            return impl;
        }
    };

    protected static final ImplBuilder pageDescriptorRegistryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: PageDescriptorRegistry" );
            BeanFactoryPageDescriptorRegistry impl = new BeanFactoryPageDescriptorRegistry();
            impl.setApplicationContext( applicationServices.getApplicationContext() );
            return impl;
        }
    };

    protected static final ImplBuilder messageTranslatorFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: MessageTranslatorFactory" );
            DefaultMessageTranslatorFactory impl = new DefaultMessageTranslatorFactory();
            impl.setMessageSource( applicationServices.getApplicationContext() );
            return impl;
        }
    };

    protected static final ImplBuilder labeledEnumResolverImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: LabeledEnumResolver" );
            return new StaticLabeledEnumResolver();
        }
    };

    protected static final ImplBuilder messageSourceImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            // The application context is our properly configured message source
            logger.info( "Using MessageSource from application context" );
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("org.springframework.richclient.application.messages");
            return messageSource;
        }
    };

    protected static final ImplBuilder messageSourceAccessorImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            // Just construct one on top of the current message source
            return new MessageSourceAccessor( (MessageSource) applicationServices.getService( MessageSource.class ) );
        }
    };

    protected static final ImplBuilder applicationWindowFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ApplicationWindowFactory" );
            return new DefaultApplicationWindowFactory();
        }
    };

    protected static final ImplBuilder applicationPageFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: ApplicationPageFactory" );
            return new DefaultApplicationPageFactory();
        }
    };

    protected static final ImplBuilder pageComponentPaneFactoryImplBuilder = new ImplBuilder() {
        public Object build( DefaultApplicationServices applicationServices ) {
            logger.info( "Creating default service impl: PageComponentPaneFactory" );
            return new DefaultPageComponentPaneFactory();
        }
    };

    /**
     * Static initializer to construct the implementation builder map.
     */
    static {
        // Default service implementation builders
        serviceImplBuilders.put( ApplicationContext.class, applicationContextImplBuilder );
        serviceImplBuilders.put( ApplicationObjectConfigurer.class, applicationObjectConfigurerImplBuilder );
        serviceImplBuilders.put( ApplicationSecurityManager.class, applicationSecurityManagerImplBuilder );
        serviceImplBuilders.put( ApplicationPageFactory.class, applicationPageFactoryImplBuilder );
        serviceImplBuilders.put( ApplicationWindowFactory.class, applicationWindowFactoryImplBuilder );
        serviceImplBuilders.put( PageComponentPaneFactory.class, pageComponentPaneFactoryImplBuilder );
        serviceImplBuilders.put( BinderSelectionStrategy.class, binderSelectionStrategyImplBuilder );
        serviceImplBuilders.put( BindingFactoryProvider.class, bindingFactoryProviderImplBuilder );
        serviceImplBuilders.put( ButtonFactory.class, buttonFactoryImplBuilder );
        serviceImplBuilders.put( MenuFactory.class, menuFactoryImplBuilder );
        serviceImplBuilders.put( CommandServices.class, commandServicesImplBuilder );
        serviceImplBuilders.put( CommandConfigurer.class, commandConfigurerImplBuilder );
        serviceImplBuilders.put( ComponentFactory.class, componentFactoryImplBuilder );
        serviceImplBuilders.put( ConversionService.class, conversionServiceImplBuilder );
        serviceImplBuilders.put( FormComponentInterceptorFactory.class, formComponentInterceptorFactoryImplBuilder );
        serviceImplBuilders.put( FieldFaceSource.class, FieldFaceSourceImplBuilder );
        serviceImplBuilders.put( IconSource.class, iconSourceImplBuilder );
        serviceImplBuilders.put( ImageSource.class, imageSourceImplBuilder );
        serviceImplBuilders.put( LabeledEnumResolver.class, labeledEnumResolverImplBuilder );
        serviceImplBuilders.put( MessageSource.class, messageSourceImplBuilder );
        serviceImplBuilders.put( MessageSourceAccessor.class, messageSourceAccessorImplBuilder );
        serviceImplBuilders.put( RulesSource.class, rulesSourceImplBuilder );
        serviceImplBuilders.put( SecurityControllerManager.class, SecurityControllerManagerImplBuilder );
        serviceImplBuilders.put( ValueChangeDetector.class, valueChangeDetectorImplBuilder );
        serviceImplBuilders.put( ViewDescriptorRegistry.class, viewDescriptorRegistryImplBuilder );
        serviceImplBuilders.put( PageDescriptorRegistry.class, pageDescriptorRegistryImplBuilder );
        serviceImplBuilders.put( MessageTranslatorFactory.class, messageTranslatorFactoryImplBuilder );
    }
}
