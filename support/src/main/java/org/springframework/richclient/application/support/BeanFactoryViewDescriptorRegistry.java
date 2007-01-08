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

import java.util.Map;

import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.util.Assert;


/**
 * A simple {@link ViewDescriptorRegistry} implementation that pulls singleton view definitions out
 * of a spring application context. This class is intended to be managed by a Spring IoC container.
 * If being created programatically, be sure to call the 
 * {@link #setApplicationContext(org.springframework.context.ApplicationContext)} method.
 * 
 * 
 * @author Keith Donald
 * @author Kevin Stembridge
 */
public class BeanFactoryViewDescriptorRegistry extends ApplicationObjectSupport implements ViewDescriptorRegistry {

    /**
     * {@inheritDoc}
     */
    public ViewDescriptor[] getViewDescriptors() {
        Map beans = getApplicationContext().getBeansOfType(ViewDescriptor.class, false, false);
        return (ViewDescriptor[])beans.values().toArray(new ViewDescriptor[beans.size()]);
    }

    /**
     * Returns the view descriptor with the given identifier, or null if no such bean definition 
     * with the given name exists in the current application context.
     * 
     * @param viewName The bean name of the view descriptor that is to be retrieved from the 
     * underlying application context. Must not be null.
     * 
     * @throws IllegalArgumentException if {@code viewName} is null.
     * @throws BeanNotOfRequiredTypeException if the bean retrieved from the underlying application
     * context is not of type {@link ViewDescriptor}.
     * 
     */
    public ViewDescriptor getViewDescriptor(String viewName) {
        
        Assert.required(viewName, "viewName");
        
        try {
            return (ViewDescriptor) getApplicationContext().getBean(viewName, ViewDescriptor.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }
        
    }

}
