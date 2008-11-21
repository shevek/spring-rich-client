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

import net.sf.swingdocking.TabbingDesktopPane;

import org.springframework.core.JdkVersion;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

/**
 * @author Arne Limburg
 */
public class TabbedSwingDockingApplicationPage extends SwingDockingApplicationPage {

    public TabbedSwingDockingApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        super(window, pageDescriptor);
    }

    protected JDesktopPane createDesktopPane() {
        if (!JdkVersion.isAtLeastJava16()) {
            throw new IllegalStateException("At least Java Version 6 is needed for tabbed Swing-Docking.");
        }
        return new TabbingDesktopPane();
    }
}
