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
package org.springframework.binding.form;

import java.util.Map;

/**
 * Interface to be implemented by objects that can resolve a FieldFace for a given form model or context id and a field path.
 * 
 * @author Oliver Hutchison
 * @author Mathias Broekelmann
 * @see org.springframework.binding.form.support.MessageSourceFieldFaceSource
 */
public interface FieldFaceSource {

    /**
     * Return the FieldFace for the given form model and form field.
     * 
     * @param formModel
     *            the form model for which the FieldFace is being resolved
     * @param field
     *            the form field
     * @return the FieldFace for the given form model and field (never null).
     */
    FieldFace getFieldFace(FormModel formModel, String field);

    /**
     * Return the FieldFace for the given field name and a context.
     * 
     * @param field
     *            the field name
     * @param contextId
     *            optional context id for the field face
     * @return the FieldFace for the given field (never null).
     * @throws IllegalArgumentException
     *             if field is null or empty
     */
    FieldFace getFieldFace(String field, String contextId);

    /**
     * Return the FieldFace for the given field name a context and a map containing values to create the field face.
     * 
     * @param field
     *            the field name
     * @param contextId
     *            optional context id for the field face
     * @param context
     *            contains optional context values
     * @return the FieldFace for the given field (never null).
     * @throws IllegalArgumentException
     *             if field is null or empty
     */
    FieldFace getFieldFace(String field, String contextId, Map context);
}
