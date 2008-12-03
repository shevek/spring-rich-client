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
package com.jidesoft.spring.richclient.googledemo.events;

import java.lang.reflect.InvocationTargetException;

import org.springframework.context.ApplicationEvent;

/**
 * Internal event that signals an remote exception from
 * within a Swing worker
 * 
 * @author Jonny Wray
 *
 */
public class SwingWorkerExceptionEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 4604992370692705011L;
	private InvocationTargetException cause;
	
	public SwingWorkerExceptionEvent(Object source, InvocationTargetException cause){
		super(source);
		this.cause = cause;
	}
	
	public InvocationTargetException getCause(){
		return cause;
	}
}
