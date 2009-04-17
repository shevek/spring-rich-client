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
package org.springframework.richclient.command;

import junit.framework.TestCase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Mathias Broekelmann
 * 
 */
public abstract class AbstractCommandTests extends TestCase {

    private TestListener testListener;

    protected void setUp() throws Exception {
        testListener = new TestListener();
    }

    public void testEnabledState() throws Exception {
        TestAbstractCommand command = new TestAbstractCommand();
        command.addEnabledListener(testListener);
        // test initial value
        command.secondaryEnabledState = false;
        assertFalse(command.isEnabled());
        command.setSecondaryEnabledState(false);
        assertEquals(0, testListener.eventCount);
        assertFalse(command.isEnabled());

        // test state change
        testListener.eventCount = 0;
        command.setSecondaryEnabledState(true);
        assertEquals(1, testListener.eventCount);
        assertTrue(command.isEnabled());

        // test initial value
        command = new TestAbstractCommand();
        command.addEnabledListener(testListener);
        testListener.eventCount = 0;
        command.secondaryEnabledState = true;
        assertTrue(command.isEnabled());
        command.setSecondaryEnabledState(false);
        assertEquals(1, testListener.eventCount);
        assertFalse(command.isEnabled());
    }

    public void testVisibleState() throws Exception {
        TestAbstractCommand command = new TestAbstractCommand();
        command.addPropertyChangeListener("visible", testListener);
        // test initial value
        command.secondaryVisibleState = false;
        command.setSecondaryVisibleState(false);
        assertEquals(0, testListener.eventCount);
        assertFalse(command.isVisible());

        // test state change
        testListener.eventCount = 0;
        command.setSecondaryVisibleState(true);
        assertEquals(1, testListener.eventCount);
        assertTrue(command.isVisible());

        // test initial value
        command = new TestAbstractCommand();
        command.addPropertyChangeListener("visible", testListener);
        testListener.eventCount = 0;
        command.secondaryVisibleState = true;
        assertTrue(command.isVisible());
        command.setSecondaryVisibleState(false);
        assertEquals(1, testListener.eventCount);
        assertFalse(command.isVisible());
    }

    protected class TestListener implements PropertyChangeListener {

        int eventCount = 0;

        public void propertyChange(PropertyChangeEvent evt) {
            eventCount++;
        }

    }

    protected class TestAbstractCommand extends AbstractCommand {

        boolean secondaryEnabledState;

        boolean secondaryVisibleState;

        public boolean isEnabled() {
            return super.isEnabled() && secondaryEnabledState;
        }

        public boolean isVisible() {
            return super.isVisible() && secondaryVisibleState;
        }

        public void setSecondaryEnabledState(boolean secondaryEnabledState) {
            if (secondaryEnabledState != this.secondaryEnabledState) {
                this.secondaryEnabledState = secondaryEnabledState;
                updatedEnabledState();
            }
        }

        public void setSecondaryVisibleState(boolean secondaryVisibleState) {
            if (secondaryVisibleState != this.secondaryVisibleState) {
                this.secondaryVisibleState = secondaryVisibleState;
                updatedVisibleState();
            }
        }

        public void execute() {
        }
    }
}
