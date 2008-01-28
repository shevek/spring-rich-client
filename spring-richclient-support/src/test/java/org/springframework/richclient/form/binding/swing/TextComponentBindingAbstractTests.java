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
package org.springframework.richclient.form.binding.swing;

import javax.swing.JTextField;

import org.springframework.binding.form.FieldMetadata;

public class TextComponentBindingAbstractTests extends BindingAbstractTests {

    private JTextField tc;

    private TextComponentBinding b;

    protected String setUpBinding() {        
        b = new TextComponentBinding(new JTextField(), fm, "simpleProperty");
        tc = (JTextField)b.getControl();
        return "simpleProperty";
    }

    public void testComponentTracksEnabledChanges() {
        assertEquals(true, tc.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, tc.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, tc.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        FieldMetadata state = fm.getFieldMetadata("simpleProperty");
        assertEquals(true, tc.isEditable());
        state.setReadOnly(true);
        assertEquals(false, tc.isEditable());
        state.setReadOnly(false);
        assertEquals(true, tc.isEditable());
    }

    public void testComponentUpdatesValueModel() {
        tc.setText("1");
        assertEquals("1", vm.getValue());
        tc.setText(null);
        assertEquals("", vm.getValue());
        tc.setText("2");
        assertEquals("2", vm.getValue());
        tc.setText("");
        assertEquals("", vm.getValue());
    }

    public void testValueModelUpdatesComponent() {
        vm.setValue("1");
        assertEquals("1", tc.getText());
        vm.setValue(null);
        assertEquals("", tc.getText());
        vm.setValue("2");
        assertEquals("2", tc.getText());
        vm.setValue("");
        assertEquals("", tc.getText());
    }
}
