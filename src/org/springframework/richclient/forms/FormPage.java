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

package org.springframework.richclient.forms;

import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.factory.ControlFactory;

/**
 * @author Keith Donald
 */
public interface FormPage 
extends ControlFactory {
    public String getId();

    public SwingFormModel getFormModel();

    public Object getFormObject();

    public ValueModel getFormObjectHolder();

    public Object getValue(String formProperty);

    public ValueModel getValueModel(String formProperty);

    public void addValidationListener(ValidationListener listener);

    public void removeValidationListener(ValidationListener listener);

    public ValidationListener newSingleLineResultsReporter(Guarded guarded,
            MessageReceiver messageAreaPane);

    public boolean hasErrors();

    public void commit();

    public void revert();
}