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
package org.springframework.richclient.settings.support;

import javax.swing.JLabel;
import javax.swing.JSplitPane;

import junit.framework.TestCase;

import org.springframework.richclient.settings.TransientSettings;

/**
 * @author Peter De Bruycker
 */
public class SplitPaneSettingsTests extends TestCase {

    private TransientSettings settings;

    private JSplitPane splitPane;

    public void testSaveState() {
        splitPane.setDividerLocation(333);

        SplitPaneSettings.saveState(settings, "split", splitPane);

        assertEquals(333, settings.getInt("split.dividerLocation"));
    }

    public void testRestoreState() {
        splitPane.setDividerLocation(333);
        assertEquals(333, splitPane.getDividerLocation());

        settings.setInt("split.dividerLocation", 250);

        SplitPaneSettings.restoreState(settings, "split", splitPane);

        assertEquals(250, splitPane.getDividerLocation());
    }

    protected void setUp() throws Exception {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JLabel("Left"));
        splitPane.setRightComponent(new JLabel("Right"));
        splitPane.setSize(800, 600);

        settings = new TransientSettings();
    }
}