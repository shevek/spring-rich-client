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

import java.awt.Window;

import javax.swing.Icon;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.util.Assert;

public abstract class AbstractView extends AbstractControlFactory implements
        View {
    private ViewDescriptor descriptor;

    private ViewContext context;

    public final void initialize(ViewDescriptor descriptor, ViewContext context) {
        Assert.isTrue(context != null, "View context must be non-null");
        Assert.isTrue(descriptor != null, "View descriptor must be non-null");
        this.descriptor = descriptor;
        this.context = context;
        registerGlobalCommandDelegates(context);
    }

    public ViewContext getContext() {
        return context;
    }

    protected CommandManager getCommandManager() {
        return context.getParentWindow().getCommandRegistry();
    }

    protected StatusBarCommandGroup getStatusBar() {
        return context.getParentWindow().getStatusBar();
    }

    protected Window getParentWindowControl() {
        return getContext().getParentWindow().getControl();
    }

    public String getDisplayName() {
        return descriptor.getDisplayName();
    }

    public String getCaption() {
        return descriptor.getCaption();
    }

    public String getDescription() {
        return descriptor.getDescription();
    }

    public ActionCommand createActionCommand() {
        return null;
    }

    public Icon getIcon() {
        return descriptor.getImageIcon();
    }

    /**
     * Template method called when this view is initialized in a context; allows
     * subclasses to register global actions with the context.
     * 
     * @param context
     */
    protected void registerGlobalCommandDelegates(ViewContext context) {

    }

}