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
package org.springframework.richclient.application.support;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;

/**
 * Provides a standard implementation of {@link ApplicationPage}
 */
public class DefaultApplicationPage extends AbstractApplicationPage implements PageLayoutBuilder {

    private JComponent control;

    public DefaultApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        super(window, pageDescriptor);
    }

    public JComponent getControl() {
        if (control == null) {
            this.control = new JPanel(new BorderLayout());
            this.getPageDescriptor().buildInitialLayout(this);
            setActiveComponent();
        }
        return control;
    }
    
    protected boolean giveFocusTo(PageComponent pageComponent) {
        PageComponentPane pane = pageComponent.getContext().getPane();
        this.control.removeAll();
        this.control.add(pane.getControl());
        this.control.validate();
        this.control.repaint();
        pane.requestFocusInWindow();
        
        fireFocusGained(pageComponent);
        
        return true;
    }

    protected PageComponent createPageComponent(PageComponentDescriptor pageComponentDescriptor) {
        PageComponent pageComponent = pageComponentDescriptor.createPageComponent();
        pageComponent.setContext(new DefaultViewContext(this, new PageComponentPane(pageComponent)));

        return pageComponent;
    }

    // Initial Application Page Layout Builder methods
    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }
}