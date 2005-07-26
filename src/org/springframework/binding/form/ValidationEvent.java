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

import org.springframework.core.closure.Constraint;
import org.springframework.core.style.ToStringCreator;
import org.springframework.rules.reporting.ValidationResults;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class ValidationEvent {
    private FormModel formModel;

    private Constraint constraint;

    private ValidationResults results;

    public ValidationEvent(FormModel formModel, Constraint constraint) {
        this(formModel, constraint, null);
    }

    public ValidationEvent(FormModel formModel, Constraint constraint, ValidationResults results) {
        Assert.notNull(formModel, "The form model property is required");
        Assert.notNull(constraint, "The constraint property is required");
        this.formModel = formModel;
        this.constraint = constraint;
        this.results = results;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ValidationEvent)) {
            return false;
        }
        ValidationEvent e = (ValidationEvent)o;
        return formModel.equals(e.formModel) && constraint.equals(e.constraint);
    }

    public int hashCode() {
        return formModel.hashCode() + constraint.hashCode();
    }

    public FormModel getFormModel() {
        return formModel;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public ValidationResults getResults() {
        return results;
    }

    public String toString() {
        return new ToStringCreator(this).toString();
    }
}