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

import org.springframework.binding.value.support.PropertyChangeSupport;
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
    private String id;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public SimpleViewDescriptor(String id, View view) {
        Assert.notNull(view, "view cannot be null");

        this.id = id;
        this.view = view;
        
        view.setDescriptor(this);
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
        return id;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public String getCaption() {
        return id;
    }

    public String getDescription() {
        return id;
    }

    public String getDisplayName() {
        return id;
    }

    public Icon getIcon() {
        return null;
    }

    public Image getImage() {
        return null;
    }

}
