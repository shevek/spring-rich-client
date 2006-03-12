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

package org.springframework.richclient.form;

import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.factory.ControlFactory;

/**
 * @author Keith Donald
 */
public interface Form extends ControlFactory {
    public String getId();

    public ValidatingFormModel getFormModel();

    public Object getFormObject();

    public void setFormObject(Object formObject);

    public Object getValue(String formProperty);

    public ValueModel getValueModel(String formProperty);

    public void addValidationListener(ValidationListener listener);

    public void removeValidationListener(ValidationListener listener);

    public ValidationResultsReporter newSingleLineResultsReporter(Guarded guarded, Messagable messageAreaPane);

    public boolean hasErrors();

    public void commit();

    public void revert();
}