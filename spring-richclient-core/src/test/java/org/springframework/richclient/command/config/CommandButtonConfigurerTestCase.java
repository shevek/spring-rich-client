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

import junit.framework.TestCase;

import org.springframework.richclient.image.EmptyIcon;

/**
 * Abstract base class for <code>CommandButtonConfigurer</code>
 * implementations
 * 
 * @author Peter De Bruycker
 */
public abstract class CommandButtonConfigurerTestCase extends TestCase {

	private CommandButtonConfigurer configurer;

	private CommandFaceDescriptor descriptor;

	public final void testConfigureWithNullDescriptor() {
		try {
			configurer.configure(new JButton(), null, null);
			fail("Should throw IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			pass();
		}
	}

	public final void testConfigureWithNullButton() {
		try {
			configurer.configure(null, null, descriptor);
			fail("Should throw IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			pass();
		}
	}

	private static void pass() {
		// test passes
	}

	protected final void setUp() throws Exception {
		configurer = createConfigurer();
		assertNotNull("Configurer cannot be null", configurer);

		descriptor = new CommandFaceDescriptor();
		CommandButtonIconInfo iconInfo = new CommandButtonIconInfo(EmptyIcon.SMALL);
		CommandButtonLabelInfo labelInfo = CommandButtonLabelInfo.valueOf("test");
		descriptor.setIconInfo(iconInfo);
		descriptor.setLabelInfo(labelInfo);
		descriptor.setCaption("Tool tip");
	}

	protected final CommandFaceDescriptor getCommandFaceDescriptor() {
		return descriptor;
	}

	protected abstract CommandButtonConfigurer createConfigurer();
}
