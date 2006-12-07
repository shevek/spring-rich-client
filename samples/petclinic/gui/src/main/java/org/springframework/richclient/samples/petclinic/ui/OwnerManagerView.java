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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.closure.support.Block;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.CompositeDialogPage;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.InputApplicationDialog;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.progress.TreeStatusBarUpdater;
import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.richclient.util.PopupMenuMouseListener;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.jdbc.JdbcPet;
import org.springframework.util.Assert;

public class OwnerManagerView extends AbstractView implements ApplicationListener {

    private Clinic clinic;

    private String ownerLastName = "";

    private JTree ownersTree;

    private DefaultTreeModel ownersTreeModel;

    private RenameExecutor renameExecutor = new RenameExecutor();

    private DeleteExecutor deleteExecutor = new DeleteExecutor();

    private PropertiesExecutor propertiesExecutor = new PropertiesExecutor();

    private RenameOwnerExecutor renameOwnerExecutor = new RenameOwnerExecutor();

    private RenamePetExecutor renamePetExecutor = new RenamePetExecutor();

    private OwnerPropertiesExecutor ownerPropertiesExecutor = new OwnerPropertiesExecutor();

    private PetPropertiesExecutor petPropertiesExecutor = new PetPropertiesExecutor();

    private NewPetAction newPetCommand = new NewPetAction();

    public void setClinic(Clinic clinic) {
        Assert.notNull(clinic, "The clinic property is required");
        this.clinic = clinic;
    }

    public void setLastNameToDisplay(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("renameCommand", renameExecutor);
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
            Owner owner = (Owner)i.next();
            DefaultMutableTreeNode ownerNode = new DefaultMutableTreeNode(owner);
            for (Iterator j = owner.getPets().iterator(); j.hasNext();) {
                ownerNode.add(new DefaultMutableTreeNode(j.next()));
            }
            rootNode.add(ownerNode);
        }
        this.ownersTreeModel = new DefaultTreeModel(rootNode);
        this.ownersTree = new JTree(ownersTreeModel);
        ownersTree.setShowsRootHandles(true);
        ownersTree.addTreeSelectionListener(new TreeStatusBarUpdater(getStatusBar()) {
            public String getSelectedObjectName() {
                Owner selectedOwner = getSelectedOwner();
                if (selectedOwner != null)
                    return selectedOwner.getFirstName() + " " + selectedOwner.getLastName();

                return "Owners";
            }
        });
        ownersTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                updateCommands();
            }
        });
        ownersTree.addMouseListener(new PopupMenuMouseListener() {
            protected boolean onAboutToShow(MouseEvent e) {
                return !isRootOrNothingSelected();
            }

            protected JPopupMenu getPopupMenu() {
                return getSelectedOwner() != null ? createOwnerPopupContextMenu() : createPetPopupContextMenu();
            }
        });
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
        if (node != null)
            return (Owner)node.getUserObject();

        return null;
    }

    private DefaultMutableTreeNode getSelectedOwnerNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)ownersTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Owner))
            return null;

        return node;
    }

    private Pet getSelectedPet() {
        DefaultMutableTreeNode node = getSelectedPetNode();
        if (node != null)
            return (Pet)node.getUserObject();

        return null;
    }

    private DefaultMutableTreeNode getSelectedPetNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)ownersTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Pet))
            return null;

        return node;
    }

    private void updateCommands() {
        int treeSelectionCount = ownersTree.getSelectionCount();
        if (isRootOrNothingSelected()) {
            renameExecutor.setEnabled(false);
            deleteExecutor.setEnabled(false);
            propertiesExecutor.setEnabled(false);
        }
        else if (treeSelectionCount == 1) {
            renameExecutor.setEnabled(true);
            deleteExecutor.setEnabled(true);
            propertiesExecutor.setEnabled(true);
        }
        else if (treeSelectionCount > 1) {
            renameExecutor.setEnabled(false);
            deleteExecutor.setEnabled(true);
            propertiesExecutor.setEnabled(false);
        }
        newPetCommand.setEnabled(getSelectedOwner() != null);
    }

    private boolean isRootOrNothingSelected() {
        return ownersTree.getSelectionCount() == 0
                || (ownersTree.getSelectionCount() == 1 && ownersTree.isRowSelected(0));
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
                Object userObject = node.getUserObject();
                if (userObject instanceof Owner) {
                    Owner o = (Owner)userObject;
                    this.setText(o.getFirstName() + " " + o.getLastName());
                    this.setIcon(getIconSource().getIcon("owner.bullet"));
                }
                else if (userObject instanceof Pet) {
                    Pet p = (Pet)userObject;
                    this.setText("<html>" + p.getName() + " <i>(" + p.getType().getName() + ")");
                    this.setIcon(getIconSource().getIcon("pet.bullet"));
                }
            }
            return this;
        }
    };

    public TreeCellRenderer getTreeCellRenderer() {
        return treeCellRenderer;
    }

    private JPopupMenu createOwnerPopupContextMenu() {
        // rename, separator, delete, addPet separator, properties
        CommandGroup group = getWindowCommandManager().createCommandGroup(
                "ownerViewTableOwnerCommandGroup",
                new Object[] {"renameCommand", "separator", "deleteCommand", "separator", newPetCommand, "separator",
                        "propertiesCommand"});
        return group.createPopupMenu();
    }

    private JPopupMenu createPetPopupContextMenu() {
        // rename, separator, delete, separator, properties
        CommandGroup group = getWindowCommandManager().createCommandGroup("ownerViewTablePetCommandGroup",
                new Object[] {"renameCommand", "separator", "deleteCommand", "separator", "propertiesCommand"});
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

    private class RenameExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            if (getSelectedOwner() != null) {
                renameOwnerExecutor.execute();
            }
            else {
                renamePetExecutor.execute();
            }
        }
    }

    private class PropertiesExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            if (getSelectedOwner() != null) {
                ownerPropertiesExecutor.execute();
            }
            else {
                petPropertiesExecutor.execute();
            }
        }
    }

    private class DeleteExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            int ownerCount = 0;
            int petCount = 0;
            final List nodesToDelete = new ArrayList();
            TreePath[] paths = ownersTree.getSelectionPaths();
            for (int i = 0; i < paths.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                if (node.isRoot()) {
                    continue;
                }
                if (node.getUserObject() instanceof Owner) {
                    ownerCount++;
                }
                else {
                    petCount++;
                }
                nodesToDelete.add(node);
            }

            ConfirmationDialog dialog = new ConfirmationDialog() {
                protected void onConfirm() {
                    for (Iterator i = nodesToDelete.iterator(); i.hasNext();) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)i.next();
                        ownersTreeModel.removeNodeFromParent(node);
                        if (node.getUserObject() instanceof Owner) {
                            // clinic.deleteOwner((Owner) node.getUserObject());
                        }
                        else {
                            // clinic.deletePet((Pet) node.getUserObject());
                        }
                    }
                }
            };
            if (ownerCount > 0 && petCount > 0) {
                dialog.setTitle(getMessage("confirmDeleteOwnerAndPetDialog.title"));
                dialog.setConfirmationMessage(getMessage("confirmDeleteOwnerAndPetDialog.label"));
            }
            else if (ownerCount > 0) {
                dialog.setTitle(getMessage("confirmDeleteOwnerDialog.title"));
                dialog.setConfirmationMessage(getMessage("confirmDeleteOwnerDialog.label"));
            }
            else {
                dialog.setTitle(getMessage("confirmDeletePetDialog.title"));
                dialog.setConfirmationMessage(getMessage("confirmDeletePetDialog.label"));
            }

            dialog.showDialog();
        }
    }

    private class RenameOwnerExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            final Owner owner = getSelectedOwner();
            InputApplicationDialog renameDialog = new InputApplicationDialog(owner, "firstName");
            renameDialog.setTitle(getMessage("renameOwnerDialog.title"));
            renameDialog.setInputLabelMessage("renameOwnerDialog.label");
            renameDialog.setParentComponent(getWindowControl());
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

    private class RenamePetExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            final Pet pet = getSelectedPet();
            InputApplicationDialog renameDialog = new InputApplicationDialog(pet, "name");
            renameDialog.setTitle(getMessage("renamePetDialog.title"));
            renameDialog.setInputLabelMessage("renamePetDialog.label");
            renameDialog.setParentComponent(getWindowControl());
            renameDialog.setFinishAction(new Block() {

                public void handle(Object o) {
                    clinic.storePet(pet);
                    getSelectedPetNode().setUserObject(pet);
                    ownersTreeModel.nodeChanged(getSelectedPetNode());
                }
            });
            renameDialog.showDialog();
        }
    }

    private class OwnerPropertiesExecutor extends AbstractActionCommandExecutor {

        private HierarchicalFormModel ownerFormModel;

        private OwnerGeneralForm ownerGeneralForm;

        private CompositeDialogPage compositePage;

        public void execute() {
            final Owner owner = getSelectedOwner();
            ownerFormModel = FormModelHelper.createCompoundFormModel(owner);
            ownerGeneralForm = new OwnerGeneralForm(FormModelHelper.createChildPageFormModel(ownerFormModel, null));

            compositePage = new TabbedDialogPage("ownerProperties");
            compositePage.addForm(ownerGeneralForm);
            compositePage.addForm(new OwnerAddressForm(FormModelHelper.createChildPageFormModel(ownerFormModel, null)));

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

    private class PetPropertiesExecutor extends AbstractActionCommandExecutor {

        public void execute() {
            final Pet pet = getSelectedPet();
            final PetForm petForm = new PetForm(FormModelHelper.createFormModel(pet), false);
            final FormBackedDialogPage dialogPage = new FormBackedDialogPage(petForm);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(dialogPage, getWindowControl()) {
                protected void onAboutToShow() {
                    petForm.requestFocusInWindow();
                    setEnabled(dialogPage.isPageComplete());
                }

                protected boolean onFinish() {
                    petForm.commit();
                    clinic.storePet(pet);
                    ownersTreeModel.nodeChanged(getSelectedPetNode());
                    return true;
                }
            };
            dialog.showDialog();
        }
    }

    private class NewPetAction extends ActionCommand {

        public NewPetAction() {
            super("newPetCommand");
        }

        protected void doExecuteCommand() {
            final Pet newPet = new JdbcPet();
            final PetForm petForm = new PetForm(FormModelHelper.createFormModel(newPet), true);
            final FormBackedDialogPage dialogPage = new FormBackedDialogPage(petForm);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(dialogPage, getWindowControl()) {
                protected void onAboutToShow() {
                    petForm.requestFocusInWindow();
                    setEnabled(dialogPage.isPageComplete());
                }

                protected boolean onFinish() {
                    petForm.commit();
                    getSelectedOwner().addPet(newPet);
                    clinic.storePet(newPet);
                    DefaultMutableTreeNode ownerNode = getSelectedOwnerNode();
                    ownerNode.add(new DefaultMutableTreeNode(newPet));
                    ownersTreeModel.nodeStructureChanged(ownerNode);
                    return true;
                }
            };
            dialog.showDialog();

        }
    }
    
    public void componentClosed() {
    	System.out.println("closed");
    }
    
    public void componentFocusGained() {
    	System.out.println("gained");
    }
    
    public void componentFocusLost() {
    	System.out.println("lost");
    }
    
    public void componentOpened() {
    	System.out.println("opened");
    }

}