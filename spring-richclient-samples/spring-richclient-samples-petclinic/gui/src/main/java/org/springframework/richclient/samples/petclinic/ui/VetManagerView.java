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
package org.springframework.richclient.samples.petclinic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.progress.TreeStatusBarUpdater;
import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Specialty;
import org.springframework.samples.petclinic.Vet;
import org.springframework.util.Assert;

public class VetManagerView extends AbstractView {

    private Clinic clinic;

    private JTree vetsTree;

    private DefaultTreeModel vetsTreeModel;

    public void setClinic(Clinic clinic) {
        Assert.notNull(clinic, "The clinic property is required");
        this.clinic = clinic;
    }

    protected JComponent createControl() {
        JPanel view = new JPanel(new BorderLayout());
        createVetManagerTree();
        JScrollPane sp = new JScrollPane(vetsTree);
        view.add(sp, BorderLayout.CENTER);
        return view;
    }

    private void createVetManagerTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Vets");
        Collection vets = clinic.getVets();
        for (Iterator i = vets.iterator(); i.hasNext();) {
            Vet vet = (Vet)i.next();
            DefaultMutableTreeNode vetNode = new DefaultMutableTreeNode(vet);
            for (Iterator s = vet.getSpecialties().iterator(); s.hasNext();) {
                Specialty specialty = (Specialty)s.next();
                vetNode.add(new DefaultMutableTreeNode(specialty));
            }
            rootNode.add(vetNode);
        }
        this.vetsTreeModel = new DefaultTreeModel(rootNode);
        this.vetsTree = new JTree(vetsTreeModel);
        vetsTree.setShowsRootHandles(true);
        vetsTree.addTreeSelectionListener(new TreeStatusBarUpdater(getStatusBar()) {
            public String getSelectedObjectName() {
                Vet selectedVet = getSelectedVet();
                if (selectedVet != null)
                    return selectedVet.getFirstName() + " " + selectedVet.getLastName();

                return "Vets";
            }
        });
        vetsTree.setCellRenderer(getTreeCellRenderer());
        vetsTree.setRootVisible(true);
    }

    private Vet getSelectedVet() {
        DefaultMutableTreeNode node = getSelectedVetNode();
        if (node != null)
            return (Vet)node.getUserObject();

        return null;
    }

    private DefaultMutableTreeNode getSelectedVetNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)vetsTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Vet))
            return null;

        return node;
    }

    private DefaultTreeCellRenderer treeCellRenderer = new FocusableTreeCellRenderer() {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.isRoot()) {
                this.setIcon(getIconSource().getIcon("folder.icon"));
            }
            else if (node.getUserObject() instanceof Vet) {
                Vet o = (Vet)node.getUserObject();
                this.setText(o.getFirstName() + " " + o.getLastName());
                this.setIcon(getIconSource().getIcon("owner.bullet"));
            }
            else {
                Specialty o = (Specialty)node.getUserObject();
                this.setText(o.getName());
                this.setIcon(getIconSource().getIcon("specialty.bullet"));
            }
            return this;
        }
    };

    public TreeCellRenderer getTreeCellRenderer() {
        return treeCellRenderer;
    }

}