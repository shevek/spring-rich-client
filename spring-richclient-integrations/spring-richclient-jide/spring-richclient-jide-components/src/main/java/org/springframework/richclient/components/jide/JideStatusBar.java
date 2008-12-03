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
package org.springframework.richclient.components.jide;

import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.ProgressStatusBarItem;
import com.jidesoft.status.ProgressStatusBarItem.CancelCallback;
import com.jidesoft.status.StatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.progress.ProgressMonitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the Spring RCP StatusBar concept using the
 * JIDE status bar as the underlying component. By default the
 * status bar includes a component for displaying messages,
 * using LabelStatusBarItem, and a progress monitor, using
 * ProgressStatusBarItem. Extra status bar items can be added
 * to the bar via the statusBarItems parameter. 
 * 
 * @author Jonny Wray
 *
 */
public class JideStatusBar implements StatusBar {

	private static final String ERROR_ICON = "statusbar.error";
	private static final String MESSAGE_ICON = "statusbar.message";
	private com.jidesoft.status.StatusBar statusBar;
	private ProgressStatusBarItem progressItem;
	private LabelStatusBarItem statusItem = new LabelStatusBarItem("message");
	private ProgressStatusBarAdapter progressMonitor;
	private List statusBarItems = new ArrayList();
	
	public JideStatusBar(){
		progressItem = getProgressStatusBarItem();
		progressMonitor = new ProgressStatusBarAdapter(progressItem);
	}
	/**
     * Can be overridden to provide another progress status
     * bar item. For example, replacing the cancel button
     * by an icon.
     */
    protected ProgressStatusBarItem getProgressStatusBarItem(){
    	ProgressStatusBarItem item = new ProgressStatusBarItem();
    	item.setDefaultStatus(null);
    	return item;
    }

    /**
     * Controls whether to enable the cancel button on the progress
     * monitor. Default is true. 
     * 
     */
	public void setCancelEnabled(boolean enabled) { 
		progressMonitor.setCancelEnabled(enabled);
	}
    /**
     * Configure the call-back to be used when the cancel button is pressed. If not
     * configured a default is use that sets the cancel flag on the progress
     * monitor. 
     * 
     * @param cancelCallback
     */
    public void setCancelCallback(CancelCallback cancelCallback){
    	progressItem.setCancelCallback(cancelCallback);
    }
    
    public StatusBarItem getItemByName(String name){
    	return statusBar.getItemByName(name);
    }
    
    public void setStatusBarItems(List statusBarItems){
    	this.statusBarItems = statusBarItems;
    }
    
    public void setLabelStatusBarItem(LabelStatusBarItem item){
    	this.statusItem = item;
    }
    
    public StatusBarItem getLabelStatusBarItem(){
    	return statusItem;
    }
    
    public List getStatusBarItems(){
    	return Collections.unmodifiableList(statusBarItems);
    }

	public JComponent getControl() {
		if(statusBar == null){
			statusBar = new com.jidesoft.status.StatusBar();
	    	statusItem.setPreferredWidth(300);
	    	statusBar.add(statusItem, JideBoxLayout.FLEXIBLE);
	    	statusBar.add(progressItem, JideBoxLayout.VARY);
	    	Iterator it = statusBarItems.iterator();
	    	while(it.hasNext()){
	    		statusBar.add((StatusBarItem)it.next(), JideBoxLayout.FLEXIBLE);
	    	}
		}
		return statusBar;
	}

	public void clear() {
		updateStatusItem(null, null, SystemColor.BLACK);
	}

	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}


	public void setErrorMessage(String message) {
		updateStatusItem(message, getImageIcon(ERROR_ICON), SystemColor.RED);
	}

	public void setErrorMessage(Message errorMessage) {
		setErrorMessage(errorMessage == null ? null : errorMessage.getMessage());
	}

	public void setMessage(String message) {
		updateStatusItem(message, getImageIcon(MESSAGE_ICON), SystemColor.BLACK);
	}

	public void setMessage(Message message) {
		setMessage(message == null ? null : message.getMessage());
	}

	public void setVisible(boolean visible) {
		statusBar.setVisible(visible);
	}

	private void updateStatusItem(String message, Icon icon, Color color){
		statusItem.setForeground(color);
		statusItem.setText(message);
		statusItem.setIcon(icon);
	}
	
    private ImageIcon getImageIcon(String key){
    	IconSource iconSource = (IconSource)ApplicationServicesLocator.services().getService(IconSource.class);
    	Icon icon = iconSource.getIcon(key);
    	return (ImageIcon)icon;
    }
}
