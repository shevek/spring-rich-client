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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.controls.SimpleInternalFrame;

public class ApplicationPage {
    private JComponent pageControl = new JPanel(new BorderLayout());

    private ApplicationWindow window;

    private ViewRegistry viewRegistry;

    private List viewListeners = new ArrayList();

    public ApplicationPage(ApplicationWindow window) {
        this.window = window;
        this.viewRegistry = Application.services().getViewRegistry();
    }

    public ApplicationWindow getParentWindow() {
        return window;
    }

    public JComponent getControl() {
        return pageControl;
    }

    public void addViewListener(ViewListener listener) {
        viewListeners.add(listener);
    }

    public void showView(String viewName) {
        ViewDescriptor descriptor = viewRegistry.getViewDescriptor(viewName);
        if (descriptor != null) {
            showView(descriptor);
        }
    }

    public void showView(ViewDescriptor viewDescriptor) {
        View view = viewDescriptor.createView();
        view.initialize(viewDescriptor, new SimpleViewContext(viewDescriptor
                .getDisplayName(), this));
        SimpleInternalFrame viewPane = new SimpleInternalFrame(viewDescriptor
                .getImageIcon(), viewDescriptor.getDisplayName());
        viewPane.setToolTipText(viewDescriptor.getCaption());
        viewPane.add(view.getControl());
        pageControl.removeAll();
        pageControl.add(viewPane, BorderLayout.CENTER);
        pageControl.revalidate();
        fireViewActivated(view);
    }

    protected void fireViewActivated(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewActivated(view);
        }
    }

}