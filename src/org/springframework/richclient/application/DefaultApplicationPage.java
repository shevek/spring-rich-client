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

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import org.springframework.richclient.control.SimpleInternalFrame;

/**
 * Provides a standard implementation of {@link ApplicationPage}
 */
public class DefaultApplicationPage implements ApplicationPage {
    private JComponent pageControl;

    private SimpleInternalFrame viewPane;

    private ApplicationWindow window;

    private ViewRegistry viewRegistry;

    private View view;

    private List viewListeners = new ArrayList();

    public DefaultApplicationPage(ApplicationWindow window) {
        this.window = window;
        this.viewRegistry = Application.services().getViewRegistry();
        this.pageControl = new JPanel(new BorderLayout());
        this.viewPane = new SimpleInternalFrame("");
        this.pageControl.add(viewPane, BorderLayout.CENTER);
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
        // todo - views are always being recreated when switched...we should
        // cache open views...
        if (view != null) {
            view.removePropertyChangeListener(this);
            viewPane.remove(view.getControl());
        }
        view = viewDescriptor.createView();
        view.initialize(viewDescriptor, new SimpleViewContext(
                viewDescriptor.getDisplayName(), this));
        viewPane.add(view.getControl());
        view.addPropertyChangeListener(this);
        updateViewPane();
        fireViewActivated(view);
    }

    public View getView() {
        return view;
    }

    protected void fireViewActivated(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewActivated(view);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateViewPane();
    }

    private void updateViewPane() {
        viewPane.setTitle(view.getDisplayName());
        viewPane.setFrameIcon(view.getImageIcon());
        viewPane.setToolTipText(view.getCaption());
    }

}
