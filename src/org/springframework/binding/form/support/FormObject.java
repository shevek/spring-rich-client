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
package org.springframework.binding.form.support;

import org.springframework.binding.value.PropertyChangePublisher;

/**
 * Interface to be implemented by backing form objects; e.g unbuffered,
 * domain-specific form models.
 * 
 * @author Keith Donald
 */
public interface FormObject extends PropertyChangePublisher {
    
    /**
     * Does this form object have changes that have not yet been committed? This
     * can be used to determine if a warning needs to be displayed when a user
     * closes the GUI displaying the form without first committing their
     * changes, for example.
     * 
     * @return the dirty status
     */
    public boolean isDirty();
}