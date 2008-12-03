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
package com.jidesoft.spring.richclient.googledemo.command;

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

import com.jidesoft.swing.JideSwingUtilities;

import com.jidesoft.spring.richclient.googledemo.preferences.SettingsDialog;

/**
 * Command that brings up the preferencs dialog
 * 
 * @author Jonny Wray
 *
 */
public class PreferencesCommand extends ApplicationWindowAwareCommand {
	private static final String ID = "preferencesCommand";
	
	public PreferencesCommand() {
        super(ID);
    }
	
    protected void doExecuteCommand() {
    	SettingsDialog dialog = new SettingsDialog(getParentWindowControl(), "Preferences");
    	dialog.pack();
    	JideSwingUtilities.globalCenterWindow(dialog);
    	dialog.setVisible(true);
    }

}
