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
package org.springframework.richclient.form.builder.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.util.Assert;

/**
 * @author oliverh
 */
public class ChainedInterceptorFactory implements FormComponentInterceptorFactory {

    public List interceptorFactories = Collections.EMPTY_LIST;

    public ChainedInterceptorFactory() {
    }

    public void setInterceptorFactories(List interceptorFactories) {
        Assert.notNull(interceptorFactories);
        this.interceptorFactories = interceptorFactories;
    }

    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        List interceptors = getInterceptors(formModel);
        if (interceptors.size() == 0) {
            return null;
        }
        return new ChainedInterceptor(interceptors);
    }

    private List getInterceptors(FormModel formModel) {
        List interceptors = new ArrayList();
        for (Iterator i = interceptorFactories.iterator(); i.hasNext();) {
            FormComponentInterceptor interceptor = ((FormComponentInterceptorFactory)i.next())
                    .getInterceptor(formModel);
            if (interceptor != null) {
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

    private static class ChainedInterceptor implements FormComponentInterceptor {
        private List interceptors;

        public ChainedInterceptor(List interceptors) {
            this.interceptors = interceptors;
        }

        public void processLabel(String propertyName, JComponent label) {
            for (Iterator i = interceptors.iterator(); i.hasNext();) {
                FormComponentInterceptor interceptor = ((FormComponentInterceptor)i.next());
                interceptor.processLabel(propertyName, label);
            }
        }

        public void processComponent(String propertyName, JComponent component) {
            for (Iterator i = interceptors.iterator(); i.hasNext();) {
                FormComponentInterceptor interceptor = ((FormComponentInterceptor)i.next());
                interceptor.processComponent(propertyName, component);
            }
        }
    }
}