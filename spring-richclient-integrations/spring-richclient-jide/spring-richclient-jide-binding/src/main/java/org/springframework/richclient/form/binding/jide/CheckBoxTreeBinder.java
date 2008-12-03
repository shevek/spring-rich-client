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
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.util.Map;

/**
 * Binder for the JIDE check box tree
 * 
 * @author Jonny Wray
 *
 */
public class CheckBoxTreeBinder extends AbstractBinder {

    public static final String MODEL_KEY = "model";
    public static final String SELECTED_ITEM_HOLDER_KEY = "selectedItemHolder";
    public static final String RENDERER_KEY = "renderer";
    public static final String SELECTED_ITEM_TYPE_KEY = "selectedItemType";

    public CheckBoxTreeBinder() {
        super(null, new String[] {SELECTED_ITEM_HOLDER_KEY, SELECTED_ITEM_TYPE_KEY, MODEL_KEY,
                RENDERER_KEY, });
    }

    public CheckBoxTreeBinder(String[] supportedContextKeys) {
        super(null, supportedContextKeys);
    }
    
	protected JComponent createControl(Map arg0) {
		CheckBoxTree tree = new CheckBoxTree();
		return tree;
	}
	
	protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
		Assert.isTrue(control instanceof CheckBoxTree, formPropertyPath);
		CheckBoxTree list = (CheckBoxTree)control;
		CheckBoxTreeBinding binding = new CheckBoxTreeBinding(list,
				formModel, formPropertyPath);
		applyContext(binding, context);
		return binding;
	}
	
    protected void applyContext(CheckBoxTreeBinding binding, Map context) {
        if (context.containsKey(MODEL_KEY)) {
            binding.setModel((TreeModel)context.get(MODEL_KEY));
        }
        if (context.containsKey(SELECTED_ITEM_HOLDER_KEY)) {
            binding.setSelectedItemHolder((ValueModel)context.get(SELECTED_ITEM_HOLDER_KEY));
        }
        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((TreeCellRenderer)context.get(RENDERER_KEY)); 
        }
        if (context.containsKey(SELECTED_ITEM_TYPE_KEY)) {
            binding.setSelectedItemType((Class)context.get(SELECTED_ITEM_TYPE_KEY));
        }
    }
	
}
