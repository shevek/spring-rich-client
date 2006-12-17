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
package org.springframework.richclient.command.support;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.util.Assert;

/**
 * An action command for displaying a {@link View} based on the provided {@link ViewDescriptor}.
 */
public class ShowViewCommand extends ApplicationWindowAwareCommand {
    
    /** The identifier of this command. */
    public static final String ID = "showViewCommand";
    
    private ViewDescriptor viewDescriptor;

    /**
     * Creates a new {@code ShowViewCommand} with an id of {@value #ID}. 
     * The newly created command will be enabled by default.
     */
    //FIXME does this class need a no-args constructor? The class cannot operate without a 
    //view descriptor and once set, the view descriptor ID will override the one set in this 
    //constructor anyway.
    public ShowViewCommand() {
        super(ID);
        setEnabled(true);
    }

    /**
     * Creates a new {@code ShowViewCommand} with the given view descriptor and associated 
     * application window. The new instance will have a command identifier equal to the id from
     * the view descriptor, the command will be enabled by default.
     *
     * @param viewDescriptor The object describing the view that this command will be 
     * responsible for showing.
     * @param window The application window that the command belongs to.
     * 
     * @throw IllegalArgumentException if {@code viewDescriptor} is null.
     */
    public ShowViewCommand(ViewDescriptor viewDescriptor, ApplicationWindow window) {
        this();
        //FIXME does this really need to call this()? It only provides an ID that will be 
        //overridden anyway when setViewDescriptor is called on the next line.
        setViewDescriptor(viewDescriptor);
        setApplicationWindow(window);
        setEnabled(true);
    }

    /**
     * Sets the object that describes the view that is to be opened by this command object.
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
        //made an invariant on the ApplicationWindowAwareCommand that it never returns null.
        //Same applies to ApplicationWindow.getPage(), can also return null
        getApplicationWindow().getPage().showView(viewDescriptor);
    }

}
