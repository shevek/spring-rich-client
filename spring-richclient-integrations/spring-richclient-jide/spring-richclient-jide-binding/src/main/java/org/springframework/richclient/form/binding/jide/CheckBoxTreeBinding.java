/*
 * Copyright 2005 the original author or authors.
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
package org.springframework.richclient.form.binding.jide;

import com.jidesoft.swing.CheckBoxTree;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedCollectionValueModel;
import org.springframework.richclient.form.binding.support.AbstractBinding;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Binding for the JIDE check box tree using the ListBinding
 * as a guide.
 * 
 * @author Jonny Wray
 *
 */
public class CheckBoxTreeBinding extends AbstractBinding {

    private final SelectedItemChangeHandler selectedItemChangeHandler = new SelectedItemChangeHandler();
	private CheckBoxTree tree;
    private TreeModel model;
    private ValueModel selectedItemHolder;
    private TreeCellRenderer renderer;
    private Class selectedItemType;
    private Class concreteSelectedType;
	
    public CheckBoxTreeBinding(CheckBoxTree tree, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.tree = tree;
    }

    public void setModel(TreeModel model) {
        this.model = model;
    }

    public void setRenderer(TreeCellRenderer renderer) {
        this.renderer = renderer;
    }

    public void setSelectedItemHolder(ValueModel selectedItemHolder) {
        this.selectedItemHolder = selectedItemHolder;
    }

    public void setSelectedItemType(final Class selectedItemType) {
        this.selectedItemType = selectedItemType;
    }

    protected Class getSelectedItemType() {
        if (this.selectedItemType == null) {
            if (this.selectedItemHolder != null && this.selectedItemHolder.getValue() != null) {
                setSelectedItemType(this.selectedItemHolder.getValue().getClass());
            }
        }

        return this.selectedItemType;
    }
    
    protected boolean isSelectedItemMultiValued() {
        return isSelectedItemACollection() || isSelectedItemAnArray();
    }
    
    protected boolean isSelectedItemAnArray() {
        Class itemType = getSelectedItemType();
        return itemType != null && itemType.isArray();
    }

    protected boolean isSelectedItemACollection() {
        return getSelectedItemType() != null && Collection.class.isAssignableFrom(getSelectedItemType());
    }

    protected boolean isTrulyMultipleSelect() {
        //return tree.getM() != TreeSelectionModel.SINGLE_TREE_SELECTION && isSelectedItemMultiValued();
    	return isSelectedItemMultiValued();
    }

    protected Class getConcreteSelectedType() {
        if (concreteSelectedType == null) {
            if (isSelectedItemACollection()) {
                concreteSelectedType = BufferedCollectionValueModel.getConcreteCollectionType(getSelectedItemType());
            }
            else if (isSelectedItemAnArray()) {
                concreteSelectedType = getSelectedItemType().getComponentType();
            }
        }
        return concreteSelectedType;
    }
    
	protected JComponent doBindControl() {
		tree.setModel(model);
		tree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(selectedItemChangeHandler);
        if (renderer != null) {
            tree.setCellRenderer(renderer);
        }
        tree.getCheckBoxTreeSelectionModel().setDigIn(true);
        return tree;
	}

	protected void enabledChanged() {
		 tree.setEnabled(isEnabled() && !isReadOnly());
	}

	protected void readOnlyChanged() {
		 tree.setEnabled(isEnabled() && !isReadOnly());
	}
	
	/**
	 * A listener that populates the selected item holder with
	 * a list containing all the underlying user objects.
	 * 
	 * @author Jonny Wray
	 *
	 */
    private class SelectedItemChangeHandler implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
    		List  selectedSet = new ArrayList();
        	TreePath[] selected = tree.getCheckBoxTreeSelectionModel().getSelectionPaths();
        	if(selected != null){
        		for(int j=0;j<selected.length;j++){
        			TreePath path = selected[j];
        			if(path.getLastPathComponent().equals(model.getRoot())){
        				int children = model.getChildCount(model.getRoot());
        				for(int i=0;i<children;i++){
        					Object userObject = ((DefaultMutableTreeNode)model.getChild(model.getRoot(), i)).getUserObject();
        					selectedSet.add(userObject);
        				}
        			}
        			else{
	        			Object userObject = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
	        			selectedSet.add(userObject);
        			}
        		}
        	}
			selectedItemHolder.setValue(selectedSet);
		}
    }
}
