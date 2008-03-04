package org.springframework.richclient.samples.showcase.wizard;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.setup.SetupLicenseWizardPage;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.AbstractWizardPage;

public class InstallWizard extends AbstractWizard {

	private Resource licenseResource;

	public void setLicenseResource(Resource licenseResource) {
		this.licenseResource = licenseResource;
	}

	public void addPages() {
		addPage(new SetupLicenseWizardPage(licenseResource));
		addPage(new DirectoryInputPage());
	}

	protected boolean onFinish() {
		return true;
	}

	private class DirectoryInputPage extends AbstractWizardPage {
		public DirectoryInputPage() {
			super("directoryInputPage");
		}

		protected JComponent createControl() {
			JPanel panel = new JPanel();
			panel.add(new JLabel("directory input"));
			return panel;
		}
	}
}
