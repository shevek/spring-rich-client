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
package org.springframework.rules.constraint;

import java.util.Collection;

import org.springframework.core.closure.Constraint;
import org.springframework.util.StringUtils;

/**
 * Validates a required property. Required is defined as non-null and, if the
 * object is a string, not empty and not blank.
 * 
 * @author Keith Donald
 */
public class Required implements Constraint {
    private static final Required instance = new Required();

    /**
     * Tests if this argument is present (non-null, not-empty, not blank)
     * 
     * @see org.springframework.core.closure.Constraint#test(java.lang.Object)
     */
    public boolean test(Object argument) {
        if (argument != null) {
            if (argument instanceof String) {
                if (StringUtils.hasText((String)argument)) {
                    return true;
                }
            } else if (argument instanceof Collection) {
                return ((Collection)argument).size() > 0;
            } else if( argument.getClass().isArray() ) {
                return ((Object[])argument).length > 0;
            } else {
                return true;
            }
        }
        return false;
    }

    public static Constraint instance() {
        return instance;
    }
    
    public String toString() {
        return "required";
    }

}