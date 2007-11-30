/*
 * Copyright 2002-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.config;

import javax.swing.JButton;

/**
 * Test case for <code>ToolBarCommandButtonConfigurer</code>
 * 
 * @author Peter De Bruycker
 */
public class ToolBarCommandButtonConfigurerTests extends CommandButtonConfigurerTestCase {

	public void testDefaults() {
		ToolBarCommandButtonConfigurer configurer= new ToolBarCommandButtonConfigurer();
		
		assertFalse(configurer.isShowText());
		assertTrue(configurer.isTextBelowIcon());
	}
	
	public void testConfigureWithDefaults() {
		ToolBarCommandButtonConfigurer configurer = new ToolBarCommandButtonConfigurer();
		JButton button = new JButton();

		configurer.configure(button, null, getCommandFaceDescriptor());

		assertEquals(null, button.getText());
		assertEquals(getCommandFaceDescriptor().getIcon(), button.getIcon());
		assertEquals(getCommandFaceDescriptor().getCaption(), button.getToolTipText());
	}
	
	public void testConfigureWithShowTextTrue() {
		ToolBarCommandButtonConfigurer configurer = new ToolBarCommandButtonConfigurer();
		configurer.setShowText(true);
		
		JButton button = new JButton();

		configurer.configure(button, null, getCommandFaceDescriptor());

		assertEquals(getCommandFaceDescriptor().getText(), button.getText());
		assertEquals(getCommandFaceDescriptor().getIcon(), button.getIcon());
		assertEquals(getCommandFaceDescriptor().getCaption(), button.getToolTipText());
		
		assertEquals(JButton.BOTTOM, button.getVerticalTextPosition());
		assertEquals(JButton.CENTER, button.getHorizontalTextPosition());
	}
	
	public void testConfigureWithShowTextTrueAndTextBelowIconFalse() {
		ToolBarCommandButtonConfigurer configurer = new ToolBarCommandButtonConfigurer();
		configurer.setShowText(true);
		configurer.setTextBelowIcon(false);
		
		JButton button = new JButton();

		configurer.configure(button, null, getCommandFaceDescriptor());

		assertEquals(getCommandFaceDescriptor().getText(), button.getText());
		assertEquals(getCommandFaceDescriptor().getIcon(), button.getIcon());
		assertEquals(getCommandFaceDescriptor().getCaption(), button.getToolTipText());
		
		assertEquals(JButton.CENTER, button.getVerticalTextPosition());
		assertEquals(JButton.TRAILING, button.getHorizontalTextPosition());
	}
	
	protected CommandButtonConfigurer createConfigurer() {
		return new ToolBarCommandButtonConfigurer();
	}
}
