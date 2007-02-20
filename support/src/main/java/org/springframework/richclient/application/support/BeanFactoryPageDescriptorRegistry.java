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
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageDescriptorRegistry;
import org.springframework.richclient.util.Assert;


/**
 * A simple {@link PageDescriptorRegistry} implementation that pulls singleton page definitions out
 * of a spring application context. This class is intended to be managed by a Spring IoC container.
 * If being created programatically, be sure to call the
 * {@link #setApplicationContext(org.springframework.context.ApplicationContext)} method.
 *
 *
 * @author Keith Donald
 * @author Kevin Stembridge
 * @author Rogan Dawes
 */
public class BeanFactoryPageDescriptorRegistry extends ApplicationObjectSupport implements PageDescriptorRegistry {

    /**
     * {@inheritDoc}
     */
    public PageDescriptor[] getPageDescriptors() {
        Map beans = getApplicationContext().getBeansOfType(PageDescriptor.class, false, false);
        return (PageDescriptor[])beans.values().toArray(new PageDescriptor[beans.size()]);
    }

    /**
     * Returns the page descriptor with the given identifier, or null if no such bean definition
     * with the given name exists in the current application context.
     *
     * @param pageName The bean name of the page descriptor that is to be retrieved from the
     * underlying application context. Must not be null.
     *
     * @throws IllegalArgumentException if {@code pageName} is null.
     * @throws BeanNotOfRequiredTypeException if the bean retrieved from the underlying application
     * context is not of type {@link PageDescriptor}.
     *
     */
    public PageDescriptor getPageDescriptor(String pageName) {

        Assert.required(pageName, "pageName");

        try {
            return (PageDescriptor) getApplicationContext().getBean(pageName, PageDescriptor.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            return null;
        }

    }

}
