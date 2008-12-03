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

import com.jidesoft.status.ProgressStatusBarItem;

/**
 * Extension of the JideStatusBarCommandGroup that overrides the progress
 * status bar item to use an IconProgressStatusBarItem rather than the
 * default.
 * 
 * @author Jonny Wray
 *
 */
public class IconProgressStatusBar extends JideStatusBar
{

	private ProgressStatusBarItem progressItem = null;
	
    public ProgressStatusBarItem getProgressStatusBarItem(){
    	if(progressItem == null){
    		progressItem = new IconProgressStatusBarItem();
            progressItem.setDefaultStatus(null);
    	}
    	return progressItem;
    }
}
