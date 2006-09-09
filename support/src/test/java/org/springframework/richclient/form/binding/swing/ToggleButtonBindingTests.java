/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.form.binding.swing;

import javax.swing.JToggleButton;

import org.springframework.binding.support.TestBean;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.DefaultFormModel;

/**
 * @author Mathias Broekelmann
 * 
 */
public class ToggleButtonBindingTests extends BindingAbstractTests {

    private JToggleButton tb;

    private ToggleButtonBinding tbb;

    protected String setUpBinding() {
        tbb = new ToggleButtonBinding(new JToggleButton(), fm, "booleanProperty");
        tb = (JToggleButton) tbb.getControl();
        return "booleanProperty";
    }

    public void testInitialValue() {
        TestBean bean = new TestBean();
        bean.setBooleanProperty(true);
        FormModel fm = new DefaultFormModel(bean);
        ToggleButtonBinding binding = new ToggleButtonBinding(new JToggleButton(), fm, "booleanProperty");
        JToggleButton control = (JToggleButton) binding.getControl();
        assertEquals(true, control.isSelected());
    }

    public void testComponentTracksEnabledChanges() {
        assertEquals(true, tb.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, tb.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, tb.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        FieldMetadata state = fm.getFieldMetadata(property);
        assertEquals(true, tb.isEnabled());
        state.setReadOnly(true);
        assertEquals(false, tb.isEnabled());
        state.setReadOnly(false);
        assertEquals(true, tb.isEnabled());
    }

    public void testComponentUpdatesValueModel() {
        tb.setSelected(true);
        assertEquals(Boolean.TRUE, vm.getValue());
        tb.setSelected(false);
        assertEquals(Boolean.FALSE, vm.getValue());
        tb.setSelected(true);
        assertEquals(Boolean.TRUE, vm.getValue());
    }

    public void testValueModelUpdatesComponent() {
        vm.setValue(Boolean.TRUE);
        assertTrue(tb.isSelected());
        vm.setValue(Boolean.FALSE);
        assertFalse(tb.isSelected());
        vm.setValue(Boolean.TRUE);
        assertTrue(tb.isSelected());
    }

}
