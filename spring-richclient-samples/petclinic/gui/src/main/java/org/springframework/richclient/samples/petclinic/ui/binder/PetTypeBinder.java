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
package org.springframework.richclient.samples.petclinic.ui.binder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.TypeConverter;
import org.springframework.core.closure.Closure;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.PetType;
import org.springframework.util.Assert;

public class PetTypeBinder extends AbstractBinder implements InitializingBean {

    private Clinic clinic;

    private Map petTypes;

    public PetTypeBinder() {
        super(PetType.class, new String[] {});        
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(clinic, "Clinic must be provided.");
        petTypes = new LinkedHashMap();
        for (Iterator i = clinic.getPetTypes().iterator(); i.hasNext();) {
            PetType petType = (PetType)i.next();
            petTypes.put(petType.getName(), petType);
        }
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createComboBox();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JComboBox, formPropertyPath);
        ComboBoxBinding binding = new ComboBoxBinding((JComboBox)control, formModel, formPropertyPath) {
            protected ValueModel getValueModel() {
                return new PetTypeAdapter(super.getValueModel(), petTypes);
            }
        };
        binding.setSelectableItems(petTypes.keySet());
        return binding;
    }

    // This is a hack to get the combo box working even though
    // PetType does not implement equals/hashCode.
    private class PetTypeAdapter extends TypeConverter {
        private PetTypeAdapter(ValueModel valueModel, final Map petTypes) {
            super(valueModel, new Closure() {
                public Object call(Object petType) {                    
                    return petType != null ? ((PetType)petType).getName() : "";
                }
            }, new Closure() {
                public Object call(Object petTypeName) {
                    return petTypes.get(petTypeName);
                }
            });            
        }
    }
}