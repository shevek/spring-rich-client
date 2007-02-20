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

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.core.DescribedElement;
import org.springframework.richclient.core.VisualizedElement;

public interface PageDescriptor extends DescribedElement, VisualizedElement {
    public String getId();

    public void buildInitialLayout(PageLayoutBuilder pageLayout);

    /**
     * Create a command that when executed, will attempt to show the
     * page component described by this descriptor in the provided
     * application window.
     *
     * @param window The window
     *
     * @return The show page component command.
     */
    public ActionCommand createShowPageCommand(ApplicationWindow window);

    public CommandButtonLabelInfo getShowPageCommandLabel();


}
