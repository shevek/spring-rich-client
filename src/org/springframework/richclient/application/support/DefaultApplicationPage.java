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
import org.springframework.util.Assert;
import org.springframework.util.closure.support.AbstractConstraint;

/**
 * Provides a standard implementation of {@link ApplicationPage}
 */
public class DefaultApplicationPage implements ApplicationPage,
        ApplicationPageLayoutBuilder {

    private ApplicationPageDescriptor descriptor;

    private JComponent control;

    private ApplicationWindow window;

    private ViewDescriptorRegistry viewDescriptorRegistry;

    private Set viewPanes = new LinkedHashSet();

    private Set viewListeners = new LinkedHashSet();

    private ViewPane activeView;

    protected DefaultApplicationPage() {
        this.viewDescriptorRegistry = Application.services()
                .getViewDescriptorRegistry();
    }

    public DefaultApplicationPage(ApplicationWindow window,
            ApplicationPageDescriptor pageDescriptor) {
        this.viewDescriptorRegistry = Application.services()
                .getViewDescriptorRegistry();
        setApplicationWindow(window);
        setDescriptor(pageDescriptor);
    }

    public void setApplicationWindow(ApplicationWindow window) {
        Assert.notNull(window, "The containing window is required");
        Assert
                .state(this.window == null,
                        "Page window already set: it should only be set once, during initialization");
        this.window = window;
        addViewListener(new SharedCommandTargeter(this.window));
    }

    public void setDescriptor(ApplicationPageDescriptor descriptor) {
        Assert.notNull(descriptor, "The page's descriptor is required");
        Assert
                .state(
                        this.descriptor == null,
                        "Page descriptor already set: it should only be set once, during initialization");
        this.descriptor = descriptor;
    }

    public String getId() {
        return descriptor.getId();
    }

    public ApplicationWindow getApplicationWindow() {
        return window;
    }

    public JComponent getControl() {
        if (control == null) {
            this.control = new JPanel(new BorderLayout());
            this.descriptor.buildInitialLayout(this);
            setActiveView();
        }
        return control;
    }

    private void setActiveView() {
        if (viewPanes.size() > 0) {
            activeView = (ViewPane)viewPanes.iterator().next();
            fireViewFocusGained(activeView.getView());
        }
        else {
            activeView = null;
        }
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
            fireViewFocusLost(this.activeView.getView());
        }
        this.activeView = viewPane;
        fireViewFocusGained(this.activeView.getView());
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
        this.control.removeAll();
        this.control.add(viewPane.getControl());
        this.control.validate();
        this.control.repaint();
        viewPane.requestFocusInWindow();
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
        view.initialize(new DefaultViewContext(viewDescriptor, this));
        fireViewCreated(view);
        return view;
    }

    private ViewDescriptor getViewDescriptor(String viewDescriptorId) {
        return viewDescriptorRegistry.getViewDescriptor(viewDescriptorId);
    }

    public View getActiveView() {
        return activeView.getView();
    }

    public void addViewListener(ViewListener listener) {
        viewListeners.add(listener);
    }

    public void removeViewListener(ViewListener listener) {
        viewListeners.remove(listener);
    }

    protected void fireViewFocusGained(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewFocusGained(view);
        }
    }

    protected void fireViewFocusLost(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewFocusLost(view);
        }
    }

    protected void fireViewCreated(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewCreated(view);
        }
    }

    protected void fireViewDisposed(View view) {
        for (Iterator i = viewListeners.iterator(); i.hasNext();) {
            ViewListener l = (ViewListener)i.next();
            l.viewDisposed(view);
        }
    }

    // Initial Application Page Layout Builder methods

    public void addView(String viewDescriptorId) {
        ViewPane viewPane = createViewPane(getViewDescriptor(viewDescriptorId));
        this.control.add(viewPane.getControl());
    }

    public void close() {
        if (activeView != null) {
            fireViewFocusLost(activeView.getView());
        }
        Iterator it = viewPanes.iterator();
        while (it.hasNext()) {
            View view = ((ViewPane)it.next()).getView();
            view.dispose();
            fireViewDisposed(view);
        }
    }
}