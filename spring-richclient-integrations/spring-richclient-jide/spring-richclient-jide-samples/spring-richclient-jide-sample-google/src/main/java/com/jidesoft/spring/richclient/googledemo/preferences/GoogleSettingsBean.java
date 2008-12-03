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

import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * Bean that acts as a backer for the google key form
 * 
 * @author Jonny Wray
 *
 */
public class GoogleSettingsBean {

	private String googleKeyTextInput = null;

	private GoogleSettingsManager preferencesManager = 
		(GoogleSettingsManager)ApplicationServicesLocator.services().getService(GoogleSettingsManager.class);
	
	public GoogleSettingsBean(){
		googleKeyTextInput = preferencesManager.getGoogleKey();
	}
	
	public void setGoogleKeyTextInput(String googleKeyTextInput){
		this.googleKeyTextInput = googleKeyTextInput;
	}
	
	public String getGoogleKeyTextInput(){
		return googleKeyTextInput;
	}
	
	/**
	 * Saves the current status of the list of article sources
	 *
	 */
	public void saveSettings(){
		if(googleKeyTextInput != null ){
			preferencesManager.saveGoogleKey(googleKeyTextInput);
		}
	}
}
