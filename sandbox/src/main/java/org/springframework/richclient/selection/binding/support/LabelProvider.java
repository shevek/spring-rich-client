/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.selection.binding.support;

/**
 * Strategy interface to determine the text for a given item.
 * 
 * @author Peter De Bruycker
 */
public interface LabelProvider {
    /**
     * Returns the text for the given item.
     * <p>
     * Warning: the item can be null.
     * 
     * @param item
     *            the item
     * @return the text
     */
    String getLabel(Object item);
}
