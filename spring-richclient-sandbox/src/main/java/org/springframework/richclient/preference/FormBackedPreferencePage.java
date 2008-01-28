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

import javax.swing.JComponent;

import org.springframework.richclient.form.Form;
import org.springframework.util.Assert;

public abstract class FormBackedPreferencePage extends PreferencePage {

	private Form form;

	public FormBackedPreferencePage(String id) {
		super(id);
	}

    public FormBackedPreferencePage(String id, boolean autoconfigure) {
        super(id, autoconfigure);
    }

    protected final JComponent createContents() {
		form = createForm();
		Assert.notNull(form,
				"You must set the form before contents are created.");

		initPageValidationReporter();

		return form.getControl();
	}

	protected abstract Form createForm();

	public Form getForm() {
		return form;
	}

	protected void initPageValidationReporter() {
		form.newSingleLineResultsReporter(this);
        form.addGuarded(this);
	}

	public void onAboutToShow() {
		setEnabled(!form.hasErrors());
	}

	public void setEnabled(boolean enabled) {
		setPageComplete(enabled);
	}
}