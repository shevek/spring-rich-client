/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.application;

import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.richclient.factory.ComponentFactory;

/**
 * @author Keith Donald
 */
public class ApplicationServicesAccessorSupport {
    protected ApplicationContext getApplicationContext() {
        return Application.locator().getApplicationContext();
    }

    protected String getMessage(String messageCode) {
        return getApplicationContext().getMessage(messageCode, null,
                messageCode, Locale.getDefault());
    }

    protected ComponentFactory getComponentFactory() {
        return Application.locator().getComponentFactory();
    }

}