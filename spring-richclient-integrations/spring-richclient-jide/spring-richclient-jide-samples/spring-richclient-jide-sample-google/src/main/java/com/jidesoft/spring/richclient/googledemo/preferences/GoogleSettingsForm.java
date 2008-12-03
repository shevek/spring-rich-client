/*
 * Copyright 2005 the original author or authors.
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
package com.jidesoft.spring.richclient.googledemo.preferences;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Form that obtained a google key from the user via
 * a text area.
 * 
 * @author Jonny Wray
 *
 */
public class GoogleSettingsForm extends AbstractForm {

	private static final String ID = "googleSettingsForm";
    protected JComponent textArea;
	
	public GoogleSettingsForm(FormModel formModel) {
        super(formModel, ID);
	}
	
	protected JComponent createFormControl(){

        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        textArea = formBuilder.add("googleKeyTextInput")[1];
        return formBuilder.getForm();
	}

    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();
    }
}
