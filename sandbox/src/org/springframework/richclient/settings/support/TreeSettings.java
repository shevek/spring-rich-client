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

import java.util.StringTokenizer;

import javax.swing.JTree;

/**
 * TODO add assertions (tree not null)
 * @author Peter De Bruycker
 */
public class TreeSettings {
    private TreeSettings() {
        // no instantiations
    }

    public static String saveExpansionState(JTree tree) {
        int rowCount = tree.getRowCount();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rowCount; i++) {
            sb.append(tree.isExpanded(i) ? 1 : 0);
            if (i < rowCount - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    public static void restoreExpansionState(JTree tree, String pref) {
        //		TODO use pref.split(",");
        StringTokenizer st = new StringTokenizer(pref, ",");
        int j = 0;
        String[] states = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            states[j] = st.nextToken();
            j++;
        }

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

    /**
     * TODO store lead and anchor selection
     */
    public static String saveSelectionState(JTree tree) {
        // TODO use same logic to calculate intervals as with table selection
        StringBuffer sb = new StringBuffer();
        if (tree.getSelectionCount() > 0) {
            int[] selectedRows = tree.getSelectionRows();
            for (int i = 0; i < selectedRows.length; i++) {
                sb.append(selectedRows[i]);
                if (i < selectedRows.length - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

    public static void restoreSelectionState(JTree tree, String pref) {
        //		TODO use pref.split(",");
        StringTokenizer st = new StringTokenizer(pref, ",");
        int j = 0;
        String[] selections = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            selections[j] = st.nextToken();
            j++;
        }

        try {
            int[] selectedRows = ArrayUtil.toIntArray(selections);

            for (int i = 0; i < selectedRows.length; i++) {
                tree.addSelectionRow(selectedRows[i]);
            }
        }
        catch (IllegalArgumentException e) {
            // TODO log this exception
        }
    }

}
