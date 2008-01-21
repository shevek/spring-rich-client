/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.selection.binding.support;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.DefaultValueChangeDetector;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * Testcase for ValueModel2EventListBridge
 * 
 * @author Peter De Bruycker
 */
public class ValueModel2EventListBridgeTests extends TestCase {
    public void testValueHolderMustContainCollection() {
        EventList eventList = new BasicEventList();

        ValueModel valueModel = new ValueHolder("test");

        try {
            new ValueModel2EventListBridge(valueModel, eventList);
            fail("Must throw exception");
        }
        catch (IllegalArgumentException e) {
            // test passes
        }
    }

    public void testAutomaticSynchronization() {
        List list1 = Arrays.asList(new String[] { "item 1", "item2", "item3" });
        List list2 = Arrays.asList(new String[] { "item 4", "item5", "item6" });

        EventList eventList = new BasicEventList();
        ValueModel valueModel = new ValueHolder(list1);

        ValueModel2EventListBridge bridge = new ValueModel2EventListBridge(valueModel, eventList);
        assertEquals("auto sync: data copied in constructor", list1, eventList);

        valueModel.setValue(list2);
        assertEquals("when value in ValueModel changes, it's copied to the EventList", list2, eventList);
    }

    public void testManualSynchronization() {
        List list1 = Arrays.asList(new String[] { "item 1", "item2", "item3" });
        List list2 = Arrays.asList(new String[] { "item 4", "item5", "item6" });

        EventList eventList = new BasicEventList();
        ValueModel valueModel = new ValueHolder(list1);

        ValueModel2EventListBridge bridge = new ValueModel2EventListBridge(valueModel, eventList, true);
        assertTrue("manual sync: data not copied in constructor", eventList.isEmpty());

        bridge.synchronize();
        assertEquals("sync copies data", list1, eventList);

        valueModel.setValue(list2);
        assertEquals("when value in ValueModel changes, it's NOT copied to the EventList", list1, eventList);

        bridge.synchronize();
        assertEquals(list2, eventList);
    }

    protected void setUp() throws Exception {
        ApplicationServices services = new ApplicationServices() {

            public Object getService(Class serviceType) {
                return new DefaultValueChangeDetector();
            }

            public boolean containsService(Class serviceType) {
                return ValueChangeDetector.class.equals(serviceType);
            }

        };
        ApplicationServicesLocator.load(new ApplicationServicesLocator(services));
    }
}
