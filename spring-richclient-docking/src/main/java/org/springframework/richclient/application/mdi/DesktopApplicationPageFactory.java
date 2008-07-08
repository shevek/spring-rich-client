/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.mdi;

import javax.swing.JDesktopPane;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.mdi.contextmenu.DefaultDesktopCommandGroupFactory;
import org.springframework.richclient.application.mdi.contextmenu.DesktopCommandGroupFactory;

/**
 * Factory for <code>DesktopApplicationPage</code> instances
 * 
 * @author Peter De Bruycker
 */
public class DesktopApplicationPageFactory implements ApplicationPageFactory {
	private int dragMode = JDesktopPane.LIVE_DRAG_MODE;
	private DesktopCommandGroupFactory desktopCommandGroupFactory;

	public int getDragMode() {
		return dragMode;
	}

	public void setDragMode(int dragMode) {
		this.dragMode = dragMode;
	}

	public ApplicationPage createApplicationPage(ApplicationWindow window, PageDescriptor descriptor) {
		return new DesktopApplicationPage(window, descriptor, dragMode, desktopCommandGroupFactory);
	}
	
	public void setDesktopCommandGroupFactory(DesktopCommandGroupFactory desktopCommandGroupFactory) {
		this.desktopCommandGroupFactory = desktopCommandGroupFactory;
	}
	
	public DesktopCommandGroupFactory getDesktopCommandGroupFactory() {
		if(desktopCommandGroupFactory == null) {
			desktopCommandGroupFactory = new DefaultDesktopCommandGroupFactory();
		}
		
		return desktopCommandGroupFactory;
	}
}
