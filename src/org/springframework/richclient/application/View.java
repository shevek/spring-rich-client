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

import javax.swing.Icon;

import org.springframework.richclient.core.ManagedElement;
import org.springframework.richclient.factory.ControlFactory;

/**
 * A view is a panel-like component displayed within an area on the page
 * associated with an application window.  There can be multiple views per page;
 * a single view can only be displayed once on a single page.
 *  
 * View instances encapsulate the creation of and access to the visual
 * presentation of the underlying control. A view's descriptor -- which is
 * effectively a singleton -- can be asked to instantiate new instances of a
 * single view for display within an application with multiple windows. In other
 * words, a single view instance is never shared between windows.
 * 
 * @author Keith Donald
 */
public interface View extends ManagedElement, ControlFactory {
    public Icon getIcon();

    public void initialize(ViewDescriptor descriptor, ViewContext context);

    public ViewContext getContext();

    public ViewDescriptor getViewDescriptor();

}
