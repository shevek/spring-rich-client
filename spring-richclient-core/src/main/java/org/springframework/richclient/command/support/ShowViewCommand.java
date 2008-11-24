/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.command.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PropertyNotSetException;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.util.Assert;

/**
 * An action command for displaying a {@link View} based on a provided {@link ViewDescriptor}.
 */
public class ShowViewCommand extends ApplicationWindowAwareCommand implements InitializingBean {
    
    private ViewDescriptor viewDescriptor;
    
    /**
     * Creates a new uninitialized {@code ShowViewCommand}. The {@code applicationWindow} and 
     * {@code viewDescriptor} properties must be set before using the new instance.
     */
    public ShowViewCommand() {
        //do nothing
    }

    /**
     * Creates a new {@code ShowViewCommand} with the given view descriptor and associated 
     * application window. The new instance will have a command identifier equal to the id from
     * the view descriptor, the command will be enabled by default.
     *
     * @param viewDescriptor The object describing the view that this command will be 
     * responsible for showing.
     * @param applicationWindow The application window that the command belongs to.
     * 
     * @throw IllegalArgumentException if {@code viewDescriptor} or {@code applicationWindow} are null.
     */
    public ShowViewCommand(ViewDescriptor viewDescriptor, ApplicationWindow applicationWindow) {
        Assert.required(applicationWindow, "applicationWindow");
        setViewDescriptor(viewDescriptor);
        setApplicationWindow(applicationWindow);
        setEnabled(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() {
        PropertyNotSetException.throwIfNull(getApplicationWindow(), "applicationWindow", getClass());
        PropertyNotSetException.throwIfNull(this.viewDescriptor, "viewDescriptor", getClass());
    }

    /**
     * Sets the descriptor for the view that is to be opened by this command object. This
     * command object will be assigned the id, label, icon, and caption from the given view
     * descriptor.
     *
     * @param viewDescriptor The view descriptor, cannot be null.
     * 
     * @throws IllegalArgumentException if {@code viewDescriptor} is null.
     */
    public final void setViewDescriptor(ViewDescriptor viewDescriptor) {
        Assert.required(viewDescriptor, "viewDescriptor");
        setId(viewDescriptor.getId()); 
        setLabel(viewDescriptor.getShowViewCommandLabel());
        setIcon(viewDescriptor.getIcon());
        setCaption(viewDescriptor.getCaption());
        this.viewDescriptor = viewDescriptor;
    }

    /**
     * Causes the view described by this instance's view descriptor to be shown.
     */
    protected void doExecuteCommand() {
        //FIXME getApplicationWindow can potentially return null. This should probably be 
        //made an invariant on the ApplicationWindowAwareCommand, that it never returns null.
        //Same applies to ApplicationWindow.getPage(), can also return null
        getApplicationWindow().getPage().showView(this.viewDescriptor.getId());
    }

}
