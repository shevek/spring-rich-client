/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.support;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;

/**
 * @author Keith Donald
 */
public class ShowViewMenu extends CommandGroup implements
        ApplicationWindowAware {

    private ApplicationWindow window;

    public ShowViewMenu() {
        super("showViewMenu");
    }

    public void setApplicationWindow(ApplicationWindow window) {
        this.window = window;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        populate();
    }

    private void populate() {
        View[] views = Application.locator().getViewRegistry().findViews();
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            addInternal(new ShowViewCommand(view, window));
        }
    }

    public static class ShowViewCommand extends ActionCommand {
        private ApplicationWindow window;

        private View view;

        public ShowViewCommand(View view) {
            this(view, null);
        }

        public ShowViewCommand(View view, ApplicationWindow window) {
            super(view.getTitle());
            setLabel("&" + view.getTitle());
            setIcon(view.getTitleIcon());
            setCaption(view.getToolTip());
            this.view = view;
            this.window = window;
            setEnabled(true);
        }

        protected void doExecuteCommand() {
            if (this.window == null) {
                this.window = Application.locator().getActiveWindow();
            }
            this.window.showViewOnPage(view);
        }
    }
}