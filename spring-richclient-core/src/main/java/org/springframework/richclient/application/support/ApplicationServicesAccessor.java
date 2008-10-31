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
package org.springframework.richclient.application.support;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;

/**
 * @author Keith Donald
 */
public class ApplicationServicesAccessor {
    protected final Log logger = LogFactory.getLog(getClass());

    protected String getApplicationName() {
        return getApplication().getName();
    }

    protected Application getApplication() {
        return Application.instance();
    }

    protected ApplicationServices getApplicationServices() {
        return ApplicationServicesLocator.services();
    }

    protected Object getService(Class serviceType) {
        return getApplicationServices().getService(serviceType);
    }

    protected ApplicationContext getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    protected ComponentFactory getComponentFactory() {
        return (ComponentFactory)getService(ComponentFactory.class);
    }

    protected MessageSource getMessageSource() {
        return getApplicationContext();
    }

    protected MessageSourceAccessor getMessages() {
        return (MessageSourceAccessor)ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
    }

    protected ImageSource getImageSource() {
        return (ImageSource)getService(ImageSource.class);
    }

    protected IconSource getIconSource() {
        return (IconSource)getService(IconSource.class);
    }

    protected ApplicationObjectConfigurer getObjectConfigurer() {
        return (ApplicationObjectConfigurer)getService(ApplicationObjectConfigurer.class);
    }

    protected CommandConfigurer getCommandConfigurer() {
        return (CommandConfigurer)getService(CommandConfigurer.class);
    }

    protected ApplicationWindow getActiveWindow() {
        return getApplication().getActiveWindow();
    }
    
    protected ConversionService getConversionService() {
        return (ConversionService)getService(ConversionService.class);
    }

    protected String getMessage(String messageCode) {
        return getApplicationContext().getMessage(messageCode, null, messageCode, Locale.getDefault());
    }

    protected String getMessage(final String[] messageCodes) {
        MessageSourceResolvable resolvable = new MessageSourceResolvable() {
            public String[] getCodes() {
                return messageCodes;
            }

            public Object[] getArguments() {
                return new Object[0];
            }

            public String getDefaultMessage() {
                return messageCodes[0];
            }
        };
        return getApplicationContext().getMessage(resolvable, Locale.getDefault());
    }

    protected String getMessage(String messageCode, Object[] args) {
        return getApplicationContext().getMessage(messageCode, args, messageCode, Locale.getDefault());
    }

    protected String getMessage(final String[] messageCodes, final Object[] args) {
        MessageSourceResolvable resolvable = new MessageSourceResolvable() {
            public String[] getCodes() {
                return messageCodes;
            }

            public Object[] getArguments() {
                return args;
            }

            public String getDefaultMessage() {
                return messageCodes[0];
            }
        };
        return getApplicationContext().getMessage(resolvable, Locale.getDefault());
    }

}