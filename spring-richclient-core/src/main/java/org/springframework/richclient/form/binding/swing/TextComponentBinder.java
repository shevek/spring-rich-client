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
package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.swing.text.DocumentFactory;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.Map;

/**
 * @author Oliver Hutchison
 */
public class TextComponentBinder extends AbstractBinder {
    private String promptKey;
    private boolean convertEmptyStringToNull;
    private DocumentFactory documentFactory;
    private boolean readOnly;
    private boolean selectAllOnFocus;

    public TextComponentBinder() {    
        super(String.class);
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JTextComponent, "Control must be an instance of JTextComponent.");
        TextComponentBinding textComponentBinding = new TextComponentBinding((JTextComponent) control, formModel, formPropertyPath);
        textComponentBinding.setConvertEmptyStringToNull(convertEmptyStringToNull);
        textComponentBinding.setPromptKey(promptKey);
        textComponentBinding.setReadOnly(readOnly);
        textComponentBinding.setSelectAllOnFocus(selectAllOnFocus);
        return textComponentBinding;
    }

    protected JTextComponent createTextComponent()
    {
         return getComponentFactory().createTextField();
    }

    protected JComponent createControl(Map context) {
        JTextComponent textComponent = createTextComponent();
        if (getDocumentFactory() != null) {
            textComponent.setDocument(getDocumentFactory().createDocument());
        }
        return textComponent;
    }

    public boolean isConvertEmptyStringToNull()
    {
        return convertEmptyStringToNull;
    }

    public void setConvertEmptyStringToNull(boolean convertEmptyStringToNull)
    {
        this.convertEmptyStringToNull = convertEmptyStringToNull;
    }

    public String getPromptKey()
    {
        return promptKey;
    }

    public void setPromptKey(String promptKey)
    {
        this.promptKey = promptKey;
    }

    public DocumentFactory getDocumentFactory()
    {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory)
    {
        this.documentFactory = documentFactory;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isSelectAllOnFocus()
    {
        return selectAllOnFocus;
    }

    public void setSelectAllOnFocus(boolean selectAllOnFocus)
    {
        this.selectAllOnFocus = selectAllOnFocus;
    }
}

