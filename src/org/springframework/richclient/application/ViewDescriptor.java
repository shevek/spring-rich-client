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

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.Assert;

/**
 * Metadata about a view; a view descriptor is effectively a singleton view
 * definition. A descriptor also acts as a factory which produces new instances
 * of a given view when requested, typically by a requesting application page. A
 * view descriptor can also produce a command which launches a view for display
 * on the page within the current active window.
 * 
 * @author Keith Donald
 */
public class ViewDescriptor extends LabeledObjectSupport implements
        InitializingBean {
    private Class viewClass;

    private Map viewProperties;

    private ApplicationEventMulticaster eventMulticaster;

    public void setViewClass(Class viewClass) {
        this.viewClass = viewClass;
    }

    public void setViewProperties(Map viewProperties) {
        this.viewProperties = viewProperties;
    }

    public void setApplicationEventMulticaster(ApplicationEventMulticaster e) {
        this.eventMulticaster = e;
    }

    public void afterPropertiesSet() {
        Assert.notNull(viewClass, "The viewClass property must be specified");
    }

    public View createView() {
        Object o = BeanUtils.instantiateClass(viewClass);
        Assert.isTrue((o instanceof View), "View class '" + viewClass
                + "' was instantiated, but instance is not a View!");
        View view = (View)o;
        if (eventMulticaster != null && view instanceof ApplicationListener) {
            eventMulticaster.addApplicationListener((ApplicationListener)view);
        }
        if (viewProperties != null) {
            BeanWrapper wrapper = new BeanWrapperImpl(view);
            wrapper.setPropertyValues(viewProperties);
        }
        return view;
    }

    public ActionCommand createActionCommand(ApplicationWindow window) {
        return new ShowViewCommand(this, window);
    }

    public static class ShowViewCommand extends ActionCommand {
        private ApplicationWindow window;

        private ViewDescriptor viewDescriptor;

        public ShowViewCommand(ViewDescriptor viewDescriptor,
                ApplicationWindow window) {
            super(viewDescriptor.getDisplayName());
            setLabel(viewDescriptor.getLabel());
            setIcon(viewDescriptor.getImageIcon());
            setCaption(viewDescriptor.getCaption());
            this.viewDescriptor = viewDescriptor;
            this.window = window;
            setEnabled(true);
        }

        protected void doExecuteCommand() {
            if (this.window == null) {
                this.window = Application.locator().getActiveWindow();
            }
            this.window.showViewOnPage(viewDescriptor);
        }
    }
}