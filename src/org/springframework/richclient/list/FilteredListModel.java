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
package org.springframework.richclient.list;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueChangePublisher;
import org.springframework.util.closure.Constraint;

/**
 * @author Keith Donald
 */
public class FilteredListModel extends AbstractFilteredListModel
        implements Observer, ValueChangeListener {

    private Constraint constraint;

    private int[] indexes;

    private int filteredSize;

    public FilteredListModel(ListModel listModel,
            Constraint constraint) {
        super(listModel);
        this.constraint = constraint;
        if (this.constraint instanceof Observable) {
            ((Observable)this.constraint).addObserver(this);
        }
        else if (this.constraint instanceof ValueChangePublisher) {
            ((ValueChangePublisher)this.constraint).addValueChangeListener(this);
        }
        reallocateIndexes();
    }
    
    protected void reallocateIndexes() {
        this.indexes = new int[getFilteredModel().getSize()];
        applyConstraint();
    }

    public void valueChanged() {
        update(null, null);
    }

    public void update(Observable changed, Object arg) {
        applyConstraint();
        fireContentsChanged(this, -1, -1);
    }

    private void applyConstraint() {
        filteredSize = 0;
        ListModel filteredListModel = getFilteredModel();
        for (int i = 0, j = 0; i < filteredListModel.getSize(); i++) {
            Object element = filteredListModel.getElementAt(i);
            if (constraint.test(element)) {
                indexes[j] = i;
                j++;
                filteredSize++;
                onMatchingElement(element);
            }
        }
        postConstraintApplied();
    }

    protected void onMatchingElement(Object element) {

    }

    protected void postConstraintApplied() {

    }

    public int getSize() {
        return filteredSize;
    }

    public Object getElementAt(int index) {
        return getFilteredModel().getElementAt(indexes[index]);
    }

    public void contentsChanged(ListDataEvent e) {
        super.contentsChanged(e);
    }

    public void intervalAdded(ListDataEvent e) {
        reallocateIndexes();
        super.intervalAdded(e);
    }

    public void intervalRemoved(ListDataEvent e) {
        reallocateIndexes();
        super.intervalRemoved(e);
    }

}