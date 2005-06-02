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
package org.springframework.richclient.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;

/**
 * If a form property has an entry "caption.propertyName" in the
 * messages.properties file the following happens: When component receives
 * focus, the value is shown in the statusbar, when the component loses focus,
 * the statusbar is emptied.
 * 
 * @author Peter De Bruycker
 */
public class FocusIndicatorInterceptorFactory implements FormComponentInterceptorFactory {

    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new FocusIndicatorInterceptor(formModel);
    }

    private class FocusIndicatorInterceptor extends AbstractFormComponentInterceptor {

        private FormModel formModel;

        public FocusIndicatorInterceptor(FormModel formModel) {
            this.formModel = formModel;
        }

        private String getCaption(String propertyName) {
            return formModel.getFormPropertyFaceDescriptor(propertyName).getCaption();
        }

        public void processComponent(final String propertyName, final JComponent component) {
            component.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (Application.instance().getActiveWindow() != null) {
                        String caption = getCaption(propertyName);
                        if (caption != null) {
                            Application.instance().getActiveWindow().getStatusBar().setMessage(caption);
                        }
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (Application.instance().getActiveWindow() != null) {
                        Application.instance().getActiveWindow().getStatusBar().setMessage("");
                    }
                }
            });
        }
    }
}