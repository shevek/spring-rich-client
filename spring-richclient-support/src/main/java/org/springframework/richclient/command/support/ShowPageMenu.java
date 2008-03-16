/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.command.support;

import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.util.Assert;

/**
 * A menu containing a collection of sub-menu items that each display a given
 * page.
 *
 * @author Keith Donald
 * @author Rogan Dawes
 */
public class ShowPageMenu extends CommandGroup implements ApplicationWindowAware {

	/** The identifier of this command. */
	public static final String ID = "showPageMenu";

	private ApplicationWindow window;

	/**
	 * Creates a new {@code ShowPageMenu} with an id of {@value #ID}.
	 */
	public ShowPageMenu() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationWindow(ApplicationWindow window) {
		this.window = window;
	}

	/**
	 * Called after dependencies have been set, populates this menu with action
	 * command objects that will each show a given page when executed. The
	 * collection of 'show page' commands will be determined by querying the
	 * {@link PageDescriptorRegistry} retrieved from {@link ApplicationServices}.
	 */
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Assert.notNull(window, "Application window cannot be null.");
		populate();
	}

	private void populate() {
		PageDescriptorRegistry pageDescriptorRegistry = (PageDescriptorRegistry) ApplicationServicesLocator.services()
				.getService(PageDescriptorRegistry.class);

		PageDescriptor[] pages = pageDescriptorRegistry.getPageDescriptors();
		for (int i = 0; i < pages.length; i++) {
			PageDescriptor page = pages[i];
			addInternal(page.createShowPageCommand(window));
		}
	}
}