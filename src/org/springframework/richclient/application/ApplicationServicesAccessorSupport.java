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

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.config.ObjectConfigurer;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class ApplicationServicesAccessorSupport {
    protected final Log logger = LogFactory.getLog(getClass());

    protected ApplicationContext getApplicationContext() {
        return Application.services().getApplicationContext();
    }

    protected MessageSourceAccessor getMessageSourceAccessor() {
        return Application.services().getMessages();
    }

    protected String getMessage(String messageCode) {
        return getApplicationContext().getMessage(messageCode, null,
                messageCode, Locale.getDefault());
    }

    protected String getMessage(final String[] messageCodes) {
        Assert.hasElements(messageCodes);
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
        return getApplicationContext().getMessage(resolvable,
                Locale.getDefault());
    }

    protected String getMessage(String messageCode, Object[] args) {
        return getApplicationContext().getMessage(messageCode, args,
                messageCode, Locale.getDefault());
    }

    protected String getMessage(final String[] messageCodes, final Object[] args) {
        Assert.hasElements(messageCodes);
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
        return getApplicationContext().getMessage(resolvable,
                Locale.getDefault());
    }

    protected ObjectConfigurer getObjectConfigurer() {
        return Application.services().getObjectConfigurer();
    }

    protected ComponentFactory getComponentFactory() {
        return Application.services().getComponentFactory();
    }

    protected ImageSource getImageSource() {
        return Application.services().getImageSource();
    }

    protected IconSource getIconSource() {
        return Application.services().getIconSource();
    }

}