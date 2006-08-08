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
package org.springframework.richclient.application.mdi;

import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.support.AbstractApplicationPage;
import org.springframework.richclient.application.support.DefaultViewContext;

/**
 * @author Peter De Bruycker
 */
public class DesktopApplicationPage extends AbstractApplicationPage implements PageLayoutBuilder{

    private ScrollingDesktopPane control;

    private JScrollPane scrollPane;

    public DesktopApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        super(window, pageDescriptor);
    }

    protected boolean giveFocusTo(PageComponent pageComponent) {
        if(getActiveComponent() == pageComponent) {
            return true;
        }
        
        PageComponentPane pane = pageComponent.getContext().getPane();
        JInternalFrame internalFrame = (JInternalFrame) pane.getControl();

        try {
            internalFrame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            // ignore
        }

        pane.requestFocusInWindow();
        return true;
    }

    protected PageComponent createPageComponent(PageComponentDescriptor descriptor) {
        final PageComponent pageComponent = descriptor.createPageComponent();
        pageComponent.setContext(new DefaultViewContext(this, new DesktopPageComponentPane(this, pageComponent)));

        JInternalFrame internalFrame = (JInternalFrame) pageComponent.getContext().getPane().getControl();
        internalFrame.setVisible(true);
        control.add(internalFrame);

        return pageComponent;
    }

    protected void setActiveComponent() {
        for (Object object : getPageComponents()) {
            PageComponent pageComponent = (PageComponent) object;
            if (!((JInternalFrame) pageComponent.getContext().getPane().getControl()).isIcon()) {
                setActiveComponent(pageComponent);
                return;
            }
        }
        //no page component found that is not iconified

    }

    public JComponent getControl() {
        if (control == null) {
            control = new ScrollingDesktopPane();
            scrollPane = new JScrollPane(control);

            this.getPageDescriptor().buildInitialLayout(this);
            setActiveComponent();
        }
        return scrollPane;
    }

    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }
}
