/*
 * Copyright 2008 the original author or authors.
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
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.support.ShowViewCommand;
import org.springframework.util.Assert;

/**
 * {@link ViewDescriptor} implementation for internal purposes (mostly testing).
 * <p>
 * This class accepts an existing {@link View} instance, and returns this in the {@link #createPageComponent()} method.
 * <p>
 * Normally you should never use this class directly.
 * 
 * @author Peter De Bruycker
 */
public class SimpleViewDescriptor implements ViewDescriptor {

    private View view;

    public SimpleViewDescriptor(View view) {
        Assert.notNull(view, "view cannot be null");

        this.view = view;
    }

    public ActionCommand createShowViewCommand(ApplicationWindow window) {
        return new ShowViewCommand(this, window);
    }

    public CommandButtonLabelInfo getShowViewCommandLabel() {
        return new CommandButtonLabelInfo(getDisplayName());
    }

    public PageComponent createPageComponent() {
        return view;
    }

    public String getId() {
        return view.getId();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        view.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        view.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        view.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        view.removePropertyChangeListener(propertyName, listener);
    }

    public String getCaption() {
        return view.getCaption();
    }

    public String getDescription() {
        return view.getDescription();
    }

    public String getDisplayName() {
        return view.getDisplayName();
    }

    public Icon getIcon() {
        return view.getIcon();
    }

    public Image getImage() {
        return view.getImage();
    }

}
