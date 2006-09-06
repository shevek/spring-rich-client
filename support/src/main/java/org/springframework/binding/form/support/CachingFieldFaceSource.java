/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.binding.form.FieldFace;
import org.springframework.binding.form.FieldFaceSource;
import org.springframework.binding.form.FormModel;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;

/**
 * A convenience superclass for FieldFaceSource's that require caching to improve the performance of FieldFace lookup.
 * 
 * <p>
 * FieldFace retrieval is delegated to subclasses using one of the {@link #loadFieldFace(FormModel, String)},
 * {@link #loadFieldFace(String, String)} or {@link #loadFieldFace(String, String, Map)} methods.
 * 
 * @author Oliver Hutchison
 * @author Mathias Broekelmann
 */
public abstract class CachingFieldFaceSource implements FieldFaceSource {

    private static final Object DEFAULT_CONTEXT = new Object();

    /*
     * A cache with FormModel keys and Map from formFieldPath to FieldFace values. The keys are held with week
     * references so this class will not prevent GC of FormModels.
     */
    private CachingMapDecorator cachedPropertyFaceDescriptors = new CachingMapDecorator(true) {
        public Object create(Object key) {
            return new HashMap();
        }
    };

    protected CachingFieldFaceSource() {
    }

    public FieldFace getFieldFace(final FormModel formModel, final String formFieldPath) {
        return getFieldFace(formModel, formFieldPath, new ObjectFactory() {
            public Object getObject() throws BeansException {
                return loadFieldFace(formModel, formFieldPath);
            }
        });
    }

    public FieldFace getFieldFace(final String field, final String contextId) {
        return getFieldFace(contextId, field, new ObjectFactory() {
            public Object getObject() throws BeansException {
                return loadFieldFace(field, contextId);
            }
        });
    }

    protected FieldFace getFieldFace(Object key, String field, ObjectFactory factory) {
        Map faceDescriptors = (Map) cachedPropertyFaceDescriptors.get(key == null ? DEFAULT_CONTEXT : key);
        FieldFace fieldFaceDescriptor = (FieldFace) faceDescriptors.get(field);
        if (fieldFaceDescriptor == null) {
            fieldFaceDescriptor = (FieldFace) factory.getObject();
            Assert.notNull(fieldFaceDescriptor, "FieldFace must not be null.");
            faceDescriptors.put(field, fieldFaceDescriptor);
        }
        return fieldFaceDescriptor;
    }

    public FieldFace getFieldFace(String field, String contextId, Map context) {
        if (context == null || context.isEmpty()) {
            return getFieldFace(field, contextId);
        }
        return loadFieldFace(field, contextId, context);
    }

    /**
     * Loads the FieldFace for the given field path and context id. This value will not be cached.
     * 
     * @param field
     *            the form field path
     * @param contextId
     *            optional context id for which the FieldFace is being resolved
     * @param context
     *            contains context parameters
     * @return the FieldFace for the given context id and the context parameters (never null).
     */
    protected abstract FieldFace loadFieldFace(String field, String contextId, Map context);

    /**
     * Loads the FieldFace for the given field path and context id. This value will be cached so performance need not be
     * a concern of this method.
     * 
     * @param field
     *            the form field path
     * @param contextId
     *            optional context id for which the FieldFace is being resolved
     * @return the FieldFace for the given context id (never null).
     */
    protected abstract FieldFace loadFieldFace(String field, String contextId);

    /**
     * Loads the FieldFace for the given form model and form property path. This value will be cached so performance
     * need not be a concern of this method.
     * 
     * @param formModel
     *            the form model for which the FieldFace is being resolved
     * @param formPropertyPath
     *            the form property path
     * @return the FieldFace for the given form model and form property path (never null).
     */
    protected abstract FieldFace loadFieldFace(FormModel formModel, String formPropertyPath);
}