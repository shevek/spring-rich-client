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
package org.springframework.richclient.form.binding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.swing.CheckBoxBinder;
import org.springframework.richclient.form.binding.swing.ComboBoxBinder;
import org.springframework.richclient.form.binding.swing.EnumComboBoxBinder;
import org.springframework.richclient.form.binding.swing.TextComponentBinder;

/**
 * @author Oliver Hutchison
 */
public class DefaultBinderSelectionStrategy implements BinderSelectionStrategy {

    private Map binders = new HashMap();

    public DefaultBinderSelectionStrategy() {
        initDefaultBinders();
    }

    public Binder selectBinder(FormModel formModel, String formPropertyPath) {
        return doDefaultSelectBinder(formModel, formPropertyPath);
    }

    public Binder selectBinder(Class controlType, FormModel formModel, String propertyName) {
        Binder binder = findBinderByControlType(controlType);
        return binder;
    }
    
    protected Binder doDefaultSelectBinder(FormModel formModel, String formPropertyPath) {
        if (isEnumeration(formModel, formPropertyPath)) {
            // TODO: How should this special case be handled?
            return new EnumComboBoxBinder(); 
        }
        else if (isBoolean(formModel, formPropertyPath)) {
            return selectBinder(JCheckBox.class, formModel, formPropertyPath);
        }
        else {
            return selectBinder(JTextComponent.class, formModel, formPropertyPath);
        }
    }

    /**
     * Try to find a binder for the given control type. If no direct match found,
     * try to find binder for closest superclass possible.
     */
    private Binder findBinderByControlType(Class controlType) {
        Binder binder = (Binder)binders.get(controlType);
        if (binder == null) {
            Class superclassType = null;
            for (Iterator it = binders.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                if (key instanceof Class && ((Class)key).isAssignableFrom(controlType)
                        && (superclassType == null || superclassType.isAssignableFrom((Class)key))) {
                    superclassType = (Class)key;
                    binder = (Binder)binders.get(key);
                }
            }
        }
        return binder;
    }

    protected void initDefaultBinders() {
        binders.put(JTextComponent.class, new TextComponentBinder());
        binders.put(JComboBox.class, new ComboBoxBinder());
        binders.put(JCheckBox.class, new CheckBoxBinder());
    }

    protected boolean isBoolean(FormModel formModel, String formPropertyPath) {
        Class aspectClass = getPropertyMetadataAccessStrategy(formModel).getPropertyType(formPropertyPath);
        return aspectClass.equals(Boolean.class) || aspectClass.equals(Boolean.TYPE);
    }

    protected boolean isEnumeration(FormModel formModel, String formPropertyPath) {
        return getPropertyMetadataAccessStrategy(formModel).isEnumeration(formPropertyPath);
    }

    private PropertyMetadataAccessStrategy getPropertyMetadataAccessStrategy(FormModel formModel) {
        return ((ConfigurableFormModel)formModel).getMetadataAccessStrategy();
    }
}