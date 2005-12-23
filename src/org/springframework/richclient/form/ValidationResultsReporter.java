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

import org.springframework.binding.validation.ValidationListener;

public interface ValidationResultsReporter extends ValidationListener {

    /**
     * Return the "has errors" status of the validation results model(s).
     * @return true if this model or any child model is marked as having errors
     */
    public boolean hasErrors();

    /**
     * Add a child validation results reporter. The error status of the child will feed
     * into the determination of the error status for this reporter. If the child has
     * errors, then this object's <code>hasErrors</code> will return true. If the child
     * has no errors, then return value of this object's <code>hasErrors</code> will
     * depend on it's direct validation results model and the <code>hasErrors</code>
     * status of any other child reporter.
     * 
     * @param child to add
     */
    public void addChild(ValidationResultsReporter child);

    /**
     * Get the parent results reporter. If this reporter has not been added as a child to
     * some other reporter, then this will return null.
     * @return parent reporter or null
     */
    public ValidationResultsReporter getParent();

    /**
     * Set the parent reporter.
     * @param parent New parent reporter
     */
    public void setParent(ValidationResultsReporter parent);
}