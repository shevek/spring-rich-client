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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import junit.framework.TestCase;

/**
 * TODO change TreeSettings and this test to use Settings
 * @author Peter De Bruycker
 */
public class TreeSettingsTests extends TestCase {

    private JTree tree;

    public void testSaveSelectionState() {
        String pref = TreeSettings.saveSelectionState(tree);
        assertEquals("", pref);

        tree.setSelectionRows(new int[] { 0, 2, 3 });
        pref = TreeSettings.saveSelectionState(tree);
        assertEquals("0,2,3", pref);
    }

    public void testRestoreSelectionState() {
        TreeSettings.restoreSelectionState(tree, "0,2,3");

        assertEquals(5, tree.getRowCount());
        assertTrue(tree.isRowSelected(0));
        assertFalse(tree.isRowSelected(1));
        assertTrue(tree.isRowSelected(2));
        assertTrue(tree.isRowSelected(3));
        assertFalse(tree.isRowSelected(4));
    }

    public void testRestoreSelectionStateWithInvalidSettingsPref() {
        TreeSettings.restoreSelectionState(tree, "invalidPref");

        assertEquals(5, tree.getRowCount());
        assertEquals(0, tree.getSelectionCount());
    }

    public void testSaveExpansionState() {
        String pref = TreeSettings.saveExpansionState(tree);
        assertEquals("1,0,0,0,0", pref);

        // expand child2
        tree.expandRow(2);

        pref = TreeSettings.saveExpansionState(tree);
        assertEquals("1,0,1,0,0,0,0", pref);
    }

    public void testRestoreExpansionState() {
        TreeSettings.restoreExpansionState(tree, "1,0,1,0,0,0,0");

        assertEquals(7, tree.getRowCount());
        assertTrue(tree.isExpanded(0));
        assertFalse(tree.isExpanded(1));
        assertTrue(tree.isExpanded(2));
        assertFalse(tree.isExpanded(3));
        assertFalse(tree.isExpanded(4));
        assertFalse(tree.isExpanded(5));
        assertFalse(tree.isExpanded(6));
    }

    public void testRestoreExpansionStateWithInvalidSettingsString() {
        TreeSettings.restoreExpansionState(tree, "invalidPref");

        assertEquals(5, tree.getRowCount());
        assertTrue(tree.isExpanded(0));
        assertFalse(tree.isExpanded(1));
        assertFalse(tree.isExpanded(2));
        assertFalse(tree.isExpanded(3));
        assertFalse(tree.isExpanded(4));
    }

    protected void setUp() throws Exception {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
        child1.add(new DefaultMutableTreeNode("child1.1"));
        root.add(child1);

        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("child2");
        child2.add(new DefaultMutableTreeNode("child2.1"));
        child2.add(new DefaultMutableTreeNode("child2.2"));
        root.add(child2);
        root.add(new DefaultMutableTreeNode("child3"));
        root.add(new DefaultMutableTreeNode("child4"));

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
    }
}