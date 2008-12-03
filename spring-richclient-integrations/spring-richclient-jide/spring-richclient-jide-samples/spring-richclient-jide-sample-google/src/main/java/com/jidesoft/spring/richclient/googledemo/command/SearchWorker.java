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

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.application.statusbar.support.StatusBarProgressMonitor;
import org.springframework.richclient.util.SwingWorker;

import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchResult;

import com.jidesoft.spring.richclient.googledemo.events.SwingWorkerExceptionEvent;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultEvent;
import com.jidesoft.spring.richclient.googledemo.preferences.GoogleSettingsManager;

/**
 * SwingWorker implementation that searches google based on 
 * a specific query string and fires an event on finish.
 * 
 * @author Jonny Wray
 *
 */
public class SearchWorker extends SwingWorker{

	private static final String PROGESS_MESSAGE = "searchGoogleCommand.progress";
	
	private String query = null;
	private int startResult;
	
	public SearchWorker(String query){
		this(query, 0);
	}

	public SearchWorker(String query, int startResult){
		this.query = query;
		this.startResult = startResult;
	}
	
	private StatusBar getStatusBar(){
		return Application.instance().getActiveWindow().getStatusBar();
	}
	
	private String getGoogleKey(){
		GoogleSettingsManager preferencesManager = 
			(GoogleSettingsManager)ApplicationServicesLocator.services().getService(GoogleSettingsManager.class);
		return preferencesManager.getGoogleKey();
	}
	
	// Occurs outside the EDT
	protected Object construct() throws Exception {
		if(query != null && query.trim().length() > 0){
			GoogleSearch googleSearch = new GoogleSearch();
			googleSearch.setKey(getGoogleKey());
			googleSearch.setQueryString(query);
			googleSearch.setStartResult(startResult);
			try{
				MessageSource messageSource = (MessageSource)ApplicationServicesLocator.services().getService(MessageSource.class);
				String title = messageSource.getMessage(PROGESS_MESSAGE, new Object[]{}, Locale.getDefault());
				getStatusBar().getProgressMonitor().taskStarted(title, StatusBarProgressMonitor.UNKNOWN);
				return googleSearch.doSearch();
			}
			finally{
				getStatusBar().getProgressMonitor().done();
			}
		}
		else{
			throw new IllegalArgumentException("Query string has to be specified");
		}
	}

	// Occurs within the EDT
	protected void finished() {
		if(getException() == null){
			GoogleSearchResult result = (GoogleSearchResult)getFinishedResult();
			Application.instance().getApplicationContext().publishEvent(
					new SearchResultEvent(this, result));
		}
		else{
			InvocationTargetException error = getException();
			Application.instance().getApplicationContext().publishEvent(
					new SwingWorkerExceptionEvent(this, error));
		}
	}
	
}
