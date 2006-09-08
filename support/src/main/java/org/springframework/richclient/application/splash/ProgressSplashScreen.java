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
package org.springframework.richclient.application.splash;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.springframework.richclient.progress.ProgressBarProgressMonitor;

/**
 * Splash screen implementation that shows the progress of the application startup.
 * 
 * @author Peter De Bruycker
 */
public class ProgressSplashScreen extends SimpleSplashScreen {
    private JProgressBar progressBar;
    private boolean showProgressLabel;

    public ProgressSplashScreen() {
        progressBar = new JProgressBar();
        setProgressMonitor(new ProgressBarProgressMonitor(progressBar));
    }

    public boolean getShowProgressLabel() {
        return showProgressLabel;
    }

    public void setShowProgressLabel(boolean showProgressLabel) {
        this.showProgressLabel = showProgressLabel;
    }

    protected JComponent createSplashContentPane() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(super.createSplashContentPane());

        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(showProgressLabel);

        content.add(progressBar, BorderLayout.SOUTH);

        return content;
    }
}
