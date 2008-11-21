/*
 * Copyright 2008 the original author or authors.
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

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.springframework.richclient.application.View;

import junit.framework.TestCase;

/**
 * testcase for {@link SimpleViewDescriptor}
 * 
 * @author Peter De Bruycker
 */
public class SimpleViewDescriptorTests extends TestCase {

    public void testCreatePageComponent() {
        View view = new AbstractView() {

            @Override
            protected JComponent createControl() {
                return new JLabel("hello");
            }
        };

        SimpleViewDescriptor descriptor = new SimpleViewDescriptor(view);

        assertSame(view, descriptor.createPageComponent());
        // must always return the same
        assertSame(view, descriptor.createPageComponent());
    }

}
