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
package org.springframework.richclient.forms;

import junit.framework.TestCase;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;

/**
 * Test cases for {@link FocusLostTextValueSetter}
 * 
 * @author oliverh
 */
public class FocusLostTextValueSetterTest extends TestCase {

    private ValueModel valueModel;
    
    private TestableValueChangeListener valueListener;

    private TestableJTextComponent comp;

    private FocusLostTextValueSetter valueSetter;
    
    public void setUp() throws Exception {
        super.setUp();
        valueModel = new ValueHolder("originalValue");
        valueListener = new TestableValueChangeListener();
        valueModel.addValueChangeListener(valueListener);
        comp = new TestableJTextComponent();
        valueSetter = new FocusLostTextValueSetter(comp, valueModel);
    }

    public void testContructor() {
        try {
            new FocusLostTextValueSetter(null);
            fail("null component not allowed");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testComponentChangeDoesNotUpdateValueModel() {
        comp.setText("newValue");
        assertTrue(!valueModel.getValue().equals("newValue"));
        assertEquals(valueListener.getEventCount(), 0);
    }

    public void testValueModelChangeUpdatesComponent() {
        valueModel.setValue("newValue");
        assertEquals(comp.getText(), "newValue");
        assertEquals(valueListener.getEventCount(), 1);
    }
    
    public void testFocusChangeUpdatesValueModel() {        
        comp.typeText("a");        
        assertEquals(valueModel.getValue(), "originalValue");  
        assertEquals(valueListener.getEventCount(), 0);
        
        comp.gainFocus();        
        comp.typeText("b");        
        assertEquals(valueModel.getValue(), "originalValue");  
        assertEquals(valueListener.getEventCount(), 0);
        
        comp.loseFocus();                
        assertEquals(valueModel.getValue(), "ab");  
        assertEquals(valueListener.getEventCount(), 1);
    } 
}