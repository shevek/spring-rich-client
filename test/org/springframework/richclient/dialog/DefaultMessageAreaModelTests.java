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
package org.springframework.richclient.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.rules.reporting.Severity;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class DefaultMessageAreaModelTests extends TestCase {

    private TestMessageAreaChangeListener ml1;

    private TestMessageAreaChangeListener ml2;

    public void testAddAndRemoveMessageListener() {
        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();

        buffer.addPropertyChangeListener(ml1);
        buffer.setMessage("Msg");
        assertEquals(buffer, ml1.lastUpdated);
        
        ml1.lastUpdated = null;
        buffer.removePropertyChangeListener(ml1);
        buffer.setMessage("Msg1");
        assertEquals(null, ml1.lastUpdated);
        
        
        buffer.addPropertyChangeListener(MessageAreaModel.MESSAGE_PROPERTY, ml1);
        buffer.setMessage("Msg");
        assertEquals(buffer, ml1.lastUpdated);
        
        ml1.lastUpdated = null;
        buffer.removePropertyChangeListener(MessageAreaModel.MESSAGE_PROPERTY, ml1);
        buffer.setMessage("Msg1");
        assertEquals(null, ml1.lastUpdated);

        buffer.addPropertyChangeListener("Some Other Property", ml1);
        buffer.setMessage("Msg");
        assertEquals(null, ml1.lastUpdated);        
    }

    public void testConstructor() {
        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
        assertNull(buffer.getMessage());
        assertNull(buffer.getSeverity());
        assertEquals(buffer, buffer.getDelegateFor());
    }

    public void testConstructorWithDelegate() {
        DefaultMessageAreaModel delegateFor = new DefaultMessageAreaModel();
        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel(delegateFor);
        assertNull(buffer.getMessage());
        assertNull(buffer.getSeverity());
        assertEquals(delegateFor, buffer.getDelegateFor());
    }

    public void testSetMessage() {
        String msg = "Info message";

        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.INFO);

        // with delegate
        DefaultMessageAreaModel delegate = new DefaultMessageAreaModel();
        buffer = new DefaultMessageAreaModel(delegate);
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.INFO);
    }

    private void assertMessageAndSeveritySet(DefaultMessageAreaModel buffer, String msg, Severity severity) {
        assertEquals("message was not set", msg, buffer.getMessage());
        assertEquals("severity must be info", severity, buffer.getSeverity());
        assertEquals("listener not notified", buffer.getDelegateFor(), ml1.lastUpdated);
        assertEquals("listener not notified", buffer.getDelegateFor(), ml2.lastUpdated);
    }

    public void testSetErrorMessage() {
        String msg = "Error message";

        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setErrorMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);

        // with delegate
        DefaultMessageAreaModel delegate = new DefaultMessageAreaModel();
        buffer = new DefaultMessageAreaModel(delegate);
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setErrorMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);
    }

    public void testSetMessageAndSeverity() {
        String msg = "Error message";
        Severity severity = Severity.WARNING;

        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setMessage(msg, severity);

        assertMessageAndSeveritySet(buffer, msg, severity);

        // with delegate
        DefaultMessageAreaModel delegate = new DefaultMessageAreaModel();
        buffer = new DefaultMessageAreaModel(delegate);
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setMessage(msg, severity);

        assertMessageAndSeveritySet(buffer, msg, severity);
    }

    public void testSetSameMessage() {
        String msg = "Test message";
        Severity severity = Severity.WARNING;

        DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
        buffer.addPropertyChangeListener(ml1);
        buffer.addPropertyChangeListener(ml2);

        buffer.setMessage(msg, severity);

        assertMessageAndSeveritySet(buffer, msg, severity);
        ml1.lastUpdated = null;
        ml2.lastUpdated = null;

        // and again
        buffer.setMessage(msg, severity);
        assertNull(ml1.lastUpdated);
        assertNull(ml2.lastUpdated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        ml1 = new TestMessageAreaChangeListener();
        ml2 = new TestMessageAreaChangeListener();
    }

    private static class TestMessageAreaChangeListener implements PropertyChangeListener {

        private MessageAreaModel lastUpdated;

        public void propertyChange(PropertyChangeEvent evt) {
            this.lastUpdated = (MessageAreaModel) evt.getSource();
            
        }

    }
}