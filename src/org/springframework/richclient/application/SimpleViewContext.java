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
package org.springframework.richclient.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.util.Assert;

/**
 * Mediator between the application and the view. The application uses this
 * class to get the view's local action handlers. The view uses this class to
 * get information about how the view is displayed in the application (for
 * example, on which window.)
 * 
 * @author Keith Donald
 */
public class SimpleViewContext implements ViewContext {

    private String viewName;

    private ApplicationPage page;

    private Map commandDelegates = new HashMap();

    public SimpleViewContext(String viewName, ApplicationPage page) {
        Assert.hasText(viewName);
        Assert.notNull(page);
        this.viewName = viewName;
        this.page = page;
    }

    public ApplicationWindow getApplicationWindow() {
        return page.getParentWindow();
    }

    public String getViewName() {
        return viewName;
    }

    public ActionCommandExecutor findGlobalCommandDelegate(String commandId) {
        Assert.notNull(commandId);
        return (ActionCommandExecutor)commandDelegates.get(commandId);
    }

    public void setGlobalCommandExecutor(String commandId,
            ActionCommandExecutor delegate) {
        Assert.notNull(commandId);
        commandDelegates.put(commandId, delegate);
    }

}