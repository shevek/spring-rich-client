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

import org.springframework.rules.reporting.Severity;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class MessageBufferTest extends TestCase {

    private TestMessageListener ml1;

    private TestMessageListener ml2;

    public void testAddAndRemoveMessageListener() {
        MessageBuffer buffer = new MessageBuffer();

        buffer.addMessageListener(ml1);
        assertTrue(buffer.getMessageListeners().contains(ml1));
        assertEquals(1, buffer.getMessageListeners().size());

        buffer.addMessageListener(ml2);
        assertTrue(buffer.getMessageListeners().contains(ml1));
        assertEquals(2, buffer.getMessageListeners().size());

        buffer.removeMessageListener(ml1);
        assertFalse(buffer.getMessageListeners().contains(ml1));
        assertEquals(1, buffer.getMessageListeners().size());

        buffer.removeMessageListener(ml2);
        assertFalse(buffer.getMessageListeners().contains(ml2));
        assertTrue(buffer.getMessageListeners().isEmpty());
    }

    public void testConstructor() {
        MessageBuffer buffer = new MessageBuffer();
        assertNull(buffer.getMessage());
        assertNull(buffer.getSeverity());
        assertTrue(buffer.getMessageListeners().isEmpty());
        assertEquals(buffer, buffer.getDelegateFor());
    }

    public void testConstructorWithDelegate() {
        MessageBuffer delegateFor = new MessageBuffer();
        MessageBuffer buffer = new MessageBuffer(delegateFor);
        assertNull(buffer.getMessage());
        assertNull(buffer.getSeverity());
        assertTrue(buffer.getMessageListeners().isEmpty());
        assertEquals(delegateFor, buffer.getDelegateFor());
    }

    public void testSetMessage() {
        String msg = "Info message";

        MessageBuffer buffer = new MessageBuffer();
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.INFO);

        // with delegate
        MessageBuffer delegate = new MessageBuffer();
        buffer = new MessageBuffer(delegate);
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.INFO);
    }

    private void assertMessageAndSeveritySet(MessageBuffer buffer, String msg,
            Severity severity) {
        assertEquals("message was not set", msg, buffer.getMessage());
        assertEquals("severity must be info", severity, buffer.getSeverity());
        assertEquals("listener not notified", buffer.getDelegateFor(),
                ml1.lastUpdated);
        assertEquals("listener not notified", buffer.getDelegateFor(),
                ml2.lastUpdated);
    }

    public void testSetErrorMessage() {
        String msg = "Error message";

        MessageBuffer buffer = new MessageBuffer();
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setErrorMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);

        // with delegate
        MessageBuffer delegate = new MessageBuffer();
        buffer = new MessageBuffer(delegate);
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setErrorMessage(msg);

        assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);
    }

    public void testSetMessageAndSeverity() {
        String msg = "Error message";
        Severity severity = Severity.WARNING;

        MessageBuffer buffer = new MessageBuffer();
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setMessage(msg, severity);

        assertMessageAndSeveritySet(buffer, msg, severity);

        // with delegate
        MessageBuffer delegate = new MessageBuffer();
        buffer = new MessageBuffer(delegate);
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

        buffer.setMessage(msg, severity);

        assertMessageAndSeveritySet(buffer, msg, severity);
    }

    public void testSetSameMessage() {
        String msg = "Test message";
        Severity severity = Severity.WARNING;

        MessageBuffer buffer = new MessageBuffer();
        buffer.addMessageListener(ml1);
        buffer.addMessageListener(ml2);

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

        ml1 = new TestMessageListener();
        ml2 = new TestMessageListener();
    }

    private static class TestMessageListener implements MessageListener {

        private MessageReceiver lastUpdated;

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.richclient.dialog.MessageListener#messageUpdated(org.springframework.richclient.dialog.MessageReceiver)
         */
        public void messageUpdated(MessageReceiver source) {
            this.lastUpdated = source;
        }

    }
}