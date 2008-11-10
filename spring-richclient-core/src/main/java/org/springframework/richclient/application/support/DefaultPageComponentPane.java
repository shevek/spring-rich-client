/*
 * Copyright 2002-2006 the original author or authors.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.components.SimpleInternalFrame;
import org.springframework.richclient.factory.AbstractControlFactory;

/**
 * A <code>DefaultPageComponentPane</code> puts the <code>PageComponent</code> inside
 * a <code>SimpleInternalFrame</code>.
 * 
 * @author Peter De Bruycker
 * 
 */
public class DefaultPageComponentPane extends AbstractControlFactory implements PageComponentPane {
    private PageComponent component;

    private PropertyChangeListener updater = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            handleViewPropertyChange();
        }
    };

    public DefaultPageComponentPane( PageComponent component ) {
        this.component = component;
        this.component.addPropertyChangeListener( updater );
    }

    public PageComponent getPageComponent() {
        return component;
    }

    protected JComponent createControl() {
        return new SimpleInternalFrame( component.getIcon(), component.getDisplayName(), createViewToolBar(), component
                .getControl() );
    }

    protected JToolBar createViewToolBar() {
        // todo
        return null;
    }

    public void propertyChange( PropertyChangeEvent evt ) {
        handleViewPropertyChange();
    }

    protected void handleViewPropertyChange() {
        SimpleInternalFrame frame = (SimpleInternalFrame) getControl();
        frame.setTitle( component.getDisplayName() );
        frame.setFrameIcon( component.getIcon() );
        frame.setToolTipText( component.getCaption() );
    }
}
