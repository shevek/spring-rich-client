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
package org.springframework.richclient.form.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.easymock.EasyMock;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Skeleton Testcase for <code>AbstractFormBuilder</code>.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractFormBuilderTestCase extends SpringRichTestCase {
	private AbstractFormBuilder formBuilder;
	private FormModel formModel;
	private ComponentFactory mockComponentFactory;
	private FormComponentInterceptorFactory mockInterceptorFactory;
	
	public void testCreateTextArea() {
		final JTextArea textArea = new JTextArea();
		
		EasyMock.expect(mockComponentFactory.createTextArea(5, 40)).andReturn(textArea);

		EasyMock.replay(mockComponentFactory);
		
		JComponent result = formBuilder.createTextArea("property");
		assertSame(textArea, result);
		
		EasyMock.verify(mockComponentFactory);
	}
	
	public void testCreateLabelWithNullInterceptor() {
		final JLabel label = new JLabel("test-label");
		
		EasyMock.expect(mockComponentFactory.createLabel("")).andReturn(label);
		EasyMock.expect(mockInterceptorFactory.getInterceptor(formModel)).andReturn(null);

		EasyMock.replay(mockComponentFactory);
		EasyMock.replay(mockInterceptorFactory);
		
		JTextField component = new JTextField();
		formBuilder.createLabelFor("property", component);
		
		EasyMock.verify(mockComponentFactory);
		EasyMock.verify(mockInterceptorFactory);
	}
	
	public void testCreateLabel() {
		final JLabel label = new JLabel("test-label");
		
		EasyMock.expect(mockComponentFactory.createLabel("")).andReturn(label);
		
		FormComponentInterceptor mockInterceptor= (FormComponentInterceptor) EasyMock.createMock(FormComponentInterceptor.class);
		mockInterceptor.processLabel("property", label);
		
		EasyMock.expect(mockInterceptorFactory.getInterceptor(formModel)).andReturn(mockInterceptor);

		EasyMock.replay(mockComponentFactory);
		EasyMock.replay(mockInterceptorFactory);
		EasyMock.replay(mockInterceptor);
		
		JTextField component = new JTextField();
		JLabel result = formBuilder.createLabelFor("property", component);
		
		assertSame(label, result);
		assertNotNull("createLabelFor cannot return null", result);
		assertEquals(component, result.getLabelFor());
		
		EasyMock.verify(mockComponentFactory);
		EasyMock.verify(mockInterceptorFactory);
		EasyMock.verify(mockInterceptor);
	}
	
	protected final void doSetUp() throws Exception {
		formModel = new DefaultFormModel(new TestBean());
		BindingFactory bindingFactory= new SwingBindingFactory(formModel);
		
		formBuilder = createFormBuilder(bindingFactory);
		assertNotNull("formBuilder cannot be null", formBuilder);
		
		mockComponentFactory= (ComponentFactory) EasyMock.createMock(ComponentFactory.class);
		formBuilder.setComponentFactory(mockComponentFactory);
		
		mockInterceptorFactory = (FormComponentInterceptorFactory) EasyMock.createMock(FormComponentInterceptorFactory.class);
		formBuilder.setFormComponentInterceptorFactory(mockInterceptorFactory);
		
		additionalSetUp();
	}
	
	protected abstract AbstractFormBuilder createFormBuilder(BindingFactory bindingFactory);

	protected void additionalSetUp() throws Exception {
		
	}
}
