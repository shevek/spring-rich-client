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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * If a form property has an entry "caption.propertyName" in the
 * messages.properties file the following happens: When component receives
 * focus, the value is shown in the statusbar, when the component loses focus,
 * the statusbar is emptied.
 * 
 * @author Peter De Bruycker
 */
public class FocusIndicatorInterceptorFactory implements FormComponentInterceptorFactory, InitializingBean {

    private MessageSourceAccessor messages;

    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new FocusIndicatorInterceptor(formModel, messages);
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    private class FocusIndicatorInterceptor extends AbstractFormComponentInterceptor {

        private FormModel formModel;

        private MessageSourceAccessor messages;

        public FocusIndicatorInterceptor(FormModel formModel, MessageSourceAccessor messages) {
            this.formModel = formModel;
            this.messages = messages;
        }

        protected String[] getMessageKeys(String formProperty) {
            String id = null;
            if (formModel instanceof SwingFormModel) {
                id = ((SwingFormModel) formModel).getId();
            }

            boolean hasFormId = StringUtils.hasText(id);
            String[] keys = new String[hasFormId ? 5 : 3];
            int i = 0;
            if (hasFormId) {
                keys[i++] = id + ".caption." + formProperty;
            }
            keys[i++] = "caption." + formProperty;

            if (hasFormId) {
                keys[i++] = id + ".label." + formProperty;
            }
            keys[i++] = "label." + formProperty;
            keys[i] = formProperty;

            return keys;
        }

        private String getCaption(String propertyName) {
            final String[] messageKeys = getMessageKeys(propertyName);
            MessageSourceResolvable resolvable = new MessageSourceResolvable() {

                public String[] getCodes() {
                    return messageKeys;
                }

                public Object[] getArguments() {
                    return null;
                }

                public String getDefaultMessage() {
                    if (messageKeys.length > 0) {
                        return messageKeys[0];
                    }
                    return null;
                }
            };
            return LabelInfoFactory.createLabelInfo(messages.getMessage(resolvable)).getText();
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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.state(messages != null, "messageSource must be set");
    }
}