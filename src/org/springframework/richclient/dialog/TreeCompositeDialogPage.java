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
package org.springframework.richclient.dialog;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.richclient.util.TablePanelBuilder;

/**
 * A concrete implementation of <code>CompositeDialogPage</code> that presents
 * the child pages in a tree on the left, and the pages itself on the right.
 * <p>
 * When the user selects a page in the tree, it is shown on the right.
 * <p>
 * This class also decorates the entries in the tree to indicate the page completed
 * status.
 * 
 * @author Peter De Bruycker
 */
public class TreeCompositeDialogPage extends CompositeDialogPage {
    private CardLayout cardLayout;
    private DefaultTreeCellRenderer treeCellRenderer = new FocusableTreeCellRenderer() {
        public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof DialogPage) {
                DialogPage page = (DialogPage) node.getUserObject();

                this.setText(decoratePageTitle(page));
                this.setIcon(null);
            }

            return this;
        }
    };

    private DefaultTreeModel pageTreeModel;
    private JPanel pagePanel;
    private JTree pageTree;

    /**
     * Constructs a new <code>TreeCompositeDialogPage</code> instance.
     * @param pageId the pageId
     */
    public TreeCompositeDialogPage(String pageId) {
        super(pageId);
    }

    /**
     * @see org.springframework.richclient.dialog.AbstractDialogPage#createControl()
     */
    protected JComponent createControl() {
        createPageControls();

        cardLayout = new CardLayout();
        pagePanel = new JPanel(cardLayout);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("pages");
        List pages = getPages();
        for (Iterator i = pages.iterator(); i.hasNext();) {
            DialogPage page = (DialogPage) i.next();

            processDialogPage(rootNode, page);
        }

        pageTreeModel = new DefaultTreeModel(rootNode);

        createTreeControl();
        pageTree.setModel(pageTreeModel);

        if (rootNode.getChildCount() > 0) {
            pageTree.setSelectionInterval(0, 0);
        }

        return createContentControl();
    }

    private void processDialogPage(DefaultMutableTreeNode root, DialogPage page) {
        DefaultMutableTreeNode pageNode = new DefaultMutableTreeNode(page);
        root.add(pageNode);

        JComponent control = page.getControl();
        control.setPreferredSize(getLargestPageSize());
        pagePanel.add(control, page.getId());
    }

    private JPanel createContentControl() {
        TablePanelBuilder panelBuilder = new TablePanelBuilder();
        panelBuilder.cell(new JScrollPane(pageTree), "colSpec=150 rowSpec=pref");
        panelBuilder.gapCol();
        panelBuilder.cell(pagePanel, "colSpec=pref valign=top");

        return panelBuilder.getPanel();
    }

    private void createTreeControl() {
        pageTree = new JTree();
        pageTree.setRootVisible(false);
        pageTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        pageTree.addTreeSelectionListener(new PageSelector());
        pageTree.setCellRenderer(treeCellRenderer);
        pageTree.setShowsRootHandles(true);
    }

    /**
     * Returns the decorated title.
     * @param page the page
     * @return the title
     */
    protected String decoratePageTitle(DialogPage page) {
        return LabelUtils.htmlBlock(
            page.getTitle() + "<sup><font size=-3 color=red>" + (page.isPageComplete() ? "" : "*"));
    }

    protected void updatePageComplete(DialogPage page) {
        super.updatePageComplete(page);
        pageTreeModel.reload();
    }

    private class PageSelector implements TreeSelectionListener {
        private TreePath currentSelection;

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) pageTree.getLastSelectedPathComponent();

            if (node == null) {
                pageTree.setSelectionPath(currentSelection);

                return;
            }
            currentSelection = e.getPath();

            DefaultMutableTreeNode selectedNode =
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            DialogPage activePage = (DialogPage) selectedNode.getUserObject();
            cardLayout.show(pagePanel, activePage.getId());
            setActivePage(activePage);
        }
    }
}
