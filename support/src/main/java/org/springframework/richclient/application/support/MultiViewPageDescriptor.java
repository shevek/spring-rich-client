/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;

/**
 * Base class for {@link PageDescriptor} implementations that support multiple
 * {@link PageComponent}s
 *
 * @author Peter De Bruycker
 */
public class MultiViewPageDescriptor extends AbstractPageDescriptor {

    private List viewDescriptors = new ArrayList();

    public void buildInitialLayout(PageLayoutBuilder pageLayout) {
        for (Iterator iter = viewDescriptors.iterator(); iter.hasNext();) {
            String viewDescriptorId = (String) iter.next();
            pageLayout.addView(viewDescriptorId);
        }
    }

    public List getViewDescriptors() {
        return viewDescriptors;
    }

    public void setViewDescriptors(List viewDescriptors) {
        this.viewDescriptors = viewDescriptors;
    }

    public void setBeanName(String name) {
        setId(name);
    }

}
