/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;

import org.springframework.binding.support.TestLabeledEnum;

public class LabeledEnumComboBoxBindingAbstractTests extends BindingAbstractTests {

    private LabeledEnumComboBoxBinding cbb;

    private JComboBox cb;

    protected String setUpBinding() {
        cbb = (LabeledEnumComboBoxBinding) new LabeledEnumComboBoxBinder().bind(fm, "enumProperty", Collections.EMPTY_MAP);
        cb = (JComboBox) cbb.getControl();
        return "enumProperty";
    }

    public void testValueModelUpdatesComponent() {
        TestListDataListener tldl = new TestListDataListener();
        cb.getModel().addListDataListener(tldl);

        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());
        tldl.assertCalls(0);

        vm.setValue(TestLabeledEnum.ONE);
        assertEquals(TestLabeledEnum.ONE, cb.getSelectedItem());
        assertEquals(1, cb.getSelectedIndex());
        tldl.assertEvent(1, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue(TestLabeledEnum.TWO);
        assertEquals(TestLabeledEnum.TWO, cb.getSelectedItem());
        assertEquals(2, cb.getSelectedIndex());
        tldl.assertEvent(2, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue(null);
        assertEquals(null, cb.getSelectedItem());
        assertEquals(-1, cb.getSelectedIndex());
        tldl.assertEvent(3, ListDataEvent.CONTENTS_CHANGED, -1, -1);

        vm.setValue(null);
        tldl.assertCalls(3);
    }

    public void testComponentUpdatesValueModel() {
        cb.setSelectedIndex(1);
        assertEquals(TestLabeledEnum.ONE, vm.getValue());

        cb.setSelectedItem(TestLabeledEnum.TWO);
        assertEquals(TestLabeledEnum.TWO, vm.getValue());

        cb.setSelectedIndex(-1);
        assertEquals(null, vm.getValue());
    }

    public void testComponentTracksEnabledChanges() {
        assertTrue(cb.isEnabled());

        fm.getFieldMetadata("enumProperty").setEnabled(false);
        assertFalse(cb.isEnabled());

        fm.getFieldMetadata("enumProperty").setEnabled(true);
        assertTrue(cb.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        assertTrue(cb.isEnabled());

        fm.getFieldMetadata("enumProperty").setReadOnly(true);
        assertFalse(cb.isEnabled());

        fm.getFieldMetadata("enumProperty").setReadOnly(false);
        assertTrue(cb.isEnabled());
    }
}