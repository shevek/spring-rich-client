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

import javax.swing.JSplitPane;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;

/**
 * Help class for saving and restoring <code>JSplitPane</code> settings.
 * 
 * @author Peter De Bruycker
 */
public class SplitPaneSettings {

    private SplitPaneSettings() {
        // no instances
    }

    public static void saveState(Settings s, String key, JSplitPane splitPane) {
        assertParameters(s, key, splitPane);

        s.setInt(key + ".dividerLocation", splitPane.getDividerLocation());
    }

    public static void restoreState(Settings s, String key, JSplitPane splitPane) {
        assertParameters(s, key, splitPane);

        if (s.contains(key + ".dividerLocation")) {
            splitPane.setDividerLocation(s.getInt(key + ".dividerLocation"));
        }
    }

    private static void assertParameters(Settings s, String key, JSplitPane splitPane) {
        Assert.notNull(s, "Settings cannot be null");
        Assert.hasText(key, "Key cannot be empty or null");
        Assert.notNull(splitPane, "SplitPane cannot be null");
    }
}