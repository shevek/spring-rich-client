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

import com.jidesoft.docking.DockingManager;
import org.springframework.richclient.application.docking.jide.perspective.Perspective;

/**
 * Perspective that hides the details view
 * 
 * @author Jonny Wray
 *
 */
public class LimitedPerspective extends Perspective
{

	public void display(DockingManager manager) {
		manager.hideFrame("detailsView");
		manager.showFrame("searchResultsView");
		manager.setShowWorkspace(true);
	}

}
