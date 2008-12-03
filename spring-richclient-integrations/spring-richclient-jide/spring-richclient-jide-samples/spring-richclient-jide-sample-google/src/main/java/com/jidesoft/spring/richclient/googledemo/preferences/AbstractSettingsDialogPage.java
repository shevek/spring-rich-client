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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.ApplicationServicesLocator;

import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.swing.PartialEtchedBorder;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;

/**
 * Abstract super class containing common implementation
 * code for dialogs internal to multipage dialog from JIDE. 
 * 
 * @author Jonny Wray
 *
 */
public abstract class AbstractSettingsDialogPage extends AbstractDialogPage {
	
	protected abstract String getFormComponentLabel();
	protected abstract JComponent getFormComponentControl();
	protected abstract void applyChanges();
	
	protected String getMessage(String key){
		MessageSource messageSource = (MessageSource)ApplicationServicesLocator.services().getService(MessageSource.class);
		return messageSource.getMessage(key, new Object[]{}, Locale.getDefault());
	}
	
	public void lazyInitialize() {
		setLayout(new BorderLayout());
		add(getTitleComponent(), BorderLayout.NORTH);
		add(getFormComponent(), BorderLayout.CENTER);
	}
	

	private JComponent getTitleComponent(){
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 10, 10, 0, 10),
				new PartialEtchedBorder(EtchedBorder.LOWERED, PartialEtchedBorder.SOUTH)));
		titlePanel.setLayout(new BorderLayout());
		StyledLabel label = new StyledLabel(getTitle());
		label.setStyleRanges(new StyleRange[]{new StyleRange(Font.BOLD, Color.BLACK)});
		titlePanel.add(label, BorderLayout.WEST);
		return titlePanel;
	}
	

	private JComponent getFormComponent(){
		JPanel formPanel = new JPanel();
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		formPanel.setLayout(new BorderLayout());
		JPanel title = new JPanel();
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		title.setLayout(new BorderLayout());
		StyledLabel titleLabel = new StyledLabel(getFormComponentLabel());
		titleLabel.setStyleRanges(new StyleRange[]{new StyleRange(Font.PLAIN, Color.BLACK)});
		title.add(titleLabel, BorderLayout.WEST);
		formPanel.add(title, BorderLayout.NORTH);
    	formPanel.add(getFormComponentControl(), BorderLayout.CENTER);
    	return formPanel;
	}
}
