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

public interface PageComponentDescriptor extends PropertyChangePublisher,
        DescribedElement, VisualizedElement {

    public String getId();

    /**
     * Factory method that produces a new instance of the PageComponent
     * described by this descriptor each time it is called. The new
     * page component instance is instantiated (it must have a default
     * constructor), and any configured properties are injected. If the
     * page component is an instance of ApplicationListener, and an
     * ApplicationEventMulticaster is configured in this application's
     * ApplicationContext, the component is registered as an
     * ApplicationListener.
     *
     * @return The new page component prototype
     */
    public PageComponent createPageComponent();

}
