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

import org.springframework.richclient.application.*;
import org.springframework.richclient.command.ActionCommand;

/**
 * {@link ActionCommand}for displaying a {@link View} based on the provided
 * {@link ViewDescriptor}.
 */
public class ShowViewCommand extends ApplicationWindowAwareCommand {
    private ViewDescriptor viewDescriptor;

    public ShowViewCommand() {
        super("showViewCommand");
        setEnabled(true);
    }

    public ShowViewCommand(ViewDescriptor viewDescriptor,
            ApplicationWindow window) {
        this();
        setViewDescriptor(viewDescriptor);
        setApplicationWindow(window);
        setEnabled(true);
    }

    public final void setViewDescriptor(ViewDescriptor viewDescriptor) {
        setId(viewDescriptor.getDisplayName());
        setLabel(viewDescriptor.getLabel());
        setIcon(viewDescriptor.getImageIcon());
        setCaption(viewDescriptor.getCaption());
        this.viewDescriptor = viewDescriptor;
    }

    protected void doExecuteCommand() {
        getApplicationWindow().getPage().showView(viewDescriptor);
    }

}
