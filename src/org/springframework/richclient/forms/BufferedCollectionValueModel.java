/*
 * Copyright 2002-2004 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.springframework.richclient.forms;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.beans.BeanUtils;
import org.springframework.richclient.list.ListListModel;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.ValueModel;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A BufferedValueModel that 
 *  
 * @author  oliverh
 */
public class BufferedCollectionValueModel extends BufferedValueModel {
    private ListListModel listListModel;

    private Class wrappedType;

    private Class class2Create;

    private boolean updating;

    public BufferedCollectionValueModel(ValueModel wrappedModel,
            Class wrappedType) {
        super(wrappedModel);
        Assert.notNull(wrappedType);
        this.wrappedType = wrappedType;
        this.class2Create = getConcreteClassForWrappedType(wrappedType);
        updateListListModelFromBackingObject();
    }

    public Object get() {
        if (!isChangeBuffered()) {
            super.set(listListModel);
        }
        return super.get();
    }

    protected void doBufferedValueCommit(Object bufferedValue) {
        if (listListModelHasSameStructureAsBackingCollection()) { return; }
        Object newCollection = createOrRepopulateBackingCollection();
        getWrappedModel().set(newCollection);
        if (listListModelHasSameStructureAsBackingCollection()) { return; }
        super.set(updateListListModelFromBackingObject());
    }

    protected Class getConcreteClassForWrappedType(Class wrappedType) {
        Class class2Create;
        if (wrappedType.isArray()) {
            if (BeanUtils.isPrimitiveArray(wrappedType)) { throw new IllegalArgumentException(
                    "wrappedType can not be an array of primitive types"); }
            class2Create = wrappedType;
        }
        else if (wrappedType == Collection.class) {
            class2Create = ArrayList.class;
        }
        else if (wrappedType == List.class) {
            class2Create = ArrayList.class;
        }
        else if (wrappedType == Set.class) {
            class2Create = HashSet.class;
        }
        else if (wrappedType == SortedSet.class) {
            class2Create = TreeSet.class;
        }
        else if (Collection.class.isAssignableFrom(wrappedType)) {
            if (wrappedType.isInterface()) { throw new IllegalArgumentException(
                    "unable to handle Collection of type ["
                            + wrappedType
                            + "]. Do not know how to create a concrete implementation"); }
            class2Create = wrappedType;
        }
        else {
            throw new IllegalArgumentException("wrappedType [" + wrappedType
                    + "] must be an array or a Collection");
        }
        return class2Create;
    }

    /*
     * Checks if the structre of the ListListModel is the same as the wrapped
     * collecton. "same stucture" is defined as having the same elements in the
     * same order with the one exception that NULL == empty list.
     */
    private boolean listListModelHasSameStructureAsBackingCollection() {
        Object wrappedObject = super.getWrappedModel().get();
        if (wrappedObject == null) {
            return listListModel.size() == 0;
        }
        else if (wrappedObject instanceof Object[]) {
            Object[] wrappedArray = (Object[])wrappedObject;
            if (wrappedArray.length != listListModel.size()) { return false; }
            for (int i = 0; i < listListModel.size(); i++) {
                if (!ObjectUtils.nullSafeEquals(wrappedArray[i], listListModel
                        .get(i))) { return false; }
            }
        }
        else {
            if (((Collection)wrappedObject).size() != listListModel.size()) { return false; }
            for (Iterator i = ((Collection)wrappedObject).iterator(), j = listListModel
                    .iterator(); i.hasNext();) {
                if (!ObjectUtils.nullSafeEquals(i.next(), j.next())) { return false; }
            }
        }
        return true;
    }

    /*
     * When posible this method will try to repopulate the backing collection
     * with the objects that are contained in the ListListModel.
     */
    protected Object createOrRepopulateBackingCollection() {
        Object wrappedObject = super.getWrappedModel().get();

        // first handle the special NULL case
        if (listListModel.size() == 0 && wrappedObject == null) { return null; }

        // if wrappedObject is an array of different length to the ListListModel
        // or one of the unmodifiable Collection implementations we need to
        // create a new backing object.
        // TODO: do we need to support Collections.UnmodifiableCollection
        // classes?
        if (wrappedObject == null
                || (wrappedObject instanceof Object[] && ((Object[])wrappedObject).length != listListModel
                        .size()) || wrappedObject == Collections.EMPTY_LIST
                || wrappedObject == Collections.EMPTY_SET) {
            wrappedObject = createNewCollection();
        }

        return populateFromListListModel(wrappedObject);
    }

    protected Object populateFromListListModel(Object wrappedObject) {
        if (wrappedObject instanceof Object[]) {
            Object[] wrappedArray = (Object[])wrappedObject;
            for (int i = 0; i < listListModel.size(); i++) {
                wrappedArray[i] = listListModel.get(i);
            }
        }
        else {
            ((Collection)wrappedObject).clear();
            ((Collection)wrappedObject).addAll(listListModel);
        }
        return wrappedObject;
    }

    protected Object createNewCollection() {
        if (class2Create.isArray()) {
            return Array.newInstance(class2Create.getComponentType(),
                    listListModel.size());
        }
        else {
            return BeanUtils.instantiateClass(class2Create);
        }
    }

    /**
     * Gets the list value associated with this value model, creating a list
     * model buffer containing its contents, suitable for manipulation.
     * 
     * @return The list model buffer
     */
    protected Object updateListListModelFromBackingObject() {
        if (listListModel == null) {
            listListModel = new ListListModel();
            listListModel.addListDataListener(new ListDataListener() {
                public void contentsChanged(ListDataEvent e) {
                    fireValueChanged();
                }

                public void intervalAdded(ListDataEvent e) {
                    fireValueChanged();
                }

                public void intervalRemoved(ListDataEvent e) {
                    fireValueChanged();
                }
            });
        }
        else {
            try {
                updating = true;
                listListModel.clear();
            }
            finally {
                updating = false;
            }
        }
        Object backingCollection = getWrappedModel().get();
        if (backingCollection != null) {
            try {
                updating = true;
                if (wrappedType.isAssignableFrom(backingCollection.getClass())) {
                    if (backingCollection instanceof Object[]) {
                        Object[] backingArray = (Object[])backingCollection;
                        for (int i = 0; i < backingArray.length; i++) {
                            listListModel.add(backingArray[i]);
                        }
                    }
                    else {
                        listListModel.addAll((Collection)backingCollection);
                    }
                }
                else {
                    throw new IllegalArgumentException(
                            "backingCollection must be an instance of "
                                    + wrappedType.getName());
                }
            }
            finally {
                updating = false;
            }
        }
        return listListModel;
    }

    protected void onWrappedValueChanged() {
        if (!updating) {
            updateListListModelFromBackingObject();
        }
    }

    protected void fireValueChanged() {
        if (!updating) {
            if (isChangeBuffered()) {
                super.fireValueChanged();
            }
            else {
                super.set(listListModel);
            }
        }
    }
}