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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.ExclusiveCommandGroup;
import org.springframework.richclient.command.ToggleCommand;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.text.HtmlPane;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.richclient.wizard.AbstractWizardPage;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * @author Claudio Romano
 */
public class SetupLicenseWizardPage extends AbstractWizardPage {

    private ExclusiveCommandGroup licenseAcceptGroup;

    private HtmlPane licenseTextPane;

    private Resource licenseTextLocation;

    public SetupLicenseWizardPage() {
        this(null);
    }

    public SetupLicenseWizardPage(Resource licenseTextLocation) {
        super("license");
        setLicenseTextLocation(licenseTextLocation);
    }

    public void setLicenseTextLocation(Resource location) {
        this.licenseTextLocation = location;
        if (licenseTextPane != null) {
            updateLicenseTextPane();
        }
    }

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

    protected void initLicenseTextPane() {
        this.licenseTextPane = new HtmlPane();
        updateLicenseTextPane();
    }

    private void updateLicenseTextPane() {
        try {
            Assert.state(licenseTextLocation != null, "License text location is not set");
            String text = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(licenseTextLocation
                    .getInputStream())));
            licenseTextPane.setText(LabelUtils.htmlBlock(text));
        }
        catch (IOException e) {
            throw new DataAccessResourceFailureException("License text not accessible", e);
        }
    }

}