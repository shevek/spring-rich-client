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

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;


/**
 * Simple <code>ViewRegistry</code> that pulls singleton view definitions out
 * of a spring application context.
 * 
 * @author Keith Donald
 */
public class BeanFactoryViewDescriptorRegistry extends ApplicationObjectSupport
    implements ViewDescriptorRegistry {

    public ViewDescriptor[] getViewDescriptors() {
        Map beans = getApplicationContext().getBeansOfType(ViewDescriptor.class, false, false);
        return (ViewDescriptor[])beans.values().toArray(new ViewDescriptor[beans.size()]);
    }


    public ViewDescriptor getViewDescriptor(String viewName) {
        final Object bean = getApplicationContext().getBean(viewName);
        try {
            return (ViewDescriptor)bean;
        }
        catch (ClassCastException e) {
            ClassCastException exp = new ClassCastException(bean.getClass() + " is not a " + ViewDescriptor.class +
                " when looking up '" + viewName + "'");
            exp.setStackTrace(e.getStackTrace());
            throw exp;
        }
    }

}