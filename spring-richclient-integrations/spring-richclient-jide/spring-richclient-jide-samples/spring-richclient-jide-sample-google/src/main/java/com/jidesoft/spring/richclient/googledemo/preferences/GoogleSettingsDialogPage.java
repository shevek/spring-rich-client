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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.FormModelHelper;

import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;

/**
 * Concrete dialog page, for inclusion in a multipage dialog, for
 * obtaining a Google API key from the user
 * 
 * @author Jonny Wray
 *
 */
public class GoogleSettingsDialogPage extends AbstractSettingsDialogPage {

	private GoogleSettingsForm form;
	
	public GoogleSettingsDialogPage(){
		setTitle(getMessage("settings.google.title"));
	}

	protected String getFormComponentLabel(){
		return getMessage("settings.google.label");
	}
	
	protected JComponent getFormComponentControl(){

		GoogleSettingsBean bean = new GoogleSettingsBean();
    	FormModel formModel = FormModelHelper.createFormModel(bean);
    	form = new GoogleSettingsForm(formModel);
    	form.addFormValueChangeListener("googleKeyTextInput", new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				String newValue = (String)evt.getNewValue();
				if(newValue == null || newValue.trim().length() == 0){
					fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.APPLY);
					fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.OK);
				}
				else{
					fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.APPLY);
					fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.OK);
				}
			}
    		
    	});
    	return form.createFormControl();
	}
	
	protected void applyChanges(){
		if(form != null){
			form.commit();
			GoogleSettingsBean bean = (GoogleSettingsBean)form.getFormObject();
			bean.saveSettings();
		}
	}
}
