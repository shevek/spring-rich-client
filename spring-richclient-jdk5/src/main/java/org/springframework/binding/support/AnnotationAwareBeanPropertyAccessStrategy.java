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
 */

package org.springframework.binding.support;

import java.util.Map;
import java.util.LinkedHashMap;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.util.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * A JavaBean {@link org.springframework.binding.PropertyAccessStrategy
 * PropertyAccessStrategy} that exposes JDK 1.5 Annotations for each property
 * in the form of property level user metadata.  In order to remain backward
 * compatible with 1.4 code, the annotations will be parsed by an installed
 * {@link AnnotationTranslator} which typically stores the content of each
 * annotation as key + value pairs in the user metadata.  If no translator is
 * explicitly set, then this implementation will use
 * {@link ReflectionAnnotationTranslator} by default.
 * <p/>
 * Note that this implementation will scan for annotations on both the getter
 * and setter methods of the property.  If both getter and setter happen to
 * define the same annotation, then the annotation on the getter will
 * override the annotation on the setter.
 *
 * @author andy
 * @since May 8, 2006 1:57:46 PM
 */
public class AnnotationAwareBeanPropertyAccessStrategy extends BeanPropertyAccessStrategy {
    private AnnotationTranslator annotationTranslator;

    public AnnotationAwareBeanPropertyAccessStrategy(Object bean) {
        super(bean);
    }

    public AnnotationAwareBeanPropertyAccessStrategy(final ValueModel domainObjectHolder) {
        super(domainObjectHolder);
    }

    protected AnnotationAwareBeanPropertyAccessStrategy(final BeanPropertyAccessStrategy parent, final String basePropertyPath) {
        super(parent, basePropertyPath);
    }



    //
    // METHODS FROM BASE CLASS BeanPropertyAccessStrategy
    //



    /**
     * Retrieves annotations for the specified property as user metadata.
     * Uses the intalled {@link #getAnnotationTranslator annotation translator}
     * to "parse" annotations to their key + value pair components.
     * <p/>
     * Note that currently annotations are retranslated and the returned map
     * is rebuilt on every invocation of this method.  No caching is attempted
     * because the current use case of this method is a single call for each
     * property during FormModel construction.  If this use case or usage
     * pattern should be changed, then we may want to consider caching the
     * result of this method.
     *
     * @param propertyPath
     */
    @Override
    protected Map<String,Object> getAllUserMetadataFor(final String propertyPath) {
        final PropertyDescriptor pd = getBeanWrapper().getPropertyDescriptor(propertyPath);
        Assert.notNull(pd);
        
        final AnnotationTranslator translator = getAnnotationTranslator();
        final Map<String,Object> ret = new LinkedHashMap<String,Object>();

        for(final Annotation annotation : AnnotationUtils.getAnnotationsFor(pd)) {
            translator.translate(annotation, ret);
        }
        
        return ret;
    }







    //
    // PROPERTY ACCESSORS
    //

    public AnnotationTranslator getAnnotationTranslator() {
        if(this.annotationTranslator == null) {
            this.annotationTranslator = new ReflectionAnnotationTranslator();
        }
        return annotationTranslator;
    }

    public void setAnnotationTranslator(final AnnotationTranslator annotationTranslator) {
        this.annotationTranslator = annotationTranslator;
    }
}
