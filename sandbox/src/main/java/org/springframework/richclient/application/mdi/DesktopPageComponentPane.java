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
package org.springframework.richclient.application.mdi;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentPane;

/**
 * @author Peter De Bruycker
 */
public class DesktopPageComponentPane extends PageComponentPane {

    private PageComponent pageComponent;

    private DesktopApplicationPage applicationPage;

    private JInternalFrame internalFrame;

    public DesktopPageComponentPane(DesktopApplicationPage applicationPage, PageComponent component) {
        super(component);
        this.applicationPage = applicationPage;
        pageComponent = component;
    }

    protected JComponent createControl() {
        internalFrame = new JInternalFrame(pageComponent.getDisplayName());
        if (pageComponent.getIcon() != null) {
            internalFrame.setFrameIcon(pageComponent.getIcon());
        }
        internalFrame.setResizable(true);
        internalFrame.setMaximizable(true);
        internalFrame.setIconifiable(true);
        internalFrame.setClosable(true);
        internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosed(InternalFrameEvent e) {
                applicationPage.close(pageComponent);
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                applicationPage.setActiveComponent(pageComponent);
            }
        });

        internalFrame.getContentPane().add(pageComponent.getControl());
        internalFrame.pack();
        return internalFrame;
    }
}
