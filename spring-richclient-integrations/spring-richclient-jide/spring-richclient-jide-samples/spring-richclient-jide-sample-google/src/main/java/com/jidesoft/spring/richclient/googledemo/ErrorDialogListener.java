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
package com.jidesoft.spring.richclient.googledemo;


import javax.swing.JOptionPane;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.jidesoft.spring.richclient.googledemo.events.SwingWorkerExceptionEvent;
/**
 * Listener that reacts to SearchExceptionEvents and displays
 * an error dialog. No real error translation occurs, just the
 * message of the underlying cause is displayed.
 * 
 * @author Jonny Wray
 *
 */
public class ErrorDialogListener implements ApplicationListener{
	
	public ErrorDialogListener(){
		
	}
	

	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof SwingWorkerExceptionEvent){
			SwingWorkerExceptionEvent specificEvent = (SwingWorkerExceptionEvent)event;
			String message = "Unknown Error";
			if(specificEvent.getCause().getCause() != null){
				message = specificEvent.getCause().getCause().getMessage();
			}
			JOptionPane.showMessageDialog(null, message, 
					"Application Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
