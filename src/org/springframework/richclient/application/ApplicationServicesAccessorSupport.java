/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.application;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.config.ObjectConfigurer;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;

/**
 * @author Keith Donald
 */
public class ApplicationServicesAccessorSupport {
    protected final Log logger = LogFactory.getLog(getClass());

    protected ApplicationContext getApplicationContext() {
        return Application.locator().getApplicationContext();
    }

    protected MessageSourceAccessor getMessageSourceAccessor() {
        return Application.locator().getMessages();
    }

    protected String getMessage(String messageCode) {
        return getApplicationContext().getMessage(messageCode, null,
                messageCode, Locale.getDefault());
    }

    protected String getMessage(String messageCode, Object[] args) {
        return getApplicationContext().getMessage(messageCode, args,
                messageCode, Locale.getDefault());
    }

    protected ObjectConfigurer getObjectConfigurer() {
        return Application.locator();
    }
    
    protected ComponentFactory getComponentFactory() {
        return Application.locator().getComponentFactory();
    }

    protected ImageSource getImageSource() {
        return Application.locator();
    }

    protected IconSource getIconSource() {
        return Application.locator();
    }

}