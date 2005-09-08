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
package org.springframework.binding.value.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.binding.value.ValueModel;

/**
 * Implementation of a BufferedCollectionValueModel that performs a deep copy on the
 * elements of the collection.
 * 
 * @author Larry Streepy
 * 
 */
public class DeepCopyBufferedCollectionValueModel extends BufferedCollectionValueModel {

    /**
     * Constructs a new DeepCopyBufferedCollectionValueModel.
     * 
     * @param wrappedModel the value model to wrap
     * @param wrappedType the class of the value contained by wrappedModel; this must be
     *            assignable to <code>java.util.Collection</code> or
     *            <code>Object[]</code>.
     */
    public DeepCopyBufferedCollectionValueModel(ValueModel wrappedModel, Class wrappedType) {
        super( wrappedModel, wrappedType );
    }

    /**
     * Prepare the backing collection for installation into the listListModel. Create a
     * new collection that contains a deep copy of the elements in the given collection.
     * 
     * @param col The collection of objects to process
     * @return processed collection
     */
    protected Collection prepareBackingCollection(Collection col) {
        ArrayList list = new ArrayList(col);
        for( int i=0; i < list.size(); i++ ) {
            list.set(i, deepCopy(list.get(i)));
        }
        return list;
    }

    /**
     * Create a new object that is a deep copy of the given object. This copy is created
     * using serialization, so the object to be copied must implement Serializable. If it
     * does not, then the original object will be returned. Any other error results in
     * null being returned.
     * 
     * @param value
     * @return deep copy
     */
    protected Object deepCopy(Object value) {
        try {
            // Write to new byte array to clone.
            ByteArrayOutputStream baos = new ByteArrayOutputStream( 1024 );
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            try {
                oos.writeObject( value );
            } catch( NotSerializableException e ) {
                return value;
            } finally {
                oos.close();
            }

            // Read it back and return a true copy.
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            ObjectInputStream ois = new ObjectInputStream( bais );
            try {
                return ois.readObject();
            } finally {
                ois.close();
            }
        } catch( ClassNotFoundException ex ) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        }
        return null;
    }
}
