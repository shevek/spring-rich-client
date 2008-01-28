/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.richclient.convert.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.Converter;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.core.ReflectiveVisitorHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This converter converts collection values from Object, Object[], Collection, List to Collection.class, List.class,
 * Object[].class types
 * 
 * @author Mathias Broekelmann
 * 
 */
public class CollectionConverter extends AbstractConverter implements Converter {

    private static final Class[] SOURCE_CLASSES = new Class[] { Object.class, Collection.class, List.class,
            Object[].class };

    private static final Class[] TARGET_CLASSES = new Class[] { Collection.class, List.class, Object[].class };

    private final ReflectiveVisitorHelper visitorHelper = new ReflectiveVisitorHelper();

    private Object visitor = new ValuesVisitor();

    protected Object doConvert(Object sourceValue, Class targetClass, ConversionContext context) throws Exception {
        List values = (List) visitorHelper.invokeVisit(visitor, sourceValue);
        if (Object[].class == targetClass) {
            return values.toArray();
        }
        return values;
    }

    public Class[] getSourceClasses() {
        return SOURCE_CLASSES;
    }

    public Class[] getTargetClasses() {
        return TARGET_CLASSES;
    }

    protected static class ValuesVisitor {
        List visitNull() {
            return Collections.EMPTY_LIST;
        }

        List visit(Object value) {
            return visit(new Object[] { value });
        }

        List visit(Collection values) {
            return new ArrayList(values);
        }

        List visit(List values) {
            return values;
        }

        List visit(Object[] values) {
            return Arrays.asList(values);
        }
    }

}
