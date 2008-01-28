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
package org.springframework.richclient.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.dialog.TreeCompositeDialogPage;
import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;

public class PreferenceDialog extends TitledPageApplicationDialog {

	private List preferencePages = new ArrayList();

	private Settings settings;

    public PreferenceDialog() {
        this("preferenceDialog");
    }

    public PreferenceDialog(String dialogId) {
		super(new TreeCompositeDialogPage(dialogId));
	}

	private void addPage(PreferencePage page) {
		Assert.isTrue(!isControlCreated(), "Add pages before control is created.");
		preferencePages.add(page);
		page.setPreferenceDialog(this);
	}

	public void addPreferencePage(PreferencePage page) {
		addPage(page);
		getPageContainer().addPage(page);
	}

	public void addPreferencePage(PreferencePage parent, PreferencePage page) {
		addPage(page);
		getPageContainer().addPage(parent, page);
	}

	private TreeCompositeDialogPage getPageContainer() {
		return (TreeCompositeDialogPage) getDialogPage();
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean onFinish() {
		for (Iterator iter = preferencePages.iterator(); iter.hasNext();) {
			PreferencePage page = (PreferencePage) iter.next();
			// give page the chance to veto
			if (!page.onFinish()) {
				return false;
			}
		}
		if (settings != null) {
			try {
				settings.save();
			} catch (IOException e) {
				// TODO handle exception
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public void setSettings(Settings settings) {
		Assert.notNull(settings, "Settings cannot be null.");
		this.settings = settings;
	}
}