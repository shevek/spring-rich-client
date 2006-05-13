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
package org.springframework.binding.form.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.support.TestPropertyChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Tests for @link AbstractFormModel
 * 
 * @author  Oliver Hutchison
 */
public class AbstractFormModelTests extends SpringRichTestCase {

    protected AbstractFormModel getFormModel(Object formObject) {
        return new TestAbstractFormModel(formObject);
    }

    protected AbstractFormModel getFormModel(BeanPropertyAccessStrategy pas, boolean buffering) {
        return new TestAbstractFormModel(pas, buffering);
    }

    public void testGetValueModelFromPAS() {
        TestBean p = new TestBean();
        TestPropertyAccessStrategy tpas = new TestPropertyAccessStrategy(p);
        AbstractFormModel fm = getFormModel(tpas, true);
        ValueModel vm1 = fm.getValueModel("simpleProperty");
        assertEquals(1, tpas.numValueModelRequests());
        assertEquals("simpleProperty", tpas.lastRequestedValueModel());
        ValueModel vm2 = fm.getValueModel("simpleProperty");
        assertEquals(vm1, vm2);
        assertEquals(1, tpas.numValueModelRequests());

        try {
            fm.getValueModel("iDontExist");
            fail("should't be able to get value model for invalid property");
        }
        catch (NotReadablePropertyException e) {
            // exprected
        }
    }

    public void testUnbufferedWritesThrough() {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        AbstractFormModel fm = getFormModel(pas, false);
        ValueModel vm = fm.getValueModel("simpleProperty");

        vm.setValue("1");
        assertEquals("1", p.getSimpleProperty());

        vm.setValue(null);
        assertEquals(null, p.getSimpleProperty());
    }

    public void testBufferedDoesNotWriteThrough() {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        AbstractFormModel fm = getFormModel(pas, true);
        ValueModel vm = fm.getValueModel("simpleProperty");

        vm.setValue("1");
        assertEquals(null, p.getSimpleProperty());

        vm.setValue(null);
        assertEquals(null, p.getSimpleProperty());
    }

    public void testDirtyTrackingWithBuffering() {
        testDirtyTracking(true);
    }

    public void testDirtyTrackingWithoutBuffering() {
        testDirtyTracking(false);
    }

    public void testDirtyTracking(boolean buffering) {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FormModel.DIRTY_PROPERTY);
        AbstractFormModel fm = getFormModel(pas, buffering);
        fm.addPropertyChangeListener(FormModel.DIRTY_PROPERTY, pcl);
        ValueModel vm = fm.getValueModel("simpleProperty");
        assertTrue(!fm.isDirty());

        vm.setValue("2");
        assertTrue(fm.isDirty());
        assertEquals(1, pcl.eventCount());

        fm.commit();
        assertTrue(!fm.isDirty());
        assertEquals(2, pcl.eventCount());

        vm.setValue("1");
        assertTrue(fm.isDirty());
        assertEquals(3, pcl.eventCount());

        fm.setFormObject(new TestBean());
        assertTrue(!fm.isDirty());
        assertEquals(4, pcl.eventCount());

        vm.setValue("2");
        assertTrue(fm.isDirty());
        assertEquals(5, pcl.eventCount());

