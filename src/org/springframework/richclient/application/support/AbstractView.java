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

import java.awt.Window;

import javax.swing.JComponent;

import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewContext;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.util.Assert;

public abstract class AbstractView extends LabeledObjectSupport implements View {

    private ViewDescriptor descriptor;

    private ViewContext context;

    private AbstractControlFactory controlFactory = new AbstractControlFactory() {
        protected JComponent createControl() {
            return AbstractView.this.createControl();
        }
    };

    public final void initialize(ViewDescriptor descriptor, ViewContext context) {
        Assert.isTrue(context != null, "View context must be non-null");
        Assert.isTrue(descriptor != null, "View descriptor must be non-null");
        this.descriptor = descriptor;
        setTitle(descriptor.getDisplayName());
        setCaption(descriptor.getCaption());
        setDescription(descriptor.getDescription());
        setImage(descriptor.getImage());
        this.context = context;
        setGlobalCommandExecutors(context);
    }

    public String getId() {
        return descriptor.getId();
    }
    
    public ViewContext getContext() {
        return context;
    }

    public ViewDescriptor getViewDescriptor() {
        return descriptor;
    }

    protected CommandManager getCommandManager() {
        return context.getApplicationWindow().getCommandManager();
    }

    protected StatusBarCommandGroup getStatusBar() {
        return context.getApplicationWindow().getStatusBar();
    }

    protected Window getParentWindowControl() {
        return getContext().getApplicationWindow().getControl();
    }

    public ActionCommand createActionCommand() {
        return null;
    }

    public JComponent getControl() {
        return controlFactory.getControl();
    }

    protected abstract JComponent createControl();

    /**
     * Template method called when this view is initialized in a context; allows
     * subclasses to register global actions with the context.
     * 
     * @param context
     */
    protected void setGlobalCommandExecutors(ViewContext context) {
        // nothing
    }

    public final boolean isControlCreated() {
        return controlFactory.isControlCreated();
    }

}