/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.validation;

import junit.framework.TestCase;

public class DefaultValidationResultsTests extends TestCase {
    private DefaultValidationResults vr = new DefaultValidationResults();

    public void testAddAndGetMessages() {
        assertEquals(0, vr.getMessageCount());
        assertEquals(0, vr.getMessageCount(Severity.INFO));
        assertEquals(0, vr.getMessageCount("field1"));
        assertEquals(false, vr.getHasErrors());
        assertEquals(false, vr.getHasWarnings());
        assertEquals(false, vr.getHasInfo());

        vr.addMessage("field1", Severity.INFO, "message");
        assertEquals(1, vr.getMessageCount());
        assertEquals(1, vr.getMessageCount(Severity.INFO));
        assertEquals(1, vr.getMessageCount("field1"));
        ValidationMessage vm = (ValidationMessage)vr.getMessages().get(0);
        assertEquals("field1", vm.getProperty());
        assertEquals(Severity.INFO, vm.getSeverity());
        assertEquals("message", vm.getMessage());
        assertEquals(vm, vr.getMessages("field1").get(0));
        assertEquals(vm, vr.getMessages(Severity.INFO).get(0));
        assertEquals(false, vr.getHasErrors());
        assertEquals(false, vr.getHasWarnings());
        assertEquals(true, vr.getHasInfo());

        vm = new DefaultValidationMessage("field2", Severity.WARNING, "message");
        vr.addMessage(vm);
        assertEquals(2, vr.getMessageCount());
        assertEquals(1, vr.getMessageCount(Severity.WARNING));
        assertEquals(1, vr.getMessageCount("field2"));
        assertEquals(vm, vr.getMessages().get(1));
        assertEquals(vm, vr.getMessages("field2").get(0));
        assertEquals(vm, vr.getMessages(Severity.WARNING).get(0));
        assertEquals(false, vr.getHasErrors());
        assertEquals(true, vr.getHasWarnings());
        assertEquals(true, vr.getHasInfo());

        vm = new DefaultValidationMessage(ValidationMessage.GLOBAL_PROPERTY, Severity.ERROR, "message");
        vr.addMessage(vm);
        assertEquals(3, vr.getMessageCount());
        assertEquals(1, vr.getMessageCount(Severity.ERROR));
        assertEquals(1, vr.getMessageCount(ValidationMessage.GLOBAL_PROPERTY));
        assertEquals(vm, vr.getMessages().get(2));
        assertEquals(vm, vr.getMessages(ValidationMessage.GLOBAL_PROPERTY).get(0));
        assertEquals(vm, vr.getMessages(Severity.ERROR).get(0));
        assertEquals(true, vr.getHasErrors());
        assertEquals(true, vr.getHasWarnings());
        assertEquals(true, vr.getHasInfo());

        vm = new DefaultValidationMessage("field1", Severity.ERROR, "message");
        vr.addMessage(vm);
        assertEquals(4, vr.getMessageCount());
        assertEquals(2, vr.getMessageCount(Severity.ERROR));
        assertEquals(2, vr.getMessageCount("field1"));
        assertEquals(vm, vr.getMessages().get(3));
        assertEquals(vm, vr.getMessages("field1").get(1));
        assertEquals(vm, vr.getMessages(Severity.ERROR).get(1));
        
        DefaultValidationResults vr2 = new DefaultValidationResults();
        vm = new DefaultValidationMessage("newField", Severity.INFO, "message");
        vr2.addMessage(vm);
        ValidationMessage vm2 = new DefaultValidationMessage("newField", Severity.ERROR, "message");
        vr2.addMessage(vm2);
        
        vr.addAllMessages(vr2.getMessages());
        assertEquals(6, vr.getMessageCount());
        assertEquals(3, vr.getMessageCount(Severity.ERROR));
        assertEquals(2, vr.getMessageCount(Severity.INFO));
        assertEquals(2, vr.getMessageCount("newField"));
        assertEquals(vm, vr.getMessages().get(4));
        assertEquals(vm2, vr.getMessages().get(5));
        assertEquals(vm, vr.getMessages("newField").get(0));
        assertEquals(vm2, vr.getMessages("newField").get(1));
        assertEquals(vm, vr.getMessages(Severity.INFO).get(1));
        assertEquals(vm2, vr.getMessages(Severity.ERROR).get(2));
    }

    public void testReturnedListsAreNotModifiable() {
        vr.addMessage("field1", Severity.ERROR, "what ever!");
        try {
            vr.getMessages().clear();
            fail();
        }
        catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            vr.getMessages("field1").clear();
            fail();
        }
        catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            vr.getMessages(Severity.ERROR).clear();
            fail();
        }
        catch (UnsupportedOperationException e) {
            // expected
        }
    }
}