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

/**
 * Metadata about a view; a view descriptor is effectively a singleton view
 * definition. A descriptor also acts as a factory which produces new instances
 * of a given view when requested, typically by a requesting application page. A
 * view descriptor can also produce a command which launches a view for display
 * on the page within the current active window.
 *
 * @author Keith Donald
 */
public interface ViewDescriptor extends PageComponentDescriptor {

    /**
     * Create a command that when executed, will attempt to show the
     * page component described by this descriptor in the provided
     * application window.
     *
     * @param window The window
     *
     * @return The show page component command.
     */
    public ActionCommand createShowViewCommand(ApplicationWindow window);

    public CommandButtonLabelInfo getShowViewCommandLabel();

}
