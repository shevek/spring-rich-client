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

import net.sf.nachocalendar.components.DateField;
import org.springframework.binding.form.FieldMetadata;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for NachoCalendarDateFieldBinder and NachoCalendarDateFieldBinding
 *
 * @author Geoffrey De Smet
 */
public class NachoCalendarDateFieldBinderTests extends BindingAbstractTests {

    private NachoCalendarDateFieldBinder binder;

    private NachoCalendarDateFieldBinding binding;

    private DateField dateField;

    protected String setUpBinding() {
//        Application.load(null);
//        new Application(new DefaultApplicationLifecycleAdvisor());
//        StaticApplicationContext applicationContext = new StaticApplicationContext();
//        Application.services().setApplicationContext(applicationContext);
//        applicationContext.refresh();

        Map context = new HashMap();

        binder = new NachoCalendarDateFieldBinder();
        binding = (NachoCalendarDateFieldBinding) binder.bind(fm, "dateProperty", context);
        dateField = (DateField) binding.getControl();

        return "dateProperty";
    }

    public void testInitialValue() {
        Date date = createDate(1981, 10, 16);
        vm.setValue(date);
        assertNotNull(vm.getValue());
    }

    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Date date = calendar.getTime();
        return date;
    }

    public void testComponentTracksEnabledChanges() {
        assertEquals(true, dateField.isEnabled());
        fm.setEnabled(false);
        assertEquals(false, dateField.isEnabled());
        fm.setEnabled(true);
        assertEquals(true, dateField.isEnabled());
    }

    public void testComponentTracksReadOnlyChanges() {
        FieldMetadata state = fm.getFieldMetadata("dateProperty");
        assertEquals(true, dateField.isEnabled());
        state.setReadOnly(true);
        assertEquals(false, dateField.isEnabled());
        state.setReadOnly(false);
        assertEquals(true, dateField.isEnabled());
    }

    public void testComponentUpdatesValueModel() {
        Date date1 = createDate(1981, 10, 16);
        dateField.setValue(date1);
        assertEquals(date1, vm.getValue());
        dateField.setValue(null);
        assertEquals(null, vm.getValue());
        Date date2 = createDate(1999, 11, 31);
        dateField.setValue(date2);
        assertEquals(date2, vm.getValue());
    }

    public void testValueModelUpdatesComponent() {
        Date date1 = createDate(1981, 10, 16);
        vm.setValue(date1);
        assertEquals(date1, dateField.getValue());
        vm.setValue(null);
        assertEquals(null, dateField.getValue());
        Date date2 = createDate(1999, 11, 31);
        vm.setValue(date2);
        assertEquals(date2, dateField.getValue());
    }
}