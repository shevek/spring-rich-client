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
package org.springframework.richclient.form.builder.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.util.ObjectUtils;

/**
 * Adds a "dirty overlay" to a component that is triggered by user editing. The overlaid
 * image is retrieved by the image key "dirty.overlay". The image is placed at the
 * top-left corner of the component, and the image's tooltip is set to a message
 * (retrieved with key "dirty.message") such as "{field} has changed, original value was
 * {value}.".
 * 
 * @author Peter De Bruycker
 */
public class DirtyIndicatorInterceptorFactory implements FormComponentInterceptorFactory {
    private static final String DEFAULT_ICON_KEY = "dirty.overlay";
    private static final String DEFAULT_MESSAGE_KEY = "dirty.message";

    private String iconKey = DEFAULT_ICON_KEY;

    public FormComponentInterceptor getInterceptor( FormModel formModel ) {
        IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        return new DirtyIndicatorInterceptor( formModel, iconSource.getIcon( iconKey ) );
    }

    public void setIconKey( String iconKey ) {
        this.iconKey = iconKey;
    }

    public String getIconKey() {
        return iconKey;
    }

    private static class DirtyIndicatorInterceptor extends AbstractFormComponentInterceptor {
        private Icon icon;

        private DirtyIndicatorInterceptor( FormModel formModel, Icon icon ) {
            super( formModel );
            this.icon = icon;
        }

        public void processComponent( final String propertyName, final JComponent component ) {
            final JLabel overlay = new JLabel( icon );

            final OriginalValueHolder originalValueHolder = new OriginalValueHolder();
            final ValueHolder reset = new ValueHolder( Boolean.FALSE );
            getFormModel().getValueModel( propertyName ).addValueChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if( reset.getValue() == Boolean.TRUE ) {
                        originalValueHolder.reset();
                        reset.setValue( Boolean.FALSE );

                        overlay.setVisible( false );

                        return;
                    }

                    if( !originalValueHolder.isInitialized() ) {
                        originalValueHolder.setOriginalValue( evt.getOldValue() );
                    }

                    if( isDirty(originalValueHolder.getValue(), evt.getNewValue()) ) {
                        MessageSource messageSource = (MessageSource)ApplicationServicesLocator.services().getService(MessageSource.class);
                        String tooltip = messageSource.getMessage(
                                DEFAULT_MESSAGE_KEY,
                                new Object[] {
                                        messageSource.getMessage( propertyName + ".label", null,
                                                Locale.getDefault() ), originalValueHolder.getValue() }, null );
                        overlay.setToolTipText( tooltip );
                        overlay.setVisible( true );
                    } else {
                        overlay.setVisible( false );
                    }
                }
            } );
            getFormModel().getFormObjectHolder().addValueChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    // reset original value, new "original" value is in the form model as
                    // the form object has changed
                    reset.setValue( Boolean.TRUE );
                }
            } );

            InterceptorOverlayHelper.attachOverlay( overlay, component, SwingConstants.NORTH_WEST, 0, 0 );
            overlay.setVisible( false );
        }

        private boolean isDirty( Object oldValue, Object newValue ) {
            if( oldValue == null && newValue instanceof String ) {
                // hack for string comparison: null equals empty string
                return !ObjectUtils.nullSafeEquals( "", newValue );
            } 
            
            return !ObjectUtils.nullSafeEquals( oldValue, newValue );
        }
    }

    private static class OriginalValueHolder {
        private boolean initialized;
        private Object originalValue;

        public void setOriginalValue( Object value ) {
            initialized = true;
            originalValue = value;
        }

        public void reset() {
            initialized = false;
            originalValue = null;
        }

        public Object getValue() {
            return originalValue;
        }

        public boolean isInitialized() {
            return initialized;
        }
    }
}
