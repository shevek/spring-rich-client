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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.springframework.binding.form.NestingFormModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.CompositeDialogPage;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.InputApplicationDialog;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.dialog.TreeCompositeDialogPage;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.richclient.preference.PreferenceStore;
import org.springframework.richclient.progress.TreeStatusBarUpdater;
import org.springframework.richclient.samples.petclinic.ui.preference.CompositeDialogPageType;
import org.springframework.richclient.samples.petclinic.ui.preference.PetClinicAppearance;
import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.richclient.util.PopupMenuMouseListener;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.util.Assert;
import org.springframework.util.closure.support.Block;

public class OwnerManagerView extends AbstractView implements ApplicationListener {

    private Clinic clinic;

    private String ownerLastName = "";

    private JTree ownersTree;

    private DefaultTreeModel ownersTreeModel;

    private RenameCommand renameCommand = new RenameCommand();

    private DeleteCommandExecutor deleteExecutor = new DeleteCommandExecutor();

    private PropertiesCommandExecutor propertiesExecutor = new PropertiesCommandExecutor();

    public void setClinic(Clinic clinic) {
        Assert.notNull(clinic, "The clinic property is required");
        this.clinic = clinic;
    }

    public void setLastNameToDisplay(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.DELETE, deleteExecutor);
        context.register(GlobalCommandIds.PROPERTIES, propertiesExecutor);
    }

    protected JComponent createControl() {
        JPanel view = new JPanel(new BorderLayout());
        createOwnerManagerTree();
        JScrollPane sp = new JScrollPane(ownersTree);
        view.add(sp, BorderLayout.CENTER);
        return view;
    }

    private void createOwnerManagerTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Owners");
        Collection owners = clinic.findOwners(ownerLastName);
        for (Iterator i = owners.iterator(); i.hasNext();) {
            rootNode.add(new DefaultMutableTreeNode(i.next()));
        }
        this.ownersTreeModel = new DefaultTreeModel(rootNode);
        this.ownersTree = new JTree(ownersTreeModel);
        ownersTree.setShowsRootHandles(true);
        ownersTree.addTreeSelectionListener(new TreeStatusBarUpdater(getStatusBar()) {

            public String getSelectedObjectName() {
                Owner selectedOwner = getSelectedOwner();
                if (selectedOwner != null) {
                    return selectedOwner.getFirstName() + " " + selectedOwner.getLastName();
                }
                else {
                    return "Owners";
                }
            }
        });
        ownersTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                updateCommands();
            }
        });
        ownersTree.addMouseListener(new PopupMenuMouseListener(createPopupContextMenu()));
        ownersTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && propertiesExecutor.isEnabled()) {
                    propertiesExecutor.execute();
                }
            }
        });
        ownersTree.setCellRenderer(getTreeCellRenderer());
        ownersTree.setRootVisible(true);
    }

    private Owner getSelectedOwner() {
        DefaultMutableTreeNode node = getSelectedOwnerNode();
        if (node != null) {
            return (Owner)node.getUserObject();
        }
        else {
            return null;
        }
    }

    private DefaultMutableTreeNode getSelectedOwnerNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)ownersTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Owner)) {
            return null;
        }
        else {
            return node;
        }
    }

    private void updateCommands() {
        int treeSelectionCount = ownersTree.getSelectionCount();
        if (treeSelectionCount == 0 || (treeSelectionCount == 1 && ownersTree.isRowSelected(0))) {
            renameCommand.setEnabled(false);
            deleteExecutor.setEnabled(false);
            propertiesExecutor.setEnabled(false);
        }
        else if (treeSelectionCount == 1) {
            renameCommand.setEnabled(true);
            deleteExecutor.setEnabled(true);
            propertiesExecutor.setEnabled(true);
        }
        else if (treeSelectionCount > 1) {
            renameCommand.setEnabled(false);
            deleteExecutor.setEnabled(true);
            propertiesExecutor.setEnabled(false);
        }
    }

    private DefaultTreeCellRenderer treeCellRenderer = new FocusableTreeCellRenderer() {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.isRoot()) {
                this.setIcon(getIconSource().getIcon("folder.icon"));
            }
            else {
                Owner o = (Owner)node.getUserObject();
                this.setText(o.getFirstName() + " " + o.getLastName());
                this.setIcon(getIconSource().getIcon("owner.bullet"));
            }
            return this;
        }
    };

    public TreeCellRenderer getTreeCellRenderer() {
        return treeCellRenderer;
    }

    private JPopupMenu createPopupContextMenu() {
        // rename, separator, delete, properties
        CommandGroup group = getWindowCommandManager().createCommandGroup("ownerCommandGroup",
                new Object[] {renameCommand, "separator", "deleteCommand", "separator", "propertiesCommand"});
        return group.createPopupMenu();
    }

    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof LifecycleApplicationEvent) {
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Owner.class)) {
                if (ownersTree != null) {
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)ownersTreeModel.getRoot();
                    root.add(new DefaultMutableTreeNode(le.getObject()));
                    ownersTreeModel.nodeStructureChanged(root);
                }
            }
        }
    }

    private class RenameCommand extends ActionCommand {
        public RenameCommand() {
            super("renameCommand");
        }

        protected void doExecuteCommand() {
            final Owner owner = getSelectedOwner();
            InputApplicationDialog renameDialog = new InputApplicationDialog(owner, "firstName");
            renameDialog.setTitle(getMessage("renameOwnerDialog.title"));
            renameDialog.setInputLabelMessage("renameDialog.label");
            renameDialog.setParent(getWindowControl());
            renameDialog.setFinishAction(new Block() {

                public void handle(Object o) {
                    clinic.storeOwner(owner);
                    getSelectedOwnerNode().setUserObject(owner);
                    ownersTreeModel.nodeChanged(getSelectedOwnerNode());
                }
            });
            renameDialog.showDialog();
        }
    }

    private Owner reloadSelectedOwner() {
        int ownerId = getSelectedOwner().getId();
        return clinic.loadOwner(ownerId);
    }

    private class DeleteCommandExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            ConfirmationDialog dialog = new ConfirmationDialog() {
                protected void onConfirm() {
                    TreePath[] paths = ownersTree.getSelectionPaths();
                    for (int i = 0; i < paths.length; i++) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                        if (node.isRoot()) {
                            continue;
                        }
                        Owner owner = (Owner)node.getUserObject();
                        // clinic.deleteOwner(owner);
                        ownersTreeModel.removeNodeFromParent(node);
                    }
                }
            };
            dialog.setTitle("Delete Owner(s)");
            dialog.setConfirmationMessage(getMessage("confirmDeleteOwnerDialog.label"));
            dialog.showDialog();
        }
    }

    private class PropertiesCommandExecutor extends AbstractActionCommandExecutor {

        private NestingFormModel ownerFormModel;

        private OwnerGeneralForm ownerGeneralForm;

        private CompositeDialogPage compositePage;

        public void execute() {
            final Owner owner = getSelectedOwner();
            ownerFormModel = SwingFormModel.createCompoundFormModel(owner);
            ownerGeneralForm = new OwnerGeneralForm(ownerFormModel);

            PreferenceStore ps = (PreferenceStore)getApplicationContext().getBean("preferenceStore");
            CompositeDialogPageType type = (CompositeDialogPageType)ps.getLabeledEnum(PetClinicAppearance.DIALOG_PAGE_TYPE);
            if (type == null || type.equals(CompositeDialogPageType.TABBED)) {
                compositePage = new TabbedDialogPage("ownerProperties");
            }
            else {
                compositePage = new TreeCompositeDialogPage("ownerProperties");
            }
            compositePage.addForm(ownerGeneralForm);
            compositePage.addForm(new OwnerAddressForm(ownerFormModel));

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(compositePage, getWindowControl()) {
                protected void onAboutToShow() {
                    ownerGeneralForm.requestFocusInWindow();
                    setEnabled(compositePage.isPageComplete());
                }

                protected boolean onFinish() {
                    ownerFormModel.commit();
                    clinic.storeOwner(owner);
                    ownersTreeModel.nodeChanged(getSelectedOwnerNode());
                    return true;
                }
            };
            dialog.showDialog();
        }
    }

}