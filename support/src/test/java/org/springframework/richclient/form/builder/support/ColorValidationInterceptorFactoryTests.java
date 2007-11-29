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
package org.springframework.richclient.form.builder.support;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.springframework.richclient.form.builder.support.ColorValidationInterceptorFactory.ColorChanger;

import junit.framework.TestCase;

/**
 * Testcase for ColorValidationInterceptorFactory
 * 
 * @author Peter De Bruycker
 */
public class ColorValidationInterceptorFactoryTests extends TestCase {
	public void testColorChanger() {
		Color errorColor = Color.RED;
		JComponent component = new JTextField();
		Color backgroundColor = component.getBackground();
		
		ColorChanger colorChanger = new ColorChanger(component, errorColor);
		
		colorChanger.setEnabled(false);
		assertEquals(errorColor, component.getBackground());
		
		colorChanger.setEnabled(true);
		assertEquals(backgroundColor, component.getBackground());
		
		colorChanger.setEnabled(false);
		component.setBackground(Color.GREEN);
		assertEquals("error color must always be shown if enabled = false", errorColor, component.getBackground());
		
		colorChanger.setEnabled(true);
		assertEquals(Color.GREEN, component.getBackground());
	}
}
