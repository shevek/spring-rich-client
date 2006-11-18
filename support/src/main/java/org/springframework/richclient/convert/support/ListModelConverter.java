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
package org.springframework.richclient.convert.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.core.ReflectiveVisitorHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Mathias Broekelmann
 * 
 */
public class ListModelConverter extends AbstractConverter {

    private static final Class[] TARGET_CLASSES = new Class[] { ListModel.class };

    private static final Class[] SOURCE_CLASSES = new Class[] { Collection.class, List.class, Object[].class,
            ListModel.class, Object.class };

    private final ReflectiveVisitorHelper visitorHelper = new ReflectiveVisitorHelper();

    protected Object doConvert(Object sourceValue, Class targetClass, ConversionContext context) throws Exception {
        return visitorHelper.invokeVisit(this, sourceValue);
    }

    public Class[] getSourceClasses() {
        return SOURCE_CLASSES;
    }

    public Class[] getTargetClasses() {
        return TARGET_CLASSES;
    }

    ListModel visit(ListModel listModel) {
        return listModel;
    }

    ListModel visit(List list) {
        return new ListListModel(list);
    }

    ListModel visit(Collection collection) {
        return visit(new ArrayList(collection));
    }

    ListModel visit(Object[] array) {
        return visit(Arrays.asList(array));
    }

    ListModel visit(Object object) {
        if(object instanceof Object[]) {
            return visit((Object[])object);
        }
        return visit(new Object[] { object });
    }
}
