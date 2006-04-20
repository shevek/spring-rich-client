/*
 * Copyright 2002-2006 the original author or authors.
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
package $package;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.application.support.AbstractView;

/**
 * This class defines the initial view to be presented in the archetypeapplication. It is
 * constructed automatically by the platform and configured according to the bean
 * specification in the application context.
 * 
 * @author Larry Streepy
 * 
 */
public class InitialView extends AbstractView {

    /**
     * Create the actual UI control for this view. It will be placed into the window
     * according to the layout of the page holding this view.
     */
    protected JComponent createControl() {
        // In this view, we're just going to use standard Swing to place a
        // few controls.


        JLabel lblMessage = getComponentFactory().createLabel("initialView.message");
        lblMessage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel panel = getComponentFactory().createPanel(new BorderLayout());
        panel.add(lblMessage);

        return panel;
    }
}
