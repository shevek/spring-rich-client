/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.progress;

import javax.swing.JProgressBar;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class ProgressBarProgressMonitorTests extends TestCase {
    public void testConstructorWithNullArgumentThrowsException() {
        try {
            new ProgressBarProgressMonitor(null);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // test passes
        }
    }

    public void testConstructor() {
        JProgressBar progressBar = new JProgressBar();
        ProgressBarProgressMonitor monitor = new ProgressBarProgressMonitor(progressBar);

        assertSame(progressBar, monitor.getProgressBar());
    }

    public void testProgress() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        ProgressBarProgressMonitor monitor = new ProgressBarProgressMonitor(progressBar);
        assertTrue(progressBar.isIndeterminate());

        monitor.taskStarted("main-task", 50);
        assertEquals("main-task", progressBar.getString());
        assertEquals(0, progressBar.getMinimum());
        assertEquals(50, progressBar.getMaximum());

        monitor.subTaskStarted("sub-task 1");
        assertEquals("sub-task 1", progressBar.getString());

        monitor.worked(5);
        assertEquals(5, progressBar.getValue());

        monitor.worked(10);
        assertEquals(15, progressBar.getValue());
    }
}
