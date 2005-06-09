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

import javax.swing.JList;

import org.springframework.binding.form.FormPropertyState;

public class ListBinderTests extends AbstractBindingTests {

    private ListBinding b;

    private JList c;

    protected String setUpBinding() {
        b = new ListBinding(fm, "listProperty");
        c = (JList)b.getControl();
        return "listProperty";
    }

    public void testComponentTracksEnabledChanges() {
        assertEquals(true, c.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, c.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, c.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        FormPropertyState state = fm.getFormPropertyState("listProperty");
        assertEquals(true, c.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, c.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, c.isEnabled());
    }

    public void testComponentUpdatesValueModel() {
        // TODO Auto-generated method stub
    }

    public void testValueModelUpdatesComponent() {
        // TODO Auto-generated method stub
    }
}