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
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentListener;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.util.ListenerListHelper;
import org.springframework.util.Assert;
import org.springframework.util.closure.support.AbstractConstraint;

/**
 * Provides a standard implementation of {@link ApplicationPage}
 */
public class DefaultApplicationPage implements ApplicationPage,
        PageLayoutBuilder {

    private PageDescriptor descriptor;

    private JComponent control;

    private ApplicationWindow window;

    private ViewDescriptorRegistry viewDescriptorRegistry = Application
            .services().getViewDescriptorRegistry();

    private Set pageComponents = new LinkedHashSet();

    private ListenerListHelper pageComponentListeners = new ListenerListHelper(
            PageComponentListener.class);

    private PageComponent activeComponent;

    protected DefaultApplicationPage() {
    }

    public DefaultApplicationPage(ApplicationWindow window,
            PageDescriptor pageDescriptor) {
        setApplicationWindow(window);
        setDescriptor(pageDescriptor);
    }

    public void setApplicationWindow(ApplicationWindow window) {
        Assert.notNull(window, "The containing window is required");
        Assert
                .state(this.window == null,
                        "Page window already set: it should only be set once, during initialization");
        this.window = window;
        addPageComponentListener(new SharedCommandTargeter(this.window));
    }

    public void setDescriptor(PageDescriptor descriptor) {
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

    public ApplicationWindow geWindow() {
        return window;
    }

    public JComponent getControl() {
        if (control == null) {
            this.control = new JPanel(new BorderLayout());
            this.descriptor.buildInitialLayout(this);
            setActiveComponent();
        }
        return control;
    }

    private void setActiveComponent() {
        if (pageComponents.size() > 0) {
            setActiveComponent((PageComponent)pageComponents.iterator().next());
        }
    }

    private void setActiveComponent(PageComponent pageComponent) {
        if (this.activeComponent != null) {
            fireFocusLost(this.activeComponent);
        }
        giveFocusTo(pageComponent.getContext().getPane());
        this.activeComponent = pageComponent;
        fireFocusGained(this.activeComponent);
    }

    public PageComponent getActiveComponent() {
        return activeComponent;
    }

    public void showView(String viewDescriptorId) {
        showView(getViewDescriptor(viewDescriptorId));
    }

    public void showView(ViewDescriptor viewDescriptor) {
        PageComponent component = findView(viewDescriptor.getId());
        if (component == null) {
            component = createView(viewDescriptor);
        }
        setActiveComponent(component);
    }

    private View findView(final String viewDescriptorId) {
        return (View)new AbstractConstraint() {
            public boolean test(Object arg) {
                if (arg instanceof View) {
                    return ((View)arg).getId().equals(viewDescriptorId);
                }
                else {
                    return false;
                }
            }
        }.findFirst(pageComponents);
    }

    protected boolean giveFocusTo(PageComponentPane pane) {
        this.control.removeAll();
        this.control.add(pane.getControl());
        this.control.validate();
        this.control.repaint();
        pane.requestFocusInWindow();
        return true;
    }

    protected View createView(ViewDescriptor viewDescriptor) {
        View view = viewDescriptor.createView();
        view.setContext(new DefaultViewContext(this,
                new PageComponentPane(view)));
        pageComponents.add(view);
        fireOpened(view);
        return view;
    }

    private ViewDescriptor getViewDescriptor(String viewDescriptorId) {
        return viewDescriptorRegistry.getViewDescriptor(viewDescriptorId);
    }

    public void openEditor(Object editorInput) {
        // todo
    }

    public void closeAllEditors() {
        // todo
    }

    public void addPageComponentListener(PageComponentListener listener) {
        pageComponentListeners.add(listener);
    }

    public void removePageComponentListener(PageComponentListener listener) {
        pageComponentListeners.remove(listener);
    }

    protected void fireOpened(PageComponent component) {
        pageComponentListeners.fire("componentOpened", component);
    }

    protected void fireFocusGained(PageComponent component) {
        pageComponentListeners.fire("componentFocusGained", component);
    }

    protected void fireFocusLost(PageComponent component) {
        pageComponentListeners.fire("componentFocusLost", component);
    }

    protected void fireClosed(PageComponent component) {
        pageComponentListeners.fire("componentClosed", component);
    }

    // Initial Application Page Layout Builder methods

    public void addView(String viewDescriptorId) {
        View view = createView(getViewDescriptor(viewDescriptorId));
        this.control.add(view.getContext().getPane().getControl());
    }

    public void close() {
        if (activeComponent != null) {
            fireFocusLost(activeComponent);
        }
        Iterator it = pageComponents.iterator();
        while (it.hasNext()) {
            PageComponent component = (PageComponent)it.next();
            component.dispose();
            fireClosed(component);
        }
    }
}