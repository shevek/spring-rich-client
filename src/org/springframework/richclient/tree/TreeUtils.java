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
package org.springframework.richclient.tree;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeUtils {
    public static String buildFormattedTreePath(TreeObject treeObject, boolean includeRoot, String delimSymbol) {
        if (treeObject == null) {
            return "";
        }
        TreeObject parent = treeObject;
        if (parent == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        if (delimSymbol == null) {
            delimSymbol = "/";
        }
        boolean firstTime = true;
        while (parent != null) {
            TreeObject p = parent.getParent();
            if (p == null) {
                if (includeRoot) {
                    buffer.insert(0, "/" + parent.getDisplayName() + "/");
                }
            }
            else {
                if (firstTime) {
                    buffer.insert(0, parent.getDisplayName());
                    firstTime = false;
                }
                else {
                    buffer.insert(0, parent.getDisplayName() + delimSymbol);
                }
            }
            parent = p;
        }
        return buffer.toString();
    }

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public static void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public static void expandLevels(JTree tree, int levels, boolean expand) {
        TreeModel model = tree.getModel();
        if (model == null) {
            return;
        }
        TreeNode root = (TreeNode)model.getRoot();
        // Traverse tree from root
        expandLevels(tree, new TreePath(root), levels, expand);
    }

    public static void expandLevels(JTree tree, TreePath parent, int levels, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                if (levels > 0) {
                    expandLevels(tree, path, --levels, expand);
                }
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        }
        else {
            tree.collapsePath(parent);
        }
    }

    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        }
        else {
            tree.collapsePath(parent);
        }
    }

}