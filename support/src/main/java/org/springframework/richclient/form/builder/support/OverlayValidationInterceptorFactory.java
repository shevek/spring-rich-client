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
package org.springframework.richclient.form.builder.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.validation.Severity;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.DefaultMessageAreaModel;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.util.OverlayHelper;

/**
 * Adds an "overlay" to a component that is triggered by a validation event. The overlaid
 * image is retrieved by the image key "severity.{severityShortCode}.overlay", where
 * {severityShortCode} is the number returned by {@link Severity#getShortCode()}. The
 * image is placed at the bottom-left corner of the component, and the image's tooltip is
 * set to the validation message.
 * 
 * @author Oliver Hutchison
 * @see OverlayHelper#attachOverlay
 */
public class OverlayValidationInterceptorFactory implements FormComponentInterceptorFactory {

    private int textCompHeight;

    public OverlayValidationInterceptorFactory() {
        textCompHeight = new JTextField().getPreferredSize().height;
    }

    public FormComponentInterceptor getInterceptor( FormModel formModel ) {
        return new OverlayValidationInterceptor(formModel);
    }

    public class OverlayValidationInterceptor extends ValidationInterceptor {

        public OverlayValidationInterceptor( FormModel formModel ) {
            super(formModel);
        }

        public void processComponent( String propertyName, final JComponent component ) {
            final ErrorReportingOverlay overlay = new ErrorReportingOverlay();

            registerGuarded(propertyName, overlay);
            registerMessageReceiver(propertyName, overlay);

            if( component.getParent() == null ) {
                PropertyChangeListener waitUntilHasParentListener = new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent e ) {
                        if( component.getParent() != null ) {
                            component.removePropertyChangeListener("ancestor", this);
                            attachOverlay(overlay, component);
                        }
                    }
                };
                component.addPropertyChangeListener("ancestor", waitUntilHasParentListener);
            } else {
                attachOverlay(overlay, component);
            }
        }

        private void attachOverlay( ErrorReportingOverlay overlay, JComponent component ) {
            int yOffset = component.getPreferredSize().height;
            InterceptorOverlayHelper.attachOverlay(overlay, component, SwingConstants.NORTH_WEST, 0, Math.min(yOffset,
                    textCompHeight));
        }
    }

    private class ErrorReportingOverlay extends JLabel implements Messagable, Guarded {
        private DefaultMessageAreaModel messageBuffer = new DefaultMessageAreaModel(this);

        public boolean isEnabled() {
            return true;
        }

        public void setEnabled( boolean enabled ) {
            setVisible(!enabled);
        }

        public void setMessage( Message message ) {
            messageBuffer.setMessage(message);
            message = messageBuffer.getMessage();
            setToolTipText(message.getText());
            Severity severity = message.getSeverity();

            if( severity != null ) {
                IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
                setIcon(iconSource.getIcon("severity." + severity.getLabel() + ".overlay"));
            } else {
                setIcon(null);
            }
        }
    }
}
