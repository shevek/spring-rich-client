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
package org.springframework.binding.form.support;

import junit.framework.TestCase;

import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.support.TestPropertyChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.binding.value.support.ValueModelWrapper;

public abstract class AbstractFormModelCornerCaseTests extends TestCase {

    protected AbstractFormModel getFormModel(BeanPropertyAccessStrategy pas, boolean buffering) {
        return new TestAbstractFormModel(pas, buffering);
    }

    protected AbstractFormModel getFormModel(ValueModel formObjectHolder, boolean buffering) {
        return new TestAbstractFormModel(formObjectHolder, buffering);
    }

    /**
     * For a given hierarchy of form models or each possible property there should 
     * only be one (Buffered)ValueModel that is shared between all members.
     */
    public void testTwoFormModelsInHierarchyShareSameFormObjectHolderBuffered() {
        // XXX: fails
//        testTwoFormModelsInHierarchyShareSameFormObjectHolder(true);
    }
    
    public void testTwoFormModelsInHierarchyShareSameFormObjectHolderUnbuffered() {
        // XXX: fails
//        testTwoFormModelsInHierarchyShareSameFormObjectHolder(false);
    }
    
    public void testTwoFormModelsInHierarchyShareSameFormObjectHolder(boolean buffered) {
        ValueHolder vm = new ValueHolder(new TestBean());
        AbstractFormModel fm1 = getFormModel(vm, buffered);
        AbstractFormModel fm2 = getFormModel(vm, buffered);
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(ValueModel.VALUE_PROPERTY);
        fm2.getValueModel("simpleProperty").addValueChangeListener(pcl);
        fm1.addChild(fm2);

        fm1.getValueModel("simpleProperty").setValue("1");

        assertEquals("update to simpleProperty in fm1 should have propagated to fm2", "1", fm2.getValueModel(
                "simpleProperty").getValue());
        assertEquals(1, pcl.eventCount());
        assertEquals(((ValueModelWrapper)fm1.getValueModel("simpleProperty")).getInnerMostWrappedValueModel(),
                ((ValueModelWrapper)fm2.getValueModel("simpleProperty")).getInnerMostWrappedValueModel());
    }

    /** 
     * When a form model commits it should make sure that it commits buffered values
     * in breadth first order from the root value model down the property/subproperty 
     * chain. If there's a hierarchy this ordering would need to be done across for all 
     * buffered value model in all form models in one go not just on a form model by form
     * model basis.
     * If this is not done then changes committed to child properties will be overwritten 
     * by changes to parent properties that commit after the child.
     */
    public void testBufferingMustCommitParentPropertiesBeforeChildProperties() {
        // XXX: fails
//        TestBean t = new TestBean();
//        t.setNestedProperty(new TestBean());
//        ValueHolder vm = new ValueHolder(t);
//        AbstractFormModel fm = getFormModel(vm, true);
//
//        TestBean t2 = new TestBean();
//        t2.setSimpleProperty("*");
//        fm.getValueModel("nestedProperty.simpleProperty").setValue("1");
//        fm.getValueModel("nestedProperty").setValue(t2);
//        fm.getValueModel("nestedProperty.simpleProperty").setValue("2");
//
//        fm.commit();
//
//        assertEquals("change to nestedProperty was not committed before change to nestedProperty.simpleProperty", "2",
//                t.getNestedProperty().getSimpleProperty());
    }
}
