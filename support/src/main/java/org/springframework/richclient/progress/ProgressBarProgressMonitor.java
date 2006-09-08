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

/**
 * <code>ProgressMonitor</code> implementation that delegates to a
 * <code>JProgressBar</code>.
 * 
 * @author Peter De Bruycker
 */
public class ProgressBarProgressMonitor implements ProgressMonitor {

    private JProgressBar progressBar;
    private boolean canceled;

    public ProgressBarProgressMonitor(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean b) {
        this.canceled = b;
    }

    public void done() {
        // not used
    }

    public void subTaskStarted(String name) {
        progressBar.setString(name);
    }

    public void taskStarted(String name, int totalWork) {
        progressBar.setIndeterminate(false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(totalWork);
        progressBar.setString(name);
    }

    public void worked(int work) {
        progressBar.setValue(progressBar.getValue() + work);
    }
}
