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
package org.springframework.richclient.samples.petclinic.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.Visit;

/**
 * 
 * @author Keith Donald
 */
public class MockClinic implements Clinic {

    private static int sequenceNumber;

    private List owners = Collections.synchronizedList(new ArrayList());

    public MockClinic() {
        loadOwners();
    }

    private void loadOwners() {
        List owners = new ArrayList();
        addOwner("Keith", "Donald");
        addOwner("Keri", "Donald");
        addOwner("Ronald", "McDonald");
    }

    private List copyOwners() {
        List owners = new ArrayList();
        for (Iterator i = this.owners.iterator(); i.hasNext();) {
            Owner owner = (Owner)i.next();
            owners.add(copyOwner(owner));
        }
        return owners;
    }

    private Owner copyOwner(Owner owner) {
        Owner copy = new Owner();
        copy.setId(owner.getId());
        copy.setFirstName(owner.getFirstName());
        copy.setLastName(owner.getLastName());
        return copy;
    }

    private int addOwner(String firstName, String lastName) {
        Owner o = new Owner();
        o.setId(nextSequenceNumber());
        o.setFirstName(firstName);
        o.setLastName(lastName);
        this.owners.add(o);
        return o.getId();
    }

    private synchronized int nextSequenceNumber() {
        return sequenceNumber++;
    }

    public Collection getVets() throws DataAccessException {
        return Collections.EMPTY_LIST;
    }

    public Collection getPetTypes() throws DataAccessException {
        return Collections.EMPTY_LIST;
    }

    public Collection findOwners(String lastName) throws DataAccessException {
        return copyOwners();
    }

    public Owner loadOwner(int id) throws DataAccessException {
        Owner o = loadOwnerInternal(id);
        if (o != null) {
            return copyOwner(o);
        }
        else {
            return null;
        }
    }

    public Owner loadOwnerInternal(long id) {
        for (Iterator i = this.owners.iterator(); i.hasNext();) {
            Owner owner = (Owner)i.next();
            if (owner.getId() == id) { return owner; }
        }
        return null;

    }

    public Pet loadPet(int id) throws DataAccessException {
        return null;
    }

    public void storeOwner(Owner owner) throws DataAccessException {
        Owner o = loadOwnerInternal(owner.getId());
        if (o == null) {
            int id = addOwner(owner.getFirstName(), owner.getLastName());
            owner.setId(id);
        }
        else {
            o.setFirstName(owner.getFirstName());
            o.setLastName(owner.getLastName());
        }
    }

    public void storePet(Pet pet) throws DataAccessException {
    }

    public void storeVisit(Visit visit) {
    }

}