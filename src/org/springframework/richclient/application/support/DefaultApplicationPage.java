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

    private Set viewPanes = new LinkedHashSet();

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
        addViewListener(new SharedCommandTargeter(window));
    }

    public String getId() {
        return pageDescriptor.getId();
    }

    public ApplicationWindow getApplicationWindow() {
        return parentWindow;
    }

    public JComponent getControl() {
        if (pageControl == null) {
            this.pageControl = new JPanel(new BorderLayout());
            this.pageDescriptor.buildInitialLayout(this);
            setActiveView();
        }
        return pageControl;
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
        this.pageControl.removeAll();
        this.pageControl.add(viewPane.getControl());
        this.pageControl.validate();
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
        this.pageControl.add(viewPane.getControl());
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