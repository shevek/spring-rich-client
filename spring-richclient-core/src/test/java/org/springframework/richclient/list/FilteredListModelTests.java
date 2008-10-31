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
package org.springframework.richclient.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import junit.framework.TestCase;

import org.springframework.rules.constraint.Constraint;

/**
 * @author Mathias Broekelmann
 * 
 */
public class FilteredListModelTests extends TestCase {

    private TestListModel listModel;

    private TestConstraint filter;

    private Object[] elements = new Object[] { "1", "2", "3", "4" };

    protected void setUp() throws Exception {
        listModel = new TestListModel();
        for (int i = 0; i < elements.length; i++) {
            listModel.addElement(elements[i]);
        }
        filter = new TestConstraint();
    }

    protected void tearDown() throws Exception {
        listModel = null;
        filter = null;
        elements = null;
    }

    public void testFilterWithoutElements() throws Exception {
        ListModel filteredModel = new FilteredListModel(new DefaultListModel(), filter);
        assertEquals(0, filteredModel.getSize());
        assertEquals(1, filter.observeradded);
    }

    public void testNoFilterWithElements() throws Exception {
        filter.filter = false;
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(listModel.getSize(), filteredModel.getSize());
        assertEquals(listModel.getSize(), filter.testCalled);
    }

    public void testPassThroughFilterWithElements() throws Exception {
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(0, filteredModel.getSize());
    }

    public void testFilterWithElements1() throws Exception {
        filter.elements = Arrays.asList(new Object[] { "2", "4" });
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(filter.elements.size(), filteredModel.getSize());
    }

    public void testFilterWithElements2() throws Exception {
        filter.elements = Arrays.asList(new Object[] { "2", "4", "9999" });
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(2, filteredModel.getSize());
        assertEquals("2", filteredModel.getElementAt(0));
        assertEquals("4", filteredModel.getElementAt(1));
    }

    public void testRedefineFilter() throws Exception {
        filter.elements = Arrays.asList(new Object[] { "2", "4", "9999" });
        FilteredListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(2, filteredModel.getSize());
        assertEquals("2", filteredModel.getElementAt(0));
        assertEquals("4", filteredModel.getElementAt(1));
        TestConstraint newFilter = new TestConstraint();
        newFilter.filter = false;
        filteredModel.setConstraint(newFilter);
        assertEquals(listModel.getSize(), filteredModel.getSize());
    }

    public void testFilterWithUpdatingModel() throws Exception {
        filter.elements = Arrays.asList(new Object[] { "2", "4", "9999" });
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(2, filteredModel.getSize());
        filter.testCalled = 0;
        listModel.addElement("1234");
        assertEquals(listModel.getSize(), filter.testCalled);
        assertEquals("2", filteredModel.getElementAt(0));
        assertEquals("4", filteredModel.getElementAt(1));
        listModel.addElement("9999");
        assertEquals(3, filteredModel.getSize());
        assertEquals("2", filteredModel.getElementAt(0));
        assertEquals("4", filteredModel.getElementAt(1));
        assertEquals("9999", filteredModel.getElementAt(2));
        listModel.removeElement("2");
        assertEquals(2, filteredModel.getSize());
        assertEquals("4", filteredModel.getElementAt(0));
        assertEquals("9999", filteredModel.getElementAt(1));
    }

    public void testObserver() throws Exception {
        filter.elements = Arrays.asList(new Object[] { "2", "4" });
        ListModel filteredModel = new FilteredListModel(listModel, filter);
        assertEquals(2, filteredModel.getSize());
        filter.testCalled = 0;
        filter.elements = Arrays.asList(new Object[] { "1" });
        filter.changed();
        filter.notifyObservers();
        assertEquals(listModel.getSize(), filter.testCalled);
        assertEquals(filter.elements.size(), filteredModel.getSize());
    }

    private static class TestConstraint extends Observable implements Constraint {

        boolean filter = true;

        int testCalled = 0;

        Collection elements = Collections.EMPTY_LIST;

        int observeradded = 0;

        public boolean test(Object argument) {
            testCalled++;
            if (filter) {
                return elements.contains(argument);
            }
            return true;
        }

        void changed() {
            setChanged();
        }

        public synchronized void addObserver(Observer o) {
            observeradded++;
            super.addObserver(o);
        }
    }

    private static class TestListModel extends DefaultListModel {
    }
}
