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
package org.springframework.richclient.application.support;

import javax.swing.JComponent;
import javax.swing.JLabel;

import junit.framework.TestCase;

import org.springframework.richclient.application.ApplicationPage;

/**
 * Abstract base testcase for {@link ApplicationPage} implementations.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractApplicationPageTestCase extends TestCase {

    private AbstractApplicationPage applicationPage;
    private TestView testView1;
    private TestView testView2;

    @Override
    protected void setUp() throws Exception {
        setUpViews();

        applicationPage = (AbstractApplicationPage) createApplicationPage();
        assertNotNull("createApplicationPage returns null", applicationPage);

        SimpleViewDescriptorRegistry viewDescriptorRegistry = new SimpleViewDescriptorRegistry();
        viewDescriptorRegistry.addViewDescriptor(new SimpleViewDescriptor("testView1", testView1));
        viewDescriptorRegistry.addViewDescriptor(new SimpleViewDescriptor("testView2", testView2));

        applicationPage.setViewDescriptorRegistry(viewDescriptorRegistry);

        applicationPage.setPageComponentPaneFactory(new SimplePageComponentPaneFactory());

        applicationPage.setDescriptor(new EmptyPageDescriptor());

        // trigger control creation
        JComponent control = applicationPage.getControl();
        assertNotNull("getControl cannot return null", control);
    }

    private void setUpViews() {
        testView1 = new TestView("this is test view 1");
        testView2 = new TestView("this is test view 2");
    }

    protected abstract ApplicationPage createApplicationPage();

    public void testShowViewAndClose() {
        assertNull(applicationPage.getView("testView1"));

        applicationPage.showView("testView1");

        TestView view = (TestView) applicationPage.getView("testView1");

        assertNotNull(view);
        assertEquals("testView1", view.getId());

        applicationPage.close(view);
        assertNull(applicationPage.getView("testView1"));
    }

    public void testShowViewWithInput() {
        Object input = "the input";

        applicationPage.showView("testView1", input);

        TestView view = applicationPage.getView("testView1");
        assertNotNull(view);

        assertTrue(view.isSetInputCalled());
        assertEquals(input, view.getInput());
    }
    
    public void testShowView() {
        assertSame(testView1, applicationPage.showView("testView1"));
        assertSame(testView1, applicationPage.getActiveComponent());
        
        assertSame(testView2, applicationPage.showView("testView2"));
        assertSame(testView2, applicationPage.getActiveComponent());
    }
    
    public void testShowViewWithoutInput() {
        applicationPage.showView("testView1");

        TestView view = applicationPage.getView("testView1");
        assertNotNull(view);

        assertFalse(view.isSetInputCalled());
    }

    public void testGetView() {
        assertNull(applicationPage.getView("testView1"));

        applicationPage.showView("testView1");

        TestView view = applicationPage.getView("testView1");

        assertNotNull(view);
        assertEquals("testView1", view.getId());

        applicationPage.close(view);
        assertNull(applicationPage.getView("testView1"));
    }

    private static class TestView extends AbstractView {

        private String label;
        private Object input;
        private boolean setInputCalled;

        public TestView(String label) {
            this.label = label;
        }

        @Override
        protected JComponent createControl() {
            return new JLabel(label);
        }

        @Override
        public void setInput(Object input) {
            this.input = input;
            setInputCalled = true;
        }

        public Object getInput() {
            return input;
        }
        
        public boolean isSetInputCalled() {
            return setInputCalled;
        }

    }
}
