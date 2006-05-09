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
package org.springframework.binding;

import java.util.Map;

/**
 * Simple interface for accessing metadata about a particular property.
 * 
 * EXPERIMENTAL - not yet fit for general use
 * @author Keith Donald
 */
public interface PropertyMetadataAccessStrategy {
    public boolean isReadable(String propertyName);

    public boolean isWriteable(String propertyName);

    public Class getPropertyType(String propertyName);

    /**
     * Returns custom metadata that may be associated with the specified
     * property path. 
     */
    Object getUserMetadata(String propertyName, String key);

    /**
     * Returns all custom metadata associated with the specified property
     * in the form of a Map.
     *
     * @return Map containing String keys - this method may or may not return
     *         <code>null</code> if there is no custom metadata associated
     *         with the property.
     */
    Map getAllUserMetadata(String propertyName);
}