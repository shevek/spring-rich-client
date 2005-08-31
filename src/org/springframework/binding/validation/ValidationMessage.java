/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.validation;

public interface ValidationMessage {
    
    /**
     * The property name for messages that have a global scope i.e. do not
     * apply to a specific property.
     */
    public static final String GLOBAL_PROPERTY = null;
    
    /**
     * The time that this validation message was created.
     */
    long getTimeStamp();

    /**
     * The property that this validation message applies to; or 
     * <code>GLOBAL_PROPERTY</code> if this message does not apply
     * to a specific property.
     */
    String getProperty();

    /**
     * The severity of this message.
     */
    Severity getSeverity();

    /**
     * The text of this message. If any i18n is applcable this must 
     * have already been allied to this message.
     */
    String getMessage();
}
