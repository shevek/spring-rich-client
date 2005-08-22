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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.springframework.richclient.form.Form;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.richclient.tree.TreeUtils;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.util.Assert;

/**
 * A concrete implementation of <code>CompositeDialogPage</code> that presents
 * the child pages in a tree on the left, and the pages itself on the right.
 * <p>
 * When the user selects a page in the tree, it is shown on the right.
 * <p>
 * This class also decorates the entries in the tree to indicate the page
 * completed status.
 * 
 * @author Peter De Bruycker
 * @author Oliver Hutchison
 */
public class TreeCompositeDialogPage extends CompositeDialogPage {

    private static final DialogPage ROOT_PAGE = null;

    private CardLayout cardLayout;

    private DefaultTreeCellRenderer treeCellRenderer = new FocusableTreeCellRenderer() {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.getUserObject() instanceof DialogPage) {
                DialogPage page = (DialogPage)node.getUserObject();

                this.setText(decoratePageTitle(page));
                this.setIcon(null);
            }

            return this;
        }
    };

    private DefaultTreeModel pageTreeModel;

    private JPanel pagePanel;

    private JTree pageTree;

    private Map nodes;

    /**
     * Constructs a new <code>TreeCompositeDialogPage</code> instance.
     * 
     * @param pageId
     *            the pageId
     */
    public TreeCompositeDialogPage(String pageId) {
        super(pageId);
        nodes = new HashMap();
        nodes.put(ROOT_PAGE, new DefaultMutableTreeNode("pages"));
    }

    /**
     * Adds a DialogPage to the tree. The page will be added at the top level of
     * the tree hierarchy.
     * 
     * @param page
     *            the page to add
     */
    public void addPage(DialogPage page) {
        addPage(ROOT_PAGE, page);
    }

    /**
     * Adds a new page to the tree. The page is created by wrapping the form
     * page in a FormBackedDialogPage.
     * 
     * @param parent
     *            the parent page in the tree hierarchy
     * @param formPage
     *            the form page to be insterted
     * @return the DialogPage that wraps form
     */
    public DialogPage addForm(DialogPage parent, Form form) {
        DialogPage page = createDialogPage(form);
        addPage(parent, page);
        return page;
    }

    /**
     * Adds a DialogPage to the tree.
     * 
     * @param parent
     *            the parent page in the tree hierarchy
     * @param page
     *            the page to add
     */
    public void addPage(DialogPage parent, DialogPage child) {
        DefaultMutableTreeNode parentNode = getNode(parent);
        Assert.notNull(parentNode, "Parent dialog page must have been added before child");
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        parentNode.add(childNode);
        nodes.put(child, childNode);
        super.addPage(child);
    }

    /**
     * Adds a group DialogPages to the tree.
     * 
     * @param parent
     *            the parent page in the tree hierarchy
     * @param pages
     *            the pages to add
     */
    public void addPages(DialogPage parent, DialogPage[] pages) {
        for (int i = 0; i < pages.length; i++) {
            addPage(parent, pages[i]);
        }
    }

    /**
     * Expands or collapses all of the tree nodes.
     * 
     * @param expand
     *            when true expand all nodes; otherwise collapses all nodes
     */
    public void expandAll(boolean expand) {
        if (!isControlCreated()) {
            getControl();
        }
        TreeUtils.expandAll(pageTree, expand);
    }

    /**
     * Expands or collapses a number of levels of tree nodes.
     * 
     * @param levels
     *            the number of levels to expand/collapses
     * @param expand
     *            when true expand all nodes; otherwise collapses all nodes
     */
    public void expandLevels(int levels, boolean expand) {
        if (!isControlCreated()) {
            getControl();
        }
        TreeUtils.expandLevels(pageTree, levels, expand);
    }

    /**
     * @see org.springframework.richclient.dialog.AbstractDialogPage#createControl()
     */
    protected JComponent createControl() {
        createPageControls();

        cardLayout = new CardLayout();
        pagePanel = new JPanel(cardLayout);

        List pages = getPages();
        for (Iterator i = pages.iterator(); i.hasNext();) {
            DialogPage page = (DialogPage)i.next();

            processDialogPage(page);
        }

        DefaultMutableTreeNode rootNode = getNode(null);
        pageTreeModel = new DefaultTreeModel(rootNode);

        createTreeControl();
        pageTree.setModel(pageTreeModel);

        if (rootNode.getChildCount() > 0) {
            pageTree.setSelectionInterval(0, 0);
        }

        return createContentControl();
    }

    private void processDialogPage(DialogPage page) {
        JComponent control = page.getControl();
        control.setPreferredSize(getLargestPageSize());
        pagePanel.add(control, page.getId());
    }

    private JPanel createContentControl() {
        TableLayoutBuilder panelBuilder = new TableLayoutBuilder();
        panelBuilder.cell(new JScrollPane(pageTree), "colSpec=150 rowSpec=fill:default:grow");
        panelBuilder.gapCol();
        panelBuilder.cell(pagePanel, "valign=top");
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
     * 
     * @param page
     *            the page
     * @return the title
     */
    protected String decoratePageTitle(DialogPage page) {
        return LabelUtils.htmlBlock(page.getTitle() + "<sup><font size=-3 color=red>"
                + (page.isPageComplete() ? "" : "*"));
    }

    protected void updatePageComplete(DialogPage page) {
        super.updatePageComplete(page);
        if (pageTreeModel != null) {
            pageTreeModel.nodeChanged(getNode(page));
        }
    }

    private DefaultMutableTreeNode getNode(DialogPage page) {
        return (DefaultMutableTreeNode)nodes.get(page);
    }

    private class PageSelector implements TreeSelectionListener {
        private TreePath currentSelection;

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)pageTree.getLastSelectedPathComponent();

            if (node == null) {
                pageTree.setSelectionPath(currentSelection);

                return;
            }
            currentSelection = e.getPath();

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
            DialogPage activePage = (DialogPage)selectedNode.getUserObject();
            cardLayout.show(pagePanel, activePage.getId());
            setActivePage(activePage);
        }
    }
}