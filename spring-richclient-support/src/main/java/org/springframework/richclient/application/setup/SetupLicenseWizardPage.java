/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.application.setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.springframework.core.io.Resource;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.ToggleCommand;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.text.HtmlPane;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.richclient.wizard.AbstractWizardPage;
import org.springframework.richclient.wizard.WizardPage;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * A {@link WizardPage} which shows a license text and confirmation radio
 * buttons at the bottom. The license text can be set by
 * {@link #setLicenseTextLocation(Resource)}.
 *
 * @author Claudio Romano
 */
public class SetupLicenseWizardPage extends AbstractWizardPage {
	/** Pane holding the license text. */
	private HtmlPane licenseTextPane;

	/** The license text. */
	private Resource licenseTextLocation;

	/**
	 * Default constructor. License Resource can be added later.
	 */
	public SetupLicenseWizardPage() {
		this(null);
	}

	/**
	 * Convenience constructor which sets the license resource.
	 *
	 * @see #setLicenseTextLocation(Resource)
	 */
	public SetupLicenseWizardPage(Resource licenseTextLocation) {
		super("license");
		setLicenseTextLocation(licenseTextLocation);
	}

	/**
	 * Set the {@link Resource} to use as license text.
	 */
	public final void setLicenseTextLocation(Resource location) {
		this.licenseTextLocation = location;
		if (licenseTextPane != null) {
			updateLicenseTextPane();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected JComponent createControl() {
		initLicenseTextPane();

		ToggleCommand acceptCommand = new ToggleCommand("acceptLicenseCommand") {
			protected void onSelection() {
				SetupLicenseWizardPage.this.setEnabled(true);
			}
		};

		ToggleCommand doNotAcceptCommand = new ToggleCommand("doNotAcceptLicenseCommand") {
			protected void onSelection() {
				SetupLicenseWizardPage.this.setEnabled(false);
			}
		};
		doNotAcceptCommand.setSelected(true);

		CommandGroup.createExclusiveCommandGroup(new ToggleCommand[] { acceptCommand, doNotAcceptCommand });

		GridBagLayoutBuilder formBuilder = new GridBagLayoutBuilder();
		formBuilder.append(new JScrollPane(licenseTextPane), 1, 1, true, true);
		formBuilder.nextLine();
		formBuilder.append(acceptCommand.createRadioButton());
		formBuilder.nextLine();
		formBuilder.append(doNotAcceptCommand.createRadioButton());
		return formBuilder.getPanel();
	}

	/**
	 * Create the html pane and update its contents.
	 */
	protected void initLicenseTextPane() {
		this.licenseTextPane = new HtmlPane();
		updateLicenseTextPane();
	}

	/**
	 * Updates the text in the html pane.
	 */
	private void updateLicenseTextPane() {
		try {
			Assert.state(licenseTextLocation != null, "License text location is not set");
			String text = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(licenseTextLocation
					.getInputStream())));
			licenseTextPane.setText(LabelUtils.htmlBlock(text));
		}
		catch (IOException e) {
			final IllegalStateException exp = new IllegalStateException("License text not accessible: "
					+ e.getMessage());
			exp.setStackTrace(e.getStackTrace());
			throw exp;
		}
	}

}