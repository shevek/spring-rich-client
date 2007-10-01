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
import org.springframework.richclient.factory.ControlFactory;

/**
 * A page component is displayed within an area on the page
 * associated with an application window. There can be multiple components
 * per page; a single page component can only be displayed once on a
 * single page.
 *
 * Components instances encapsulate the creation of and access to the visual
 * presentation of the underlying control. A component's descriptor --
 * which is effectively a singleton -- can be asked to instantiate new
 * instances of a single page component for display within an application
 * with multiple windows. In other words, a single page component instance is
 * never shared between windows.
 *
 * @author Keith Donald
 */
public interface PageComponent extends PropertyChangePublisher, DescribedElement, VisualizedElement, ControlFactory {
    public PageComponentContext getContext();

    public void componentOpened();

    public void componentFocusGained();

    public void componentFocusLost();

    public void componentClosed();

    public void dispose();

    public void setContext(PageComponentContext context);

    public void setDescriptor(PageComponentDescriptor pageComponentDescriptor);

    public String getId();
    
    boolean canClose();
    
    public void close();
}
