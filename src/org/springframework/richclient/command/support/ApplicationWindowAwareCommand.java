/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.command.support;

import javax.swing.JFrame;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.ActionCommand;

/**
 * @author Keith Donald
 */
public abstract class ApplicationWindowAwareCommand extends ActionCommand implements ApplicationWindowAware {

	private ApplicationWindow window;

	protected ApplicationWindowAwareCommand() {

	}

	protected ApplicationWindowAwareCommand(String commandId) {
		super(commandId);
	}

	public void setApplicationWindow(ApplicationWindow window) {
		this.window = window;
	}

	protected ApplicationWindow getApplicationWindow() {
		return window;
	}

	protected JFrame getParentWindowControl() {
		if (window == null) {
			return Application.instance().getActiveWindow().getControl();
		}
		return window.getControl();
	}
}