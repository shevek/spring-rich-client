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

import java.awt.Image;
import java.awt.Window;

import javax.swing.Icon;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.config.IconConfigurable;
import org.springframework.richclient.progress.StatusBarCommandGroup;

public abstract class AbstractView extends AbstractControlFactory implements
        View, DescriptionConfigurable, IconConfigurable {
    private String title;

    private String toolTip;

    private Icon titleIcon;

    private ViewContext context;

    public void setCaption(String caption) {
        this.title = caption;
    }

    public void setDescription(String description) {
        this.toolTip = description;
    }

    public void setIcon(Icon icon) {
        this.titleIcon = icon;
    }

    public void initialize(ViewContext context) {
        this.context = context;
        registerGlobalCommandDelegates(context);
    }

    public String getTitle() {
        return title;
    }

    public Icon getTitleIcon() {
        return titleIcon;
    }

    public String getToolTip() {
        return toolTip;
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

    protected String getMessage(String messageKey) {
        return getMessages().getMessage(messageKey);
    }

    protected MessageSourceAccessor getMessages() {
        return Application.locator().getMessages();
    }

    protected Icon getIcon(String iconKey) {
        return Application.locator().getIcon(iconKey);
    }

    protected Image getImage(String imageKey) {
        return Application.locator().getImage(imageKey);
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