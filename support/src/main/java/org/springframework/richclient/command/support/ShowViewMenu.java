/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.support;

import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.CommandGroup;

/**
 * A menu containing a collection of sub-menu items that each display a given view.
 * 
 * @author Keith Donald
 */
public class ShowViewMenu extends CommandGroup implements ApplicationWindowAware {

    /** The identifier of this command. */
    public static final String ID = "showViewMenu";

    private ApplicationWindow window;

    /**
     * Creates a new {@code ShowViewMenu} with an id of {@value #ID}.
     */
    public ShowViewMenu() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationWindow(ApplicationWindow window) {
        this.window = window;
    }

    /**
     * Called after dependencies have been set, populates this menu with action command objects 
     * that will each show a given view when executed. The collection of 'show view' commands will
     * be determined by querying the {@link ViewDescriptorRegistry} retrieved from 
     * {@link ApplicationServices}. 
     */
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        //TODO should this be confirming that 'this.window' is not null?
        populate();
    }

    private void populate() {
        ViewDescriptorRegistry viewDescriptorRegistry 
                = (ViewDescriptorRegistry) ApplicationServicesLocator
                                           .services()
                                           .getService(ViewDescriptorRegistry.class);
        
        ViewDescriptor[] views = viewDescriptorRegistry.getViewDescriptors();
        for( int i = 0; i < views.length; i++ ) {
            ViewDescriptor view = views[i];
            addInternal(view.createShowViewCommand(window));
        }
    }
    
}