        fm.revert();
        assertTrue(!fm.isDirty());
        assertEquals(6, pcl.eventCount());
    }

    /**
     * Test on dirty state of parent-child relations. When child gets dirty, parent should
     * also be dirty. When parent reverts, child should revert too.
     */
    public void testDirtyTracksKids() {
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FormModel.DIRTY_PROPERTY);
        AbstractFormModel pfm = getFormModel(new TestBean());
        AbstractFormModel fm = getFormModel(new TestBean());
        pfm.addPropertyChangeListener(FormModel.DIRTY_PROPERTY, pcl);
        pfm.addChild(fm);
        ValueModel childSimpleProperty = fm.getValueModel("simpleProperty");
        ValueModel parentSimpleProperty = pfm.getValueModel("simpleProperty");
        // test child property dirty -> parent dirty
        childSimpleProperty.setValue("1");
        assertTrue(pfm.isDirty());
        assertEquals(1, pcl.eventCount());

        fm.revert();
        assertTrue(!pfm.isDirty());
        assertEquals(2, pcl.eventCount());
        // child dirty -> revert parent triggers revert on child
        childSimpleProperty.setValue("1");
        assertTrue(pfm.isDirty());
        assertEquals(3, pcl.eventCount());
        
        pfm.revert(); 
        assertTrue(!pfm.isDirty());
        assertTrue(!fm.isDirty());
        assertEquals(4, pcl.eventCount());
        // child & parent property dirty -> parent dirty, revert child, then parent
        childSimpleProperty.setValue("1");
        assertTrue(pfm.isDirty());
        assertEquals(5, pcl.eventCount());

        parentSimpleProperty.setValue("2");
        assertTrue(pfm.isDirty());
        assertEquals(5, pcl.eventCount());

        fm.revert();
        assertTrue(pfm.isDirty());
        assertEquals(5, pcl.eventCount());

        pfm.revert();
        assertTrue(!pfm.isDirty());
        assertEquals(6, pcl.eventCount());
    }

    public void testSetFormObjectDoesNotRevertChangesToPreviousFormObject() {
        TestBean p1 = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p1);
        AbstractFormModel fm = getFormModel(pas, false);
        fm.getValueModel("simpleProperty").setValue("1");
        fm.setFormObject(new TestBean());
        assertEquals("1", p1.getSimpleProperty());
    }

    public void testCommitEvents() {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        TestCommitListener cl = new TestCommitListener();
        AbstractFormModel fm = getFormModel(pas, false);
        fm.addCommitListener(cl);
        ValueModel vm = fm.getValueModel("simpleProperty");

        vm.setValue("1");
        fm.commit();
        assertEquals(1, cl.preEditCalls);
        assertEquals(1, cl.postEditCalls);
    }

    public void testCommitWritesBufferingThrough() {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        TestCommitListener cl = new TestCommitListener();
        AbstractFormModel fm = getFormModel(pas, true);
        fm.addCommitListener(cl);
        ValueModel vm = fm.getValueModel("simpleProperty");

        vm.setValue("1");
        fm.commit();
        assertEquals("1", p.getSimpleProperty());
    }

    public void testRevertWithBuffering() {
        testRevert(true);
    }

    public void testRevertWithoutBuffering() {
        testRevert(false);
    }

    public void testRevert(boolean buffering) {
        TestBean p = new TestBean();
        BeanPropertyAccessStrategy pas = new BeanPropertyAccessStrategy(p);
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FormModel.DIRTY_PROPERTY);
        AbstractFormModel fm = getFormModel(pas, buffering);
        fm.addPropertyChangeListener(FormModel.DIRTY_PROPERTY, pcl);
        ValueModel vm = fm.getValueModel("simpleProperty");

        vm.setValue("1");
        fm.revert();

        assertEquals(null, vm.getValue());
        assertEquals(null, p.getSimpleProperty());

        TestBean tb2 = new TestBean();
        tb2.setSimpleProperty("tb2");
        fm.setFormObject(tb2);

        vm.setValue("1");
        fm.revert();

        assertEquals("tb2", vm.getValue());
        assertEquals("tb2", tb2.getSimpleProperty());
    }

    public void testEnabledEvents() {
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FormModel.ENABLED_PROPERTY);
        AbstractFormModel fm = getFormModel(new Object());
        fm.addPropertyChangeListener(FormModel.ENABLED_PROPERTY, pcl);

        assertTrue(fm.isEnabled());

        fm.setEnabled(false);
        assertTrue(!fm.isEnabled());
        assertEquals(1, pcl.eventCount());

        fm.setEnabled(false);
        assertTrue(!fm.isEnabled());
        assertEquals(1, pcl.eventCount());

        fm.setEnabled(true);
        assertTrue(fm.isEnabled());
        assertEquals(2, pcl.eventCount());

        fm.setEnabled(true);
        assertTrue(fm.isEnabled());
        assertEquals(2, pcl.eventCount());
    }

    public void testEnabledTracksParent() {
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FormModel.ENABLED_PROPERTY);
        AbstractFormModel pfm = getFormModel(new Object());
        AbstractFormModel fm = getFormModel(new Object());
        fm.addPropertyChangeListener(FormModel.ENABLED_PROPERTY, pcl);
        pfm.addChild(fm);

        pfm.setEnabled(false);
        assertTrue(!fm.isEnabled());
        assertEquals(1, pcl.eventCount());

        pfm.setEnabled(true);
        assertTrue(fm.isEnabled());
        assertEquals(2, pcl.eventCount());

        pfm.setEnabled(false);
        assertTrue(!fm.isEnabled());
        assertEquals(3, pcl.eventCount());

        fm.setEnabled(false);
        assertTrue(!fm.isEnabled());
        assertEquals(3, pcl.eventCount());

        pfm.setEnabled(true);
        assertTrue(!fm.isEnabled());
        assertEquals(3, pcl.eventCount());

        fm.setEnabled(true);
        assertTrue(fm.isEnabled());
        assertEquals(4, pcl.eventCount());
    }

    public void testConvertingValueModels() {
        AbstractFormModel fm = getFormModel(new TestBean());
        TestConversionService cs = new TestConversionService();
        fm.setConversionService(cs);

        ValueModel vm = fm.getValueModel("simpleProperty", String.class);
        assertEquals(fm.getValueModel("simpleProperty"), vm);
        assertEquals(0, cs.calls);

        try {
            fm.getValueModel("simpleProperty", Integer.class);
            fail("should have throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(1, cs.calls);
        assertEquals(String.class, cs.lastSource);
        assertEquals(Integer.class, cs.lastTarget);

        cs.executer = new ConversionExecutor(null, null);
        ValueModel cvm = fm.getValueModel("simpleProperty", Integer.class);
        assertEquals(3, cs.calls);
        assertEquals(Integer.class, cs.lastSource);
        assertEquals(String.class, cs.lastTarget);

        assertEquals(fm.getValueModel("simpleProperty", Integer.class), cvm);
        assertEquals(3, cs.calls);
    }

    public void testFieldMetadata() {
        AbstractFormModel fm = getFormModel(new TestBean());

        assertEquals(String.class, fm.getFieldMetadata("simpleProperty").getPropertyType());
        assertTrue(!fm.getFieldMetadata("simpleProperty").isReadOnly());

        assertEquals(Object.class, fm.getFieldMetadata("readOnly").getPropertyType());
        assertTrue(fm.getFieldMetadata("readOnly").isReadOnly());
    }

    public void testSetFormObjectUpdatesDirtyState() {
        final AbstractFormModel fm = getFormModel(new TestBean());
        fm.add("simpleProperty");
        fm.add("singleSelectListProperty");

        assertTrue(!fm.isDirty());

        fm.getValueModel("simpleProperty").addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                fm.getValueModel("singleSelectListProperty").setValue(null);
            }
        });

        TestBean newBean = new TestBean();
        newBean.setSimpleProperty("simpleProperty");
        newBean.setSingleSelectListProperty("singleSelectListProperty");
        fm.setFormObject(newBean);
        assertEquals(null, fm.getValueModel("singleSelectListProperty").getValue());
        assertTrue(fm.isDirty());
        fm.getValueModel("singleSelectListProperty").setValue("singleSelectListProperty");
        assertTrue(!fm.isDirty());
    }
    
    public void testFormPropertiesAreAccessableFromFormObjectChangeEvents() {
        final AbstractFormModel fm = getFormModel(new TestBean());
        assertEquals(null, fm.getValueModel("simpleProperty").getValue());
        TestBean newTestBean = new TestBean();
        newTestBean.setSimpleProperty("NewValue");
        fm.getFormObjectHolder().addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                assertEquals("NewValue", fm.getValueModel("simpleProperty").getValue());
            }
        });
        fm.setFormObject(newTestBean);
    }
    
    public void testFormObjectChangeEventComesBeforePropertyChangeEvent() {
        final TestBean testBean = new TestBean();
        final AbstractFormModel fm = getFormModel(testBean);
        final TestBean newTestBean = new TestBean();
        newTestBean.setSimpleProperty("NewValue");
        final boolean[] formObjectChangeCalled = new boolean[1];
        fm.getFormObjectHolder().addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                formObjectChangeCalled[0] = true;
            }
        });
        fm.getValueModel("simpleProperty").addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                assertEquals("Form property change event was called before form object change event", true, formObjectChangeCalled[0]);
            }
        });
        fm.setFormObject(newTestBean);
    }
    
    public void testFormObjectChangeEvents() {
        TestBean testBean = new TestBean();
        final AbstractFormModel fm = getFormModel(testBean);
        TestBean newTestBean = new TestBean();
        newTestBean.setSimpleProperty("NewValue");
        TestPropertyChangeListener testPCL = new TestPropertyChangeListener(ValueModel.VALUE_PROPERTY);
        fm.getFormObjectHolder().addValueChangeListener(testPCL);
        fm.setFormObject(newTestBean);
        assertEquals(1, testPCL.eventCount());
        assertEquals(testBean, testPCL.lastEvent().getOldValue());
        assertEquals(newTestBean, testPCL.lastEvent().getNewValue());
    }

    public static class TestCommitListener implements CommitListener {
        int preEditCalls;

        int postEditCalls;

        public void preCommit(FormModel formModel) {
            preEditCalls++;
        }

        public void postCommit(FormModel formModel) {
            postEditCalls++;
        }
    }

    public class TestConversionService implements ConversionService {

        public int calls;

        public Class lastSource;

        public Class lastTarget;

        public ConversionExecutor executer;

        public ConversionExecutor getConversionExecutor(Class source, Class target) {
            calls++;
            lastSource = source;
            lastTarget = target;
            if (executer != null) {
                return executer;
            }
            throw new IllegalArgumentException("no converter found");
        }

        public ConversionExecutor getConversionExecutorByTargetAlias(Class arg0, String arg1)
                throws IllegalArgumentException {
            fail("this method should never be called");
            return null;
        }

        public Class getClassByAlias(String arg0) {
            fail("this method should never be called");
            return null;
        }
    }
}
