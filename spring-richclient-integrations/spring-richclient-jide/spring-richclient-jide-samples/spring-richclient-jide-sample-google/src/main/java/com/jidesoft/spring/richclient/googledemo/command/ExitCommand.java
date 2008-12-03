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

import com.jidesoft.docking.DockingManager;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.docking.jide.JideApplicationPage;
import org.springframework.richclient.application.docking.jide.JideApplicationWindow;
import org.springframework.richclient.application.docking.jide.LayoutManager;
import org.springframework.richclient.application.docking.jide.perspective.Perspective;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

/**
 * Exit command that saves the current page layout upon exit.
 * 
 * @author Jonny Wray
 *
 */
public class ExitCommand extends ApplicationWindowAwareCommand {

    public ExitCommand() {
        super("exitCommand");
    }

    protected void doExecuteCommand() {
    	DockingManager manager = ((JideApplicationWindow)getApplicationWindow()).getDockingManager();
    	Perspective perspective =
    		((JideApplicationPage)getApplicationWindow().getPage()).getPerspectiveManager().getCurrentPerspective();
		
    	LayoutManager.savePageLayoutData(manager,
    		getApplicationWindow().getPage().getId(), perspective.getId());
        Application.instance().close();
    }

}
