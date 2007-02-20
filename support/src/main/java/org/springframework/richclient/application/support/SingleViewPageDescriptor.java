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
package org.springframework.richclient.application.support;

import java.awt.Image;

import javax.swing.Icon;

import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.ViewDescriptor;

public class SingleViewPageDescriptor extends AbstractPageDescriptor {

    private ViewDescriptor viewDescriptor;

    public SingleViewPageDescriptor(ViewDescriptor viewDescriptor) {
        super();
        this.viewDescriptor = viewDescriptor;
    }

    public String getId() {
        return viewDescriptor.getId();
    }

    public String getDisplayName() {
        return viewDescriptor.getDisplayName();
    }

    public String getCaption() {
        return viewDescriptor.getCaption();
    }

    public String getDescription() {
        return viewDescriptor.getDescription();
    }

    public Icon getIcon() {
        return viewDescriptor.getIcon();
    }

    public Image getImage() {
        return viewDescriptor.getImage();
    }

    public void buildInitialLayout(PageLayoutBuilder layout) {
        layout.addView(viewDescriptor.getId());
    }

}
