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
package org.springframework.richclient.form.builder.support;

import java.awt.GraphicsEnvironment;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TestBean;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Tests for <code>DirtyIndicatorInterceptor</code>.
 *
 * @author Peter De Bruycker
 */
public class DirtyIndicatorInterceptorTests extends SpringRichTestCase {
	/**
	 * FIXME: this test will not run in a headless environment
	 */
    public void testProcessComponent() throws InterruptedException {
    	if (GraphicsEnvironment.isHeadless()) {
			return;
		}
        TestBean bean = new TestBean();
        bean.setProperty("original value");

        FormModel formModel = new DefaultFormModel(bean);

        DirtyIndicatorInterceptor interceptor = new DirtyIndicatorInterceptor(formModel);
        assertEquals(formModel, interceptor.getFormModel());

        Binding binding = new SwingBindingFactory(formModel).createBinding("property");
        JTextField field = (JTextField)binding.getControl();
        field.setColumns(25);
        assertNotNull("sanity check: binding defines no component", field);

        interceptor.processComponent("property", field);

        // start a frame to trigger visual updates
        JFrame frame = new JFrame("test");
        frame.getContentPane().add(field);
        frame.pack();
        frame.setVisible(true);

        // trigger a show of the overlay, so we can get a reference to it
        ValueModel valueModel = formModel.getValueModel("property");
        valueModel.setValue("dirty");

        // sleep for a while so the gui can update itself
        Thread.sleep(500);
        formModel.revert();

        // find a reference to the overlay component
        JLayeredPane layeredPane = frame.getRootPane().getLayeredPane();
        assertEquals("sanity check: assume the layered pane only has one component, and that it is a panel and the overlay", 
                     2, layeredPane.getComponentCount());
        // the overlay is the first component
        JComponent overlay = (JComponent)layeredPane.getComponent(0);
        // the overlay is now put into another panel for clipping.
        if(!"dirtyOverlay".equals(overlay.getName())) {
            assertEquals("Unable to locate overlay", "dirtyOverlay", overlay.getComponent(0).getName());
            overlay = (JComponent) overlay.getComponent(0);
        }

        assertFalse("Overlay must be hidden", overlay.isVisible());

        // mimic user editing
        valueModel.setValue("ttt");
        assertTrue("Value is dirty, so overlay must be visible", overlay.isVisible());

        // user reverts the edit
        valueModel.setValue("original value");
        assertFalse("value is not dirty, so overlay must be hidden", overlay.isVisible());

        // dispose of the frame
        frame.dispose();
    }

    protected void registerBasicServices(DefaultApplicationServices applicationServices) {
        super.registerBasicServices(applicationServices);

        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("dirty.message", Locale.getDefault(), "{0} has changed, original value was {1}.");
        messageSource.addMessage("revert.message", Locale.getDefault(), "Revert value to {0}.");

        messageSource.addMessage("property.label", Locale.getDefault(), "Property");

        applicationServices.setMessageSource(messageSource);
    }
}
