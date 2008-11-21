/*
 * Copyright 2008 the original author or authors.
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
package org.springframework.richclient.application.docking.swingdocking;

import javax.swing.JDesktopPane;

import org.springframework.core.JdkVersion;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.mdi.DesktopApplicationPage;
import org.springframework.richclient.application.mdi.contextmenu.DefaultDesktopCommandGroupFactory;

/**
 * @author Arne Limburg
 */
public class SwingDockingApplicationPageFactory implements ApplicationPageFactory {

    public ApplicationPage createApplicationPage(ApplicationWindow window, PageDescriptor descriptor) {
        if (JdkVersion.isAtLeastJava16()) {
            return new TabbedSwingDockingApplicationPage(window, descriptor);
        } else if (JdkVersion.isAtLeastJava15()) {
            return new SwingDockingApplicationPage(window, descriptor);
        } else {
            return new DesktopApplicationPage(window, descriptor, JDesktopPane.OUTLINE_DRAG_MODE, new DefaultDesktopCommandGroupFactory());
        }
    }
}
