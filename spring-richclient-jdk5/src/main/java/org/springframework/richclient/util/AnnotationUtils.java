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

package org.springframework.richclient.util;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.beans.PropertyDescriptor;

/**
 * General utility methods for working with annotations.
 *
 * @author andy
 * @since May 8, 2006 4:57:20 PM
 */
public class AnnotationUtils {

    /**
     * Retrieves all annotations present on the specified property.  This
     * method will gather the annotations present on both the getter and the
     * setter of the property.  If a particular Annotation type appears on
     * both the getter and setter, then the instance appearing on the Getter
     * will override (replace) the instance appearing on the Setter.
     *
     * @param property
     * @return collection containing all annotations present on the specified
     *         property.  This method will never return <code>null</code>,
     *         but will return an empty Collection if no annotations appear
     *         on the property.
     */
    public static Collection<Annotation> getAnnotationsFor(final PropertyDescriptor property) {
        // The Map is used to satisfy the contract of this method: that
        // annotations on the getter replace annotations of the same type on
        // the setter.  The equals and hashcode implementation on annotations
        // prevent us from using a HashSet, as we want to compare based on
        // type, not on type and content.
        // LinkedHashMap is used to return the annotations in the same order
        // they are declared.
        final Map<Class<?>,Annotation> collector = new LinkedHashMap<Class<?>,Annotation>();
        
        if(property.getWriteMethod() != null) {
            for(final Annotation annotation : property.getWriteMethod().getAnnotations()) {
                collector.put(annotation.annotationType(), annotation);
            }
        }
        if(property.getReadMethod() != null) {
            for(final Annotation annotation : property.getReadMethod().getAnnotations()) {
                collector.put(annotation.annotationType(), annotation);
            }
        }
        
        return collector.values();
    }
}
