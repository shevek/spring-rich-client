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
 *
 */

package org.springframework.richclient.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.util.Assert;

/**
 * Abstract <code>FormUIProvider</code> implementation. Extenders only need to implement
 * the <code>createControl()</code> and <code>getComponent(String id)</code> methods.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractFormUIProvider extends AbstractControlFactory implements FormUIProvider {
    private boolean bound = false;
    private String[] properties;
    private Map contextMap = new HashMap();

    public void bind(BindingFactory factory, Form form) {
        Assert.state(properties != null && properties.length > 0, "Properties must be set");

        bound = true;

        for (int i = 0; i < properties.length; i++) {
            factory.bindControl(getComponent(properties[i]), properties[i], getContext(properties[i]));
        }
    }

    public Map getContext(String propertyPath) {
        return contextMap.containsKey(propertyPath) ? (Map)contextMap.get(propertyPath) : Collections.EMPTY_MAP;
    }

    public void setContext(String propertyPath, Map context) {
        contextMap.put(propertyPath, context);
    }

    public void setProperties(String[] properties) {
        Assert.state(!bound, "You cannot set the form properties after the binding");

        this.properties = properties;
    }

    public String[] getProperties() {
        return properties;
    }

}
