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
package com.jidesoft.spring.richclient.googledemo.view;

import com.google.soap.search.GoogleSearchResultElement;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultEvent;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultsSelectionEvent;
import com.jidesoft.spring.richclient.googledemo.events.SwingWorkerExceptionEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.docking.jide.editor.OpenEditorEvent;
import org.springframework.richclient.application.statusbar.StatusBar;

import java.util.Locale;

/**
 * Event listener that updates the status bar message field
 * 
 * @author Jonny Wray
 *
 */
public class StatusBarListener implements ApplicationListener {

	private static final String UNKNOWN_ERROR = "statusBar.unknown.error";
	private static final String ESTIMATED_RESULTS = "statusBar.estimates.result";
	private static final String NO_RESULTS = "statusBar.no.results";
	private static final String OPENED = "statusBar.opened";
	
	private StatusBar statusBar;
	
	public void setStatusBar(StatusBar statusBar){
		this.statusBar = statusBar;
	}
	
	/*
	 * This could do to use something like an event handling strategy
	 * with the concrete strategy returned from a factory keyed on the
	 * event class and/or event type.
	 * 
	 * In bigger applications I don't use this event mechanism but 
	 * rather the event listener framework at https://elf.dev.java.net/
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof SearchResultEvent){
			SearchResultEvent specificEvent = (SearchResultEvent)event;
			if(specificEvent.getSearchResult() != null){
				Integer results = new Integer(specificEvent.getSearchResult().getResultElements().length);
				int estimatedResults = specificEvent.getSearchResult().getEstimatedTotalResultsCount();
				if(estimatedResults == 0){
					statusBar.setMessage(getMessage(NO_RESULTS));
				}
				else{
					statusBar.setMessage(getMessage(ESTIMATED_RESULTS, 
							new Object[]{new Integer(estimatedResults)}));
				}
			}
			else{
				statusBar.setMessage(getMessage(NO_RESULTS));
			}
		}
		else if(event instanceof SearchResultsSelectionEvent){
			//SearchResultsSelectionEvent specificEvent = (SearchResultsSelectionEvent)event;
			//statusBarCommandGroup.setMessage(specificEvent.getSearchResult().getURL());
		}
		else if(event instanceof SwingWorkerExceptionEvent){
			SwingWorkerExceptionEvent specificEvent = (SwingWorkerExceptionEvent)event;
			if(specificEvent.getCause().getCause() != null){
				statusBar.setErrorMessage(
						specificEvent.getCause().getCause().getMessage());
			}
			else{
				statusBar.setErrorMessage(getMessage(UNKNOWN_ERROR));
			}
		}
		else if(event instanceof OpenEditorEvent){
			OpenEditorEvent specificEvent = (OpenEditorEvent)event;
			GoogleSearchResultElement element = (GoogleSearchResultElement)specificEvent.getObject();
			statusBar.setMessage(getMessage(OPENED, new Object[]{element.getURL()}));
		}
	}

	private String getMessage(String key){
		return getMessage(key, new Object[]{});
	}
	
	private String getMessage(String key, Object[] values){
		MessageSource messageSource = (MessageSource)ApplicationServicesLocator.services().getService(MessageSource.class);
		return messageSource.getMessage(key, values, Locale.getDefault());
	}
}
