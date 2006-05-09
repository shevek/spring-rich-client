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

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Provides a strategy for converting JDK 1.5 Annotations on JavaBean
 * properties into 1.4 compatible property level user metadata.
 *
 * @author andy
 * @since May 8, 2006 2:08:05 PM
 */
public interface AnnotationTranslator {
    /**
     * Translates <code>annotation</code> into its constituent (or 
     * equivalent) key + value pairs and places the resulting pairs into
     * the <code>result</code> map.
     *
     * @param annotation the annotation to be translated
     * @param result container to hold the result of this translation
     */
    void translate(Annotation annotation, Map<String,Object> result);
}
