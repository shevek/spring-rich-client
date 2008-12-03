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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.MultiplePageDialog;
import com.jidesoft.dialog.PageList;
import com.jidesoft.swing.PartialEtchedBorder;

/**
 * Specific implementation of a multiple page dialog that
 * contains just one page with a dialog asking for the
 * Google API key.
 * 
 * @author Jonny Wray
 *
 */
public class SettingsDialog extends MultiplePageDialog {

	private static final Dimension PREFERRED_SIZE = new Dimension(600, 400);
	
	private List dialogPages = new ArrayList();
	 
	public SettingsDialog(Frame parent, String title){
		super(parent, title);
		setStyle(MultiplePageDialog.TAB_STYLE);
		//setStyle(MultiplePageDialog.LIST_STYLE);
		initializeDialogPages();
	}
	
	protected void initComponents(){
		super.initComponents();
	// Uncomment with a dialog style that has an index panel
		//getIndexPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		//getIndexPanel().setBackground(Color.WHITE);
		getButtonPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getPagesPanel().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 10, 0, 0),
				new PartialEtchedBorder(EtchedBorder.LOWERED, PartialEtchedBorder.SOUTH)));
	}
	
	public Dimension getPreferredSize(){
		return PREFERRED_SIZE;
	}

	public ButtonPanel createButtonPanel(){
		ButtonPanel buttonPanel = super.createButtonPanel();
		AbstractAction applyAction = new ApplyAction();
		applyAction.setEnabled(false);
		AbstractAction okAction = new OkAction();
		((JButton)buttonPanel.getButtonByName(ButtonNames.APPLY)).setAction(applyAction);
		((JButton)buttonPanel.getButtonByName(ButtonNames.OK)).setAction(okAction);
		return buttonPanel;
	}
	
	private void initializeDialogPages(){
	// add more concrete extensions of AbstractSettingsDialogPage here
	// to add more pages to the multipage dialog
		dialogPages.add(new GoogleSettingsDialogPage());
		PageList model = new PageList();
		Iterator it = dialogPages.iterator();
		while(it.hasNext()){
			AbstractSettingsDialogPage page = (AbstractSettingsDialogPage)it.next();
			model.append(page);
		}
		setPageList(model);
	}
	
	private void applyChanges(){
		Iterator it = dialogPages.iterator();
		while(it.hasNext()){
			AbstractSettingsDialogPage page = (AbstractSettingsDialogPage)it.next();
			page.applyChanges();
		}
	}
	
	private class OkAction extends AbstractAction{
		public OkAction(){
			super(UIManager.getString("OptionPane.okButtonText"));
		}
		
		public void actionPerformed(ActionEvent e) {
			applyChanges();
			setDialogResult(RESULT_AFFIRMED);
			setVisible(false);
			dispose();
		}
	}
	
	private class ApplyAction extends AbstractAction{

		public ApplyAction(){
			super("Apply");
		}
		
		public void actionPerformed(ActionEvent e) {
			applyChanges();
			setEnabled(false);
		}
	}
}
