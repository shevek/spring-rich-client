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
package org.springframework.richclient.application.docking.flexdock;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.docking.drag.effects.EffectsManager;
import org.flexdock.perspective.PerspectiveFactory;
import org.flexdock.perspective.PerspectiveManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

/**
 * Factory for <code>FlexDockApplicationPage</code> instances
 * 
 * @author Peter De Bruycker
 */
public class FlexDockApplicationPageFactory implements ApplicationPageFactory, InitializingBean {

    private PerspectiveFactory perspectiveFactory;
    private String defaultPerspective;
    private DragPreview dragPreview;
    private boolean floatingEnabled;
    private boolean singleTabsAllowed;

    public String getDefaultPerspective() {
        return defaultPerspective;
    }

    public void setDefaultPerspective( String defaultPerspective ) {
        this.defaultPerspective = defaultPerspective;
    }

    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
        final FlexDockApplicationPage page = new FlexDockApplicationPage();
        page.setApplicationWindow( window );
        page.setDescriptor( descriptor );

        DockingManager.setDockableFactory( page );
        // TODO uncomment for persistence
        // DockingManager.setAutoPersist(true);

        PerspectiveManager.setFactory( perspectiveFactory );
        PerspectiveManager.getInstance().setCurrentPerspective( defaultPerspective, true );
        // TODO define how the file name or persister will be passed in the app context
        // PersistenceHandler persister = FilePersistenceHandler.createDefault(
        // "test-flexdock.xml" );
        // PerspectiveManager.setPersistenceHandler( persister );

        page.loadLayout();

        return page;
    }

    public void setPerspectiveFactory( PerspectiveFactory perspectiveFactory ) {
        this.perspectiveFactory = perspectiveFactory;
    }

    public PerspectiveFactory getPerspectiveFactory() {
        return perspectiveFactory;
    }

    public void afterPropertiesSet() throws Exception {
        if( dragPreview != null ) {
            EffectsManager.setPreview( dragPreview );
        }

        DockingManager.setFloatingEnabled( floatingEnabled );
        DockingManager.setSingleTabsAllowed( singleTabsAllowed );
    }

    public DragPreview getDragPreview() {
        return dragPreview;
    }

    public void setDragPreview( DragPreview dragPreview ) {
        this.dragPreview = dragPreview;
    }

    public boolean isFloatingEnabled() {
        return floatingEnabled;
    }

    public void setFloatingEnabled( boolean floatingEnabled ) {
        this.floatingEnabled = floatingEnabled;
    }
}
