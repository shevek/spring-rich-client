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
package org.springframework.richclient.forms;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.richclient.list.ListListModel;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.ValueModel;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A <code>BufferedValueModel</code> that wraps a value model containing a
 * <code>Collection</code> or <code>array</code> with a
 * <code>ListListModel</code>. The list model acts as a buffer for changes to
 * and a representation of the state of the underlying collection.
 * <p>
 * On commit the folowing steps occur: <br>
 * 1) a new instance of the backing collection type is created <br>
 * 2) the contents of the list model is inserted into this new collection <br>
 * 3) the new collecton is saved into the underlying collection's value model
 * <br>
 * 4) the structure of the list model is compared to the structure of the <br>
 * new underlying collection and if they differ the list model is updated to
 * reflect the new structure.
 * </p>
 * <p>
 * NOTE: Between calls to commit the list model adhers to the contract defined
 * in <code>java.util.List</code> NOT the contract of the underlying
 * collection's type. This can result in the list model representing a state
 * that is not possible for the underlying collection.  
 * </p>
 * 
 * @author oliverh
 */
public class BufferedCollectionValueModel extends BufferedValueModel {
    private ListListModel listListModel;

    private Class wrappedType;

    private Class wrappedConcreteType;

    private boolean updating;

    /**
     * Constructs a new BufferedCollectionValueModel.
     * 
     * @param wrappedModel
     *            the value model to wrap
     * @param wrappedType
     *            the class of the value contained by wrappedModel; this must be
     *            assignable to <code>java.util.Collection</code> or
     *            <code>Object[]</code>.
     */
    public BufferedCollectionValueModel(ValueModel wrappedModel,
            Class wrappedType) {
        super(wrappedModel);
        Assert.notNull(wrappedType);
        this.wrappedType = wrappedType;
        this.wrappedConcreteType = getConcreteClassForWrappedType(wrappedType);
        updateListListModelFromWrappedCollection();
    }

    public Object get() {
        if (!isChangeBuffered()) {
            super.set(listListModel);
        }
        return super.get();
    }

    protected void doBufferedValueCommit(Object bufferedValue) {
        if (listListModelHasSameStructureAsWrappedCollection()) { return; }
        Object newCollection = createNewWrappedCollection();
        getWrappedModel().set(newCollection);
        if (listListModelHasSameStructureAsWrappedCollection()) { return; }
        super.set(updateListListModelFromWrappedCollection());
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
     * Checks if the structure of the ListListModel is the same as the wrapped
     * collecton. "same stucture" is defined as having the same elements in the
     * same order with the one exception that NULL == empty list.
     */
    private boolean listListModelHasSameStructureAsWrappedCollection() {
        Object wrappedObject = getWrappedValue();
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

    private Object createNewWrappedCollection() {
        Object wrappedObject = getWrappedValue();
        return populateFromListListModel(createNewCollection(wrappedObject));
    }

    private Object populateFromListListModel(Object wrappedObject) {
        if (wrappedObject instanceof Object[]) {
            Object[] wrappedArray = (Object[])wrappedObject;
            for (int i = 0; i < listListModel.size(); i++) {
                wrappedArray[i] = listListModel.get(i);
            }
        }
        else {
            Collection wrappedVollection = ((Collection)wrappedObject);
            wrappedVollection.clear();
            wrappedVollection.addAll(listListModel);
        }
        return wrappedObject;
    }

    private Object createNewCollection(Object wrappedObject) {
        if (wrappedConcreteType.isArray()) {
            return Array.newInstance(wrappedConcreteType.getComponentType(),
                    listListModel.size());
        }
        else {
            Object newCollection;
            if (SortedSet.class.isAssignableFrom(wrappedConcreteType)
                    && wrappedObject instanceof SortedSet
                    && ((SortedSet)wrappedObject).comparator() != null) {
                try {
                    Constructor con = wrappedConcreteType
                            .getConstructor(new Class[] { Comparator.class });
                    newCollection = BeanUtils.instantiateClass(con,
                            new Object[] { ((SortedSet)wrappedObject)
                                    .comparator() });
                }
                catch (NoSuchMethodException e) {
                    throw new FatalBeanException(
                            "Could not instantiate SortedSet class ["
                                    + wrappedConcreteType.getName()
                                    + "]: no constructor taking Comparator found",
                            e);
                }
            }
            else {
                newCollection = BeanUtils.instantiateClass(wrappedConcreteType);
            }
            return newCollection;
        }
    }

    /**
     * Gets the list value associated with this value model, creating a list
     * model buffer containing its contents, suitable for manipulation.
     * 
     * @return The list model buffer
     */
    private Object updateListListModelFromWrappedCollection() {
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
        Object wrappedCollection = getWrappedValue();
        if (wrappedCollection != null) {
            try {
                updating = true;
                if (wrappedType.isAssignableFrom(wrappedCollection.getClass())) {
                    if (wrappedCollection instanceof Object[]) {
                        Object[] wrappedArray = (Object[])wrappedCollection;
                        for (int i = 0; i < wrappedArray.length; i++) {
                            listListModel.add(wrappedArray[i]);
                        }
                    }
                    else {
                        listListModel.addAll((Collection)wrappedCollection);
                    }
                }
                else {
                    throw new IllegalArgumentException(
                            "wrappedCollection must be an instance of "
                                    + wrappedType.getName());
                }
            }
            finally {
                updating = false;
            }
        }
        return listListModel;
    }

    private Object getWrappedValue() {
        return super.getWrappedModel().get();
    }

    protected void onWrappedValueChanged() {
        if (!updating) {
            updateListListModelFromWrappedCollection();
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