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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.PersistenceException;
import org.flexdock.view.View;
import org.flexdock.view.ViewProps;
import org.flexdock.view.Viewport;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.AbstractApplicationPage;

/**
 * <code>ApplicationPage</code> that uses FlexDock.
 * 
 * @author Peter De Bruycker
 */
public class FlexDockApplicationPage extends AbstractApplicationPage implements DockableFactory {
    private Viewport port;
    private Map<String, View> dockables = new HashMap<String, View>();
    private boolean isLoadingLayout;
    private boolean creatingDockable;

    private PropertyChangeListener activeHandler = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            if( ViewProps.ACTIVE.equals( evt.getPropertyName() ) && Boolean.TRUE.equals( evt.getNewValue() ) ) {
                View view = (View) evt.getSource();
                PageComponent component = findPageComponent( view.getPersistentId() );
                setActiveComponent( component );
            }
        }
    };

    protected View createView( final PageComponent component ) {
        View view = new View( component.getId() );
        view.setTitle( component.getDisplayName() );
        view.setTabText( component.getDisplayName() );
        view.setTabIcon( component.getIcon() );
        view.setIcon( component.getIcon() );
        view.setContentPane( component.getControl() );

        view.getViewProperties().addPropertyChangeListener( activeHandler );

        configureView( component, view, getViewDescriptor( component.getId() ) );

        dockables.put( component.getId(), view );

        return view;
    }

    protected void configureView( final PageComponent component, View view, ViewDescriptor descriptor ) {
        boolean closable = true;
        boolean pinnable = true;
        boolean dockable = true;

        if( descriptor instanceof FlexDockViewDescriptor ) {
            FlexDockViewDescriptor desc = (FlexDockViewDescriptor) descriptor;
            closable = desc.isClosable();
            pinnable = desc.isPinnable();
            dockable = desc.isDockable();
        }

        if( closable ) {
            view.addAction( View.CLOSE_ACTION );
            // TODO fix this: this is the only way I found to find out if a dockable has
            // been closed by the user.
            AbstractButton btn = view.getActionButton( View.CLOSE_ACTION );
            btn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    close( component );
                }
            } );
        }

        if( pinnable ) {
            view.addAction( View.PIN_ACTION );
        }

        view.getViewProperties().setDockingEnabled( dockable );
    }

    protected JComponent createControl() {
        port = new Viewport();

        return port;
    }

    public View getFlexView( String id ) {
        return dockables.get( id );
    }

    protected void doAddPageComponent( PageComponent pageComponent ) {
        View view = createView( pageComponent );
        dockables.put( pageComponent.getId(), view );

        if( !isLoadingLayout ) {
            DockingManager.display( view );
        }
    }

    protected void doRemovePageComponent( PageComponent pageComponent ) {
        View view = getFlexView( pageComponent.getId() );
        if( view != null ) {
            DockingManager.close( (Dockable) view );

            // HACK: if we don't repaint here, when closing the last dockable the ui is
            // not updated
            port.revalidate();
            port.repaint();

            dockables.remove( view );
        }
    }

    protected boolean giveFocusTo( final PageComponent pageComponent ) {
        if( creatingDockable ) {
            return false;
        }

        View view = getFlexView( pageComponent.getId() );

        view.setActive( true );

        // HACK: otherwise the first dockable that was active will still be active
        for( Iterator iter = DockingManager.getDockableIds().iterator(); iter.hasNext(); ) {
            String id = (String) iter.next();
            if( !id.equals( pageComponent.getId() ) ) {
                getFlexView( id ).setActive( false );
            }
        }

        return true;
    }

    public void loadLayout() {
        isLoadingLayout = true;

        // the view port must be created before we attempt to load the layout model or try
        // to restore the layout
        getControl();

        try {
            DockingManager.loadLayoutModel( true );
        } catch( IOException e ) {
            e.printStackTrace();
        } catch( PersistenceException e ) {
            e.printStackTrace();
        }

        port.revalidate();
        port.repaint();

        isLoadingLayout = false;

        // mark the view associated with the active component as active
        View view = getFlexView( getActiveComponent().getId() );
        if( view != null ) {
            view.setActive( true );
        }
    }

    public Component getDockableComponent( String id ) {
        // not used, we work with dockables
        return null;
    }

    public Dockable getDockable( String id ) {
        if( dockables.containsKey( id ) ) {
            return (Dockable) dockables.get( id );
        }

        creatingDockable = true;
        showView( id );
        creatingDockable = false;
        return (Dockable) dockables.get( id );
    }
}
