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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageDescriptor;
import org.springframework.richclient.application.ApplicationPageLayoutBuilder;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.ViewListener;
import org.springframework.richclient.control.SimpleInternalFrame;
import org.springframework.util.Assert;
import org.springframework.util.closure.support.AbstractConstraint;

/**
 * Provides a standard implementation of {@link ApplicationPage}
 */
public class DefaultApplicationPage implements ApplicationPage,
        ApplicationPageLayoutBuilder {

    private ApplicationPageDescriptor pageDescriptor;

    private JComponent pageControl;

    private SimpleInternalFrame viewPane;

    private ApplicationWindow parentWindow;

    private ViewDescriptorRegistry viewDescriptorRegistry;

    private Set viewPanes = new HashSet();

    private Set viewListeners = new LinkedHashSet();

    private ViewPane activeView;

    public DefaultApplicationPage(ApplicationWindow window,
            ApplicationPageDescriptor pageDescriptor) {
        Assert.notNull(window, "The containing window is required");
        Assert.notNull(pageDescriptor, "The page's descriptor is required");
        this.viewDescriptorRegistry = Application.services()
                .getViewDescriptorRegistry();
        this.parentWindow = window;
        this.pageDescriptor = pageDescriptor;
    }

    public String getId() {
        return pageDescriptor.getId();
    }

    public ApplicationWindow getParentWindow() {
        return parentWindow;
    }

    public JComponent getControl() {
        if (pageControl == null) {
            this.pageControl = new JPanel(new BorderLayout());
            this.pageDescriptor.buildInitialLayout(this);
        }
        return pageControl;
    }

    public void addViewListener(ViewListener listener) {
        viewListeners.add(listener);
    }

    public void removeViewListener(ViewListener listener) {
        viewListeners.remove(listener);
    }

    public void showView(String viewDescriptorId) {
        showView(getViewDescriptor(viewDescriptorId));
    }

    public void showView(ViewDescriptor viewDescriptor) {
        ViewPane viewPane = findViewPane(viewDescriptor.getId());
        if (viewPane != null) {
            giveFocusTo(viewPane);
        }
        else {
            viewPane = createViewPane(viewDescriptor);
            giveFocusTo(viewPane);
        }
        if (this.activeView != null) {
            fireViewDeactivated(this.activeView.getView());
        }
        this.activeView = viewPane;
        fireViewActivated(this.activeView.getView());
    }

    private ViewPane findViewPane(final String viewDescriptorId) {
        return (ViewPane)new AbstractConstraint() {
            public boolean test(Object arg) {
                return ((ViewPane)arg).getView().getId().equals(
                        viewDescriptorId);
            }
        }.findFirst(viewPanes);
    }

    protected boolean giveFocusTo(ViewPane viewPane) {
        this.pageControl.removeAll();
        this.pageControl.add(viewPane.getControl());
        this.pageControl.revalidate();
        this.pageControl.repaint();
        this.pageControl.requestFocusInWindow();
        return true;
    }

    private ViewPane createViewPane(ViewDescriptor viewDescriptor) {
        View view = createView(viewDescriptor);
        ViewPane viewPane = new ViewPane(view);
        this.viewPanes.add(viewPane);
        return viewPane;
    }

    protected View createView(ViewDescriptor viewDescriptor) {
        View view = viewDescriptor.createView();
        view.initialize(viewDescriptor, new DefaultViewContext(viewDescriptor
                .getId(), this));
        return view;
    }

    private ViewDescriptor getViewDescriptor(String viewDescriptorId) {
        return viewDescriptorRegistry.getViewDescriptor(viewDescriptorId);
    }
    
    public View getActiveView() {
        return activeView.getView();
    }

    protected void fireViewActivated(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewActivated(view);
        }
    }

    protected void fireViewDeactivated(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewDeactivated(view);
        }
    }

    // Initial Application Page Layout Builder methods
    
    public void addView(String viewDescriptorId) {
        ViewPane viewPane = createViewPane(getViewDescriptor(viewDescriptorId));
        this.pageControl.add(viewPane.getControl());
    }
}