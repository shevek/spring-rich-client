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

import org.springframework.richclient.application.statusbar.support.StatusBarProgressMonitor;
import org.springframework.richclient.progress.ProgressMonitor;

import com.jidesoft.status.ProgressStatusBarItem;

/**
 * An adapter that converts the JIDE ProgressStatusBarItem to
 * implement the Spring RCP ProgressMonitor interface.
 * 
 * @author Jonny Wray
 *
 */
public class ProgressStatusBarAdapter implements ProgressMonitor{
	
	private ProgressStatusBarItem barItem;
	private boolean canceled = false;
	
	/**
	 * Constructs an adapter to convert a JIDE ProgressStatusBarItem
	 * into a Spring RCP ProgressMonitor.
	 * 
	 * @param barItem The ProgressStatusBarItem to convert
	 */
	public ProgressStatusBarAdapter(ProgressStatusBarItem barItem){
		this.barItem = barItem;
		if(barItem.getCancelCallback() == null){
			barItem.setCancelCallback(new ProgressStatusBarItem.CancelCallback(){
			    public void cancelPerformed(){
			    	setCanceled(true);
			    }
			});
		}
		setCanceled(true);
	}
	
	public void setCancelEnabled(boolean cancelEnabled){
		barItem.getCancelButton().setEnabled(cancelEnabled);
	}
	
	public void taskStarted(String name, int totalWork){
		if (totalWork == StatusBarProgressMonitor.UNKNOWN) {
			barItem.setIndeterminate(true);
		}
		else{
			barItem.setIndeterminate(false);
			barItem.setProgress(0);
		}
		barItem.setProgressStatus(name);
	}

    /**
     * Null method, no subTask support currently
     */
    public void subTaskStarted(String name){
    	
    }

    /**
     * Notifies that a percentage of the work has been completed. This is called
     * by clients when the work is performed and is used to update the progress
     * monitor.
     * 
     * @param work
     *            the percentage complete (0..100)
     */
    public void worked(int work){
		barItem.setProgress(work);
    }

    /**
     * Notifies that the work is done; that is, either the main task is
     * completed or the user cancelled it.
     * 
     * done() can be called more than once; an implementation should be prepared
     * to handle this case.
     */
    public void done(){
    	barItem.setProgress(100);
    }

    /**
     * Returns true if the user does some UI action to cancel this operation.
     * (like hitting the Cancel button on the progress dialog). The long running
     * operation typically polls isCanceled().
     */
    public boolean isCanceled(){
    	return canceled;
    }

    /**
     * Attempts to cancel the monitored task.
     */
    public void setCanceled(boolean b){
    	this.canceled = b;
    }
    
}
