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

import java.awt.Image;
import java.awt.Window;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewContext;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.util.Assert;

public abstract class AbstractView extends AbstractControlFactory implements View {
    private ViewDescriptor descriptor;

    private ViewContext context;

    public void setDescriptor(ViewDescriptor descriptor) {
        Assert.notNull(descriptor, "The view descriptor is required");
        Assert.state(this.descriptor == null, "A view's descriptor may only be set once");
        this.descriptor = descriptor;
    }

    public final void setContext(ViewContext context) {
        Assert.notNull(context, "This view's page component context is required");
        Assert.state(this.context == null, "A view's context may only be set once");
        this.context = context;
        registerLocalCommandExecutors(context);
    }

    public String getId() {
        return getDescriptor().getId();
    }

    public ViewDescriptor getDescriptor() {
        return descriptor;
    }

    public PageComponentContext getContext() {
        return context;
    }

    public String getCaption() {
        return getDescriptor().getCaption();
    }

    public String getDescription() {
        return getDescriptor().getDescription();
    }

    public String getDisplayName() {
        return getDescriptor().getDisplayName();
    }

    public Icon getIcon() {
        return getDescriptor().getIcon();
    }

    public Image getImage() {
        return getDescriptor().getImage();
    }

    protected final Window getWindowControl() {
        return getContext().getWindow().getControl();
    }

    protected final CommandManager getWindowCommandManager() {
        return context.getWindow().getCommandManager();
    }

    protected final StatusBarCommandGroup getStatusBar() {
        return context.getWindow().getStatusBar();
    }

    protected abstract JComponent createControl();

    /**
     * Template method called once when this view is initialized; allows
     * subclasses to register local executors for shared commands with the view
     * context.
     * 
     * @param context
     *            the view context
     */
    protected void registerLocalCommandExecutors(ViewContext context) {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getDescriptor().addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getDescriptor().addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getDescriptor().removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getDescriptor().removePropertyChangeListener(propertyName, listener);
    }

    public void dispose() {

    }

}