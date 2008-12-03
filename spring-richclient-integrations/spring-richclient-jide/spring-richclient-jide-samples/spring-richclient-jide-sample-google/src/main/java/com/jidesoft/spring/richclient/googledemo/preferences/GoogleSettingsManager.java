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

import org.apache.log4j.Logger;

import java.io.IOException;
import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsException;
import org.springframework.richclient.settings.SettingsManager;

/**
 * The is a settings manager for choosing the Google key. This is
 * simply a few convience methods around the Spring RCP settings
 * manager.
 * 
 * @author Jonny Wray
 *
 */
public class GoogleSettingsManager  {
	private static final Logger log = Logger.getLogger(GoogleSettingsManager.class);

	private static final String DEFAULT_KEY = "55ZsNStQFHIlkwlkMRumecJRxI/BGlLC";
	private static final String GOOGLE_KEY = "googleKey";
	private SettingsManager settingsStoreManager;
	
	public void setSettingsStoreManager(SettingsManager settingsStoreManager){
		this.settingsStoreManager = settingsStoreManager;
	}

	public void saveGoogleKey(String googleKey){
		try{
			Settings userSettings = settingsStoreManager.getUserSettings();
			userSettings.setString(GOOGLE_KEY, googleKey);
			userSettings.save();
		}
		catch (SettingsException e) {
			log.warn("Unable to get user settings", e);
		} 
		catch (IOException e) {
			log.warn("Unable to save user settings", e);
		}
	}
	
	public String getGoogleKey(){
		try{
			Settings userSettings = settingsStoreManager.getUserSettings();
			if(userSettings.contains(GOOGLE_KEY)){
				return userSettings.getString(GOOGLE_KEY);
			}
			else{
				return DEFAULT_KEY;
			}
		}
		catch(SettingsException e){
			log.warn("Unable to create settings", e);
			return "";
		}
		
	}
}
