/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.support.ApplicationServicesAccessor;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public abstract class AbstractBinder extends ApplicationServicesAccessor implements Binder {

    /**
     * The client property key that is used to save a copy of a binding in its bound 
     * component's client property set. This can be used to locate the binding 
     * that has bound a given component.
     * @see JComponent#getClientProperty(java.lang.Object)
     */
    public static final String BINDING_CLIENT_PROPERTY_KEY = "binding";

    protected final Log log = LogFactory.getLog(getClass());

    private final Class requiredSourceClass;

    private final Set supportedContextKeys;

    private boolean readOnly;

    protected AbstractBinder(Class requiredSourceClass) {
        this.requiredSourceClass = requiredSourceClass;
        this.supportedContextKeys = Collections.EMPTY_SET;
    }

    protected AbstractBinder(Class requiredSourceClass, String[] supportedContextKeys) {
        this.requiredSourceClass = requiredSourceClass;
        this.supportedContextKeys = new HashSet(Arrays.asList(supportedContextKeys));
    }

    protected void validateContextKeys(Map context) {
        Set unkownKeys = new HashSet(context.keySet());
        unkownKeys.removeAll(supportedContextKeys);
        for (Iterator i = unkownKeys.iterator(); i.hasNext();) {
            final Object key = i.next();
            context.remove(key);
            if (log.isWarnEnabled()) {
                log.warn("Context key '" + key + "' not supported.");
            }
        }
    }
    
    public Class getRequiredSourceClass() {
        return requiredSourceClass;
    }

    public Binding bind(FormModel formModel, String formPropertyPath, Map context) {
        JComponent control = createControl(context);
        Assert.notNull(control, "This binder does not support creating a default control.");
        return bind(control, formModel, formPropertyPath, context);
    }

    protected abstract JComponent createControl(Map context);

    public Binding bind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        // Ensure that this component has not already been bound
        Binding binding = (Binding)control.getClientProperty(BINDING_CLIENT_PROPERTY_KEY);
        if( binding != null ) {
            throw new IllegalStateException( "Component is already bound to property: " + binding.getProperty());
        }
        validateContextKeys(context);
        binding = doBind(control, formModel, formPropertyPath, context);
        control.putClientProperty(BINDING_CLIENT_PROPERTY_KEY, binding);
        return binding;
    }

    protected abstract Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context);

    protected Class getPropertyType(FormModel formModel, String formPropertyPath) {
        return formModel.getFieldMetadata(formPropertyPath).getPropertyType();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}