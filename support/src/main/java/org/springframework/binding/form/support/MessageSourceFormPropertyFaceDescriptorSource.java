/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form.support;

import javax.swing.Icon;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.image.IconSource;
import org.springframework.util.StringUtils;

/**
 * An implementation of FormPropertyFaceDescriptorSource that resolves the 
 * FormPropertyFaceDescriptor from the <code>MessageSourceAccessor</code> provided to the 
 * setMessageSourceAccessor method or from the {@link ApplicationServices} 
 * singleton if none is provided. 
 * 
 * <p>The various properties of the FormPropertyFaceDescriptor are resolved from the message 
 * source using message keys in the following order:
 * 
 * <p><code>{formModelId}.{formPropertyPath}.{faceDescriptorProperty}</code><br>
 * <code>{formPropertyPath}.{faceDescriptorProperty}</code><br>
 * 
 * <p>Where <code>{formModelId}</code> is the id of the form model, 
 * <code>{formPropertyPath}</code> is the form property path being resolved and 
 * <code>{faceDescriptorProperty}</code> is one of <code>displayName, caption, 
 * description</code> or <code>label</code>.
 * <p>
 * If required the strategy for generating these key can be overridden be providing an 
 * alternative implementation of the getMessageKeys method.
 * 
 * @author Oliver Hutchison
 */
public class MessageSourceFormPropertyFaceDescriptorSource extends AbstractCachingPropertyFaceDescriptorSource {

    /**
     * Name for the FormPropertyFaceDescriptor's <code>displayName</code> property.
     */
    private static final String DISPLAY_NAME_PROPERTY = "displayName";

    /**
     * Name for the FormPropertyFaceDescriptor's <code>caption</code> property.
     */
    private static final String CAPTION_PROPERTY = "caption";

    /**
     * Name for the FormPropertyFaceDescriptor's <code>description</code> property.
     */
    private static final String DESCRIPTION_PROPERTY = "description";

    /**
     * Name for the FormPropertyFaceDescriptor's <code>labelInfo</code> property.
     */
    private static final String ENCODED_LABEL_PROPERTY = "label";

    /**
     * Name for the FormPropertyFaceDescriptor's <code>icon</code> property.
     */
    private static final String ICON_PROPERTY = "icon";

    private MessageSourceAccessor messageSourceAccessor;

    private IconSource iconSource;

    /**
     * Constructs a new MessageSourcePropertyFaceDescriptorSource.
     */
    public MessageSourceFormPropertyFaceDescriptorSource() {
    }

    protected FormPropertyFaceDescriptor loadFormPropertyFaceDescriptor(FormModel formModel, String formPropertyPath) {
        String displayName = getMessage(formModel, formPropertyPath, DISPLAY_NAME_PROPERTY);
        String caption = getMessage(formModel, formPropertyPath, CAPTION_PROPERTY);
        String description = getMessage(formModel, formPropertyPath, DESCRIPTION_PROPERTY);
        String encodedLabel = getMessage(formModel, formPropertyPath, ENCODED_LABEL_PROPERTY);
        Icon icon = getIconSource().getIcon(getMessage(formModel, formPropertyPath, ICON_PROPERTY));
        return new DefaultFormPropertyFaceDescriptor(displayName, caption, description, encodedLabel, icon);
    }

    /**
     * Set the message source that will be used to resolve the 
     * FormPropertyFaceDescriptor's properties.
     */
    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * If a message source was provided to the setMessageSourceAccessor method 
     * returns that otherwise returns the default message source located using 
     * the {@link ApplicationServices} singleton 
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = Application.services().getMessages();
        }
        return messageSourceAccessor;
    }

    /**
     * Set the icon source that will be used to resolve the 
     * FormPropertyFaceDescriptor's icon property.
     */
    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    protected IconSource getIconSource() {
        if (iconSource == null) {
            iconSource = Application.services().getIconSource();
        }
        return iconSource;
    }

    /**
     * Returns the value of the required property of the FormPropertyFaceDescriptor. Delegates to the 
     * getMessageKeys for the message key generation strategy.  
     */
    protected String getMessage(FormModel formModel, String formPropertyPath, String faceDescriptorProperty) {
        String[] keys = getMessageKeys(formModel, formPropertyPath, faceDescriptorProperty);
        return getMessageSourceAccessor().getMessage(new DefaultMessageSourceResolvable(keys, null, keys[0]));
    }

    /**
     * Returns an array of message keys that are used to resolve the required property of the 
     * FormPropertyFaceDescriptor. The property will be resolved from the message source using the returned 
     * message keys in order. 
     * <p>Subclasses my override this method to provide an alternative to the default message 
     * key generation strategy.
     */
    protected String[] getMessageKeys(FormModel formModel, String formPropertyPath, String faceDescriptorProperty) {
        boolean hasFormId = StringUtils.hasText(formModel.getId());
        String[] formPropertyPathElements = StringUtils.delimitedListToStringArray(formPropertyPath, ".");
        String[] keys = new String[hasFormId ? 2*formPropertyPathElements.length : formPropertyPathElements.length];
        int startIndex = 0;
        if (hasFormId) {
            String prefix = formModel.getId() + '.';
            insertKeys(keys, startIndex, prefix, formPropertyPathElements, faceDescriptorProperty);
            startIndex = formPropertyPathElements.length;
        }
        insertKeys(keys, startIndex, "", formPropertyPathElements, faceDescriptorProperty);   
        return keys; 
    }

    private void insertKeys(String[] keys, int startIndex, String prefix, String[] formPropertyPathElements, String suffix) {
        for (int i=0; i<formPropertyPathElements.length; i++) {         
            StringBuffer path = new StringBuffer();
            for (int j=i; j<formPropertyPathElements.length; j++) {
                path.append(formPropertyPathElements[j]).append('.');
            }                
            keys[startIndex++] = prefix + path + suffix;
        }
    }
}