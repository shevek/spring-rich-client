package org.springframework.richclient.application.setup;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.components.GradientPanel;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.wizard.Wizard;
import org.springframework.richclient.wizard.WizardDialog;
import org.springframework.richclient.wizard.WizardPage;

/**
 * @author cro
 */
public class SetupWizardDialog extends WizardDialog {

    private Container pageControlBackup;

    private GradientPanel firstPageControl;

    private ActionCommand nextCommand;

    public SetupWizardDialog(Wizard wizard) {
        super(wizard);
        this.setTitle(getApplicationName());
        this.setResizable(false);
    }

    protected JComponent createDialogContentPane() {
        createFirstPageControl();
        return super.createDialogContentPane();
    }

    protected JComponent createFirstPageControl() {
        firstPageControl = new GradientPanel();
        firstPageControl.setLayout(new BorderLayout());
        firstPageControl.add(createFirstPageButtonBar(), BorderLayout.SOUTH);
        return firstPageControl;
    }

    protected JComponent createFirstPageButtonBar() {
        CommandGroup dialogCommandGroup = CommandGroup.createCommandGroup(null, getIntroPageCommandGroupMembers());
        JComponent buttonBar = dialogCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        buttonBar.setOpaque(false);
        return buttonBar;
    }

    protected Object[] getIntroPageCommandGroupMembers() {
        nextCommand = new ActionCommand("nextCommand") {
            public void doExecuteCommand() {
                onNext();
            }
        };

        return new AbstractCommand[] { nextCommand, getCancelCommand() };
    }

    public void showPage(WizardPage page) {
        if (page.getPreviousPage() == null) {
            // is intro page? --> better way to find it out?
            super.showPage(page);
            pageControlBackup = getDialogContentPane();
            firstPageControl.add(page.getControl(), BorderLayout.CENTER);
            this.getDialog().setContentPane(firstPageControl);
        }
        else {
            if (pageControlBackup != null) {
                getDialog().setContentPane(pageControlBackup);
                //stop adding the content pane in future
                pageControlBackup = null;
            }
            super.showPage(page);
        }
    }

}