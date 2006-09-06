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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.swing.Icon;

import org.springframework.binding.form.FieldFace;
import org.springframework.binding.form.FormModel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.image.IconSource;
import org.springframework.util.StringUtils;

/**
 * An implementation of FieldFaceSource that resolves the FieldFace from the <code>MessageSourceAccessor</code>
 * provided to the setMessageSourceAccessor method or from the {@link ApplicationServices} singleton if none is
 * provided.
 * 
 * <p>
 * The various properties of the FieldFace are resolved from the message source using message keys in the following
 * order:
 * 
 * <p>
 * <code>{formModelId}.{formPropertyPath}.{faceDescriptorProperty}</code><br>
 * <code>{formPropertyPath}.{faceDescriptorProperty}</code><br>
 * 
 * <p>
 * Where <code>{formModelId}</code> is the id of the form model, <code>{formPropertyPath}</code> is the form
 * property path being resolved and <code>{faceDescriptorProperty}</code> is one of <code>displayName, caption, 
 * description</code>
 * or <code>label</code>.
 * <p>
 * If required the strategy for generating these key can be overridden be providing an alternative implementation of the
 * getMessageKeys method.
 * 
 * @author Oliver Hutchison
 */
public class MessageSourceFieldFaceSource extends CachingFieldFaceSource {

    /**
     * Name for the FieldFace's <code>displayName</code> property.
     */
    private static final String[] DISPLAY_NAME_PROPERTY = { "displayName" };

    /**
     * Name for the FieldFace's <code>caption</code> property.
     */
    private static final String[] CAPTION_PROPERTY = { "caption" };

    /**
     * Name for the FieldFace's <code>description</code> property.
     */
    private static final String[] DESCRIPTION_PROPERTY = { "description" };

    /**
     * Name for the FieldFace's <code>labelInfo</code> property.
     */
    private static final String[] ENCODED_LABEL_PROPERTY = { "label", "" };

    /**
     * Name for the FieldFace's <code>icon</code> property.
     */
    private static final String[] ICON_PROPERTY = { "icon" };

    private MessageSourceAccessor messageSourceAccessor;

    private IconSource iconSource;

    /**
     * Constructs a new MessageSourcePropertyFaceDescriptorSource.
     */
    public MessageSourceFieldFaceSource() {
    }

    protected FieldFace loadFieldFace(FormModel formModel, String formFieldPath) {
        return loadFieldFace(formFieldPath, formModel.getId());
    }

    /**
     * Set the message source that will be used to resolve the FieldFace's properties.
     */
    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * If a message source was provided to the setMessageSourceAccessor method returns that otherwise returns the
     * default message source located using the {@link ApplicationServices} singleton
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);
        }
        return messageSourceAccessor;
    }

    /**
     * Set the icon source that will be used to resolve the FieldFace's icon property.
     */
    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    protected IconSource getIconSource() {
        if (iconSource == null) {
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        }
        return iconSource;
    }

    /**
     * Returns the value of the required property of the FieldFace. Delegates to the getMessageKeys for the message key
     * generation strategy.
     */
    protected String getMessage(String contextId, String formPropertyPath, Map context, String[] faceDescriptorProperty) {
        String[] keys = getMessageKeys(contextId, formPropertyPath, faceDescriptorProperty);
        Object[] arguments = null;
        if (faceDescriptorProperty != null) {
            arguments = (Object[]) context.get(faceDescriptorProperty);
        }
        return getMessageSourceAccessor().getMessage(new DefaultMessageSourceResolvable(keys, arguments, keys[0]));
    }

    /**
     * Returns an array of message keys that are used to resolve the required property of the FieldFace. The property
     * will be resolved from the message source using the returned message keys in order.
     * <p>
     * Subclasses my override this method to provide an alternative to the default message key generation strategy.
     */
    protected String[] getMessageKeys(String contextId, String fieldPath, String[] faceDescriptorProperties) {
        boolean hasFormId = StringUtils.hasText(contextId);
        String[] fieldPathElements = StringUtils.delimitedListToStringArray(fieldPath, ".");
        Collection keys = new ArrayList(hasFormId ? 2 * fieldPathElements.length : fieldPathElements.length);
        if (hasFormId) {
            String prefix = contextId + '.';
            addKeys(keys, prefix, fieldPathElements, faceDescriptorProperties);
        }
        addKeys(keys, "", fieldPathElements, faceDescriptorProperties);
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    private void addKeys(Collection keys, String prefix, String[] fieldPathElements, String[] suffix) {
        int size = fieldPathElements.length;
        int suffixSize = suffix.length;
        for (int i = 0; i < size; i++) {
            StringBuffer path = new StringBuffer(prefix);
            for (int j = i; j < size; j++) {
                path.append(fieldPathElements[j]);
                if (j + 1 < size) {
                    path.append('.');
                }
            }
            for (int j = 0; j < suffixSize; j++) {
                String currentSuffix = suffix[j];
                if (StringUtils.hasText(currentSuffix)) {
                    keys.add(path.toString() + "." + currentSuffix);
                } else {
                    keys.add(path.toString());
                }
                    
            }
        }
    }

    protected FieldFace loadFieldFace(String field, String contextId, Map context) {
        String displayName = getMessage(contextId, field, context, DISPLAY_NAME_PROPERTY);
        String caption = getMessage(contextId, field, context, CAPTION_PROPERTY);
        String description = getMessage(contextId, field, context, DESCRIPTION_PROPERTY);
        String encodedLabel = getMessage(contextId, field, context, ENCODED_LABEL_PROPERTY);
        if (encodedLabel == null) {
            // try loading the default value
            encodedLabel = getMessage(contextId, field, context, null);
        }
        Icon icon = getIconSource().getIcon(getMessage(contextId, field, context, ICON_PROPERTY));
        return new DefaultFieldFace(displayName, caption, description, encodedLabel, icon);
    }

    protected FieldFace loadFieldFace(String field, String contextId) {
        return loadFieldFace(field, contextId, Collections.EMPTY_MAP);
    }
}