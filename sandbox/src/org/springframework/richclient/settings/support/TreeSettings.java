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

import javax.swing.JTree;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Peter De Bruycker
 */
public class TreeSettings {
    private static final String EXPANSION_STATE = "expansionState";
    private static final String SELECTED_ROWS = "selectedRows";

    private TreeSettings() {
        // no instantiations, static utility class
    }

    private static void assertArgumentsOk(Settings s, String key, JTree tree) {
        Assert.notNull(s, "Settings cannot be null.");
        Assert.notNull(tree, "Tree cannot be null.");
        Assert.hasText(key, "Key must have text.");
    }

    public static void restoreState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        restoreExpansionState(s, key, tree);
        restoreSelectionState(s, key, tree);
    }

    public static void restoreState(Settings s, JTree tree) {
        Assert.hasText(tree.getName(), "Name attribute of tree must be filled in.");

        restoreState(s, tree.getName(), tree);
    }

    public static void saveState(Settings s, JTree tree) {
        Assert.hasText(tree.getName(), "Name attribute of tree must be filled in.");

        saveState(s, tree.getName(), tree);
    }

    public static void saveState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        saveExpansionState(s, key, tree);
        saveSelectionState(s, key, tree);
    }

    public static void saveExpansionState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        int rowCount = tree.getRowCount();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rowCount; i++) {
            sb.append(tree.isExpanded(i) ? 1 : 0);
            if (i < rowCount - 1) {
                sb.append(",");
            }
        }
        s.setString(key + "." + EXPANSION_STATE, sb.toString());
    }

    public static void restoreExpansionState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        String expansionKey = key + "." + EXPANSION_STATE;
        if (s.contains(expansionKey)) {
            String[] states = s.getString(expansionKey).split(",");

            try {
                int[] expansionStates = ArrayUtil.toIntArray(states);

                for (int i = 0; i < expansionStates.length; i++) {
                    if (expansionStates[i] == 1) {
                        tree.expandRow(i);
                    }
                }
            }
            catch (IllegalArgumentException e) {
                // TODO log this
            }
        }
    }

    /**
     * TODO store lead and anchor selection
     */
    public static void saveSelectionState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        String selectionKey = key + "." + SELECTED_ROWS;
        if (s.contains(selectionKey)) {
            s.remove(selectionKey);
        }

        if (tree.getSelectionCount() > 0) {
            String selectionString = ArrayUtil.asIntervalString(tree.getSelectionRows());
            if (selectionString.length() > 0) {
                s.setString(selectionKey, selectionString);
            }
        }
    }

    public static void restoreSelectionState(Settings s, String key, JTree tree) {
        assertArgumentsOk(s, key, tree);

        tree.getSelectionModel().clearSelection();

        String selectionKey = key + "." + SELECTED_ROWS;
        if (s.contains(selectionKey)) {
            String selection = s.getString(selectionKey);
            if (StringUtils.hasText(selection)) {
                String[] parts = selection.split(",");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].indexOf('-') >= 0) {
                        String[] tmp = parts[i].split("-");
                        tree.addSelectionInterval(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
                    }
                    else {
                        int index = Integer.parseInt(parts[i]);
                        tree.addSelectionRow(index);
                    }
                }
            }
        }
    }
}
