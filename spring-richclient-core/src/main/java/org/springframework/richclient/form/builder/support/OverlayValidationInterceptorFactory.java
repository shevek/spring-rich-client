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

import javax.swing.*;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.components.MessageReportingOverlay;
import org.springframework.richclient.components.MayHaveMessagableTab;
import org.springframework.richclient.components.MessagableTab;
import org.springframework.richclient.components.PanelWithValidationComponent;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.HasValidationComponent;
import org.springframework.richclient.util.OverlayHelper;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.dialog.DefaultMessageAreaModel;
import org.springframework.richclient.dialog.Messagable;

public class OverlayValidationInterceptorFactory implements FormComponentInterceptorFactory
{
    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new OverlayValidationInterceptor(formModel);
    }

    public class OverlayValidationInterceptor extends ValidationInterceptor {

        public OverlayValidationInterceptor(FormModel formModel) {
            super(formModel);
        }

        public void processComponent(String propertyName, final JComponent component) {
            final ErrorReportingOverlay overlay = new ErrorReportingOverlay();

            registerGuarded(propertyName, overlay);
            registerMessageReceiver(propertyName, overlay);

            if (component.getParent() == null) {
                PropertyChangeListener waitUntilHasParentListener = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        if (component.getParent() != null) {
                            component.removePropertyChangeListener("ancestor", this);
                            attachOverlay(overlay, component);
                        }
                    }
                };
                component.addPropertyChangeListener("ancestor", waitUntilHasParentListener);
            }
            else {
                attachOverlay(overlay, component);
            }
        }

        private void attachOverlay(ErrorReportingOverlay overlay, JComponent component) {
            JComponent componentToOverlay;
            if (component instanceof HasValidationComponent)
                componentToOverlay = ((HasValidationComponent) component).getValidationComponent();
            else
                componentToOverlay = hasParentScrollPane(component) ? getParentScrollPane(component) : component;
            int yOffset = componentToOverlay.getPreferredSize().height;
            OverlayHelper.attachOverlay(overlay, componentToOverlay, SwingConstants.NORTH_WEST, 0, Math.min(yOffset,
                    new JTextField().getPreferredSize().height));
        }

        private JScrollPane getParentScrollPane(JComponent component) {
            return (JScrollPane)component.getParent().getParent();
        }

        private boolean hasParentScrollPane(JComponent component) {
            return component.getParent() != null && component.getParent() instanceof JViewport
                    && component.getParent().getParent() instanceof JScrollPane;
        }
    }

    private static class ErrorReportingOverlay extends JLabel implements Messagable, Guarded, MayHaveMessagableTab
    {

        private DefaultMessageAreaModel messageBuffer = new DefaultMessageAreaModel(this);
        private MessagableTab messagableTab = null;
        private int tabIndex = 0;

        public boolean isEnabled()
        {
            return true;
        }

        public void setEnabled(boolean enabled)
        {
            setVisible(!enabled);
        }

        public void setMessagableTab(MessagableTab messagableTab, int tabIndex)
        {
            this.messagableTab = messagableTab;
            this.tabIndex = tabIndex;
        }

        public void setMessage(Message message)
        {
            // geef de messgage door aan de omringende tabbedpane als ie er is
            if (this.messagableTab != null)
                this.messagableTab.setMessage(this, message, this.tabIndex);
            messageBuffer.setMessage(message);
            message = messageBuffer.getMessage();
            setToolTipText(message.getMessage());
            Severity severity = message.getSeverity();
            if (severity != null)
                setIcon(RcpSupport.getIcon("severity." + severity.getLabel() + ".overlay"));
            else
                setIcon(null);
        }
    }
}
