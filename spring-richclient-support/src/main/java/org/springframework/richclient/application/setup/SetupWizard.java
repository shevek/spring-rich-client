package org.springframework.richclient.application.setup;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import org.springframework.core.io.Resource;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.WizardDialog;

/**
 * @author Claudio Romano
 * @author Keith Donald
 */
public class SetupWizard extends AbstractWizard implements ActionCommandExecutor {
    private WizardDialog wizardDialog;

    private SetupLicenseWizardPage licensePage = new SetupLicenseWizardPage();

    public SetupWizard() {
        super("setup");
    }

    public void setLicenseTextLocation(Resource location) {
        licensePage.setLicenseTextLocation(location);
    }

    public void execute() {
        if (wizardDialog == null) {
            wizardDialog = new SetupWizardDialog(this);
            wizardDialog.setPreferredSize(new Dimension(500, 300));
        }
        wizardDialog.showDialog();
    }

    public void addPages() {
        addPage(new SetupIntroWizardPage());
        addPage(licensePage);
    }

    public boolean onFinish() {
        return true;
    }

    public boolean onCancel() {
        if (cancelConfirmed()) {
            // TODO use org.springframework.richclient.application.Application.close(b, i) instead (if initialized?)
            System.exit(1);
        }
        return false;
    }

    protected boolean cancelConfirmed() {
        return JOptionPane.showConfirmDialog(wizardDialog.getDialog(), getCancelMessage(), getCancelTitle(),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.NO_OPTION;
    }

    protected String getCancelTitle() {
        return getMessage("setup.cancel.title");
    }

    protected String getCancelMessage() {
        return getMessage("setup.cancel.message");
    }

}