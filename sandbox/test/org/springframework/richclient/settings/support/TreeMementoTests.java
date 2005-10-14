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

import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.TransientSettings;

/**
 * @author Peter De Bruycker
 */
public class TreeMementoTests extends TestCase {

    private JTree tree;
	private TreeMemento memento;

    public void testConstructor() {
		try {
			new WindowMemento(null);
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// test passes
		}

		try {
			tree.setName(null);
			new TreeMemento(tree, "");
			fail("Should throw IllegalArgumentException: tree has no name");
		} catch (Exception e) {
			// test passes
		}

		tree.setName("tree0");

		TreeMemento memento = new TreeMemento(tree);
		assertEquals(tree, memento.getTree());
		assertEquals("tree0", memento.getKey());
		
		memento = new TreeMemento(tree, "key");
		assertEquals(tree, memento.getTree());
		assertEquals("key", memento.getKey());
	}
    
    public void testSaveSelectionState() {
        TransientSettings settings = new TransientSettings();

        memento.saveSelectionState(settings);
        assertFalse(settings.contains("tree.selectedRows"));

        tree.setSelectionRows(new int[] { 0, 2, 3 });
        memento.saveSelectionState(settings);
        assertTrue(settings.contains("tree.selectedRows"));
        assertEquals("0,2-3", settings.getString("tree.selectedRows"));
    }

    public void testRestoreSelectionState() {
        Settings settings = new TransientSettings();

        settings.setString("tree.selectedRows", "0,2-3");
        memento.restoreSelectionState(settings);

        assertEquals(5, tree.getRowCount());
        assertTrue(tree.isRowSelected(0));
        assertFalse(tree.isRowSelected(1));
        assertTrue(tree.isRowSelected(2));
        assertTrue(tree.isRowSelected(3));
        assertFalse(tree.isRowSelected(4));
    }

    public void testSaveExpansionState() {
        Settings settings = new TransientSettings();

        memento.saveExpansionState(settings);
        assertTrue(settings.contains("tree.expansionState"));
        assertEquals("1,0,0,0,0", settings.getString("tree.expansionState"));

        // expand child2
        tree.expandRow(2);

        memento.saveExpansionState(settings);
        assertTrue(settings.contains("tree.expansionState"));
        assertEquals("1,0,1,0,0,0,0", settings.getString("tree.expansionState"));
    }

    public void testRestoreExpansionState() {
        Settings settings = new TransientSettings();
        settings.setString("tree.expansionState", "1,0,1,0,0,0,0");

        memento.restoreExpansionState(settings);

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
        Settings settings = new TransientSettings();
        settings.setString("key.expansionState", "invalidPref");

        memento.restoreExpansionState(settings);

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
        
        memento = new TreeMemento(tree, "tree");
    }
}