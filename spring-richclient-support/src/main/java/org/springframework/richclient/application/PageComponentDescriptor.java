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
package org.springframework.richclient.application;

import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.richclient.core.DescribedElement;
import org.springframework.richclient.core.VisualizedElement;

/**
 * Metadata about a page component; a page component is effectively a
 * singleton page component definition. A descriptor also acts as a factory
 * which produces new instances of a given page component when requested,
 * typically by a requesting application page. A page component descriptor
 * can also produce a command which launches a page component for display
 * on the page within the current active window.
 */
public interface PageComponentDescriptor extends PropertyChangePublisher, DescribedElement, VisualizedElement {
    
    /**
     * Returns the identifier of this descriptor.
     * @return The descriptor id.
     */
    public String getId();

    /**
     * Creates the page component defined by this descriptor.
     * @return The page component, never null.
     */
    public PageComponent createPageComponent();

}
