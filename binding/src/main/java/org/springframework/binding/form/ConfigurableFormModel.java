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
package org.springframework.binding.form;

import org.springframework.binding.value.ValueModel;

/**
 * Sub-interface implemented by form models that allow for configuration
 * of the form's value models, id etc..
 *
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public interface ConfigurableFormModel extends FormModel {
    public void setId(String id);

    public void setEnabled(boolean enabled);

    public void setReadOnly(boolean readOnly);

    public ValueModel add(String propertyName);

    public ValueModel add(String propertyName, ValueModel valueModel);

    public ValueModel addMethod(String propertyMethodName, String derivedFromProperty);

    public ValueModel addMethod(String propertyMethodName, String[] derivedFromProperties);
}