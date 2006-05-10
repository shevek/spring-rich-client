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
package org.springframework.richclient.application.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.closure.support.AbstractConstraint;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentListener;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.Assert;

/**
 * @author Peter De Bruycker
 */
public abstract class AbstractApplicationPage implements ApplicationPage {

	private final EventListenerListHelper pageComponentListeners = new EventListenerListHelper(
			PageComponentListener.class);

	private final ViewDescriptorRegistry viewDescriptorRegistry = (ViewDescriptorRegistry) ApplicationServicesLocator.services()
            .getService(ViewDescriptorRegistry.class);

	private final Set pageComponents = new LinkedHashSet();

	private PageComponent activeComponent;

	private SharedCommandTargeter sharedCommandTargeter;

	private PageDescriptor descriptor;

	private ApplicationWindow window;

	public AbstractApplicationPage() {
		
	}
	
	public AbstractApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
		setApplicationWindow(window);
		setDescriptor(pageDescriptor);
	}

	protected PageComponent findPageComponent(final String viewDescriptorId) {
		return (PageComponent) new AbstractConstraint() {

			public boolean test(Object arg) {
				if (arg instanceof View) {
					return ((View) arg).getId().equals(viewDescriptorId);
				}
				return false;
			}
		}.findFirst(pageComponents);
	}

	public void addPageComponentListener(PageComponentListener listener) {
		pageComponentListeners.add(listener);
	}

	public void removePageComponentListener(PageComponentListener listener) {
		pageComponentListeners.remove(listener);
	}

	protected void fireOpened(PageComponent component) {
		component.componentOpened();
		pageComponentListeners.fire("componentOpened", component);
	}

	protected void fireFocusGained(PageComponent component) {
		component.componentFocusGained();
		pageComponentListeners.fire("componentFocusGained", component);
	}

	protected void setActiveComponent() {
		if (pageComponents.size() > 0) {
			setActiveComponent((PageComponent) pageComponents.iterator().next());
		}
	}

	protected ViewDescriptor getViewDescriptor(String viewDescriptorId) {
		return viewDescriptorRegistry.getViewDescriptor(viewDescriptorId);
	}

	public PageComponent getActiveComponent() {
		return activeComponent;
	}

	public void setActiveComponent(PageComponent pageComponent) {
		// if pageComponent is already active, don't do anything
		if (this.activeComponent == pageComponent) {
			return;
		}

		if (this.activeComponent != null) {
			fireFocusLost(this.activeComponent);
		}
		giveFocusTo(pageComponent);
		this.activeComponent = pageComponent;
		fireFocusGained(this.activeComponent);
	}

	protected void fireFocusLost(PageComponent component) {
		component.componentFocusLost();
		pageComponentListeners.fire("componentFocusLost", component);
	}

	protected abstract boolean giveFocusTo(PageComponent pageComponent);

	protected void fireClosed(PageComponent component) {
		component.componentClosed();
		pageComponentListeners.fire("componentClosed", component);
	}

	public String getId() {
		return descriptor.getId();
	}

	public ApplicationWindow getWindow() {
		return window;
	}

	public void close(PageComponent pageComponent) {
		if (pageComponent == activeComponent) {
			fireFocusLost(pageComponent);
			activeComponent = null;
		}
		pageComponents.remove(pageComponent);
		pageComponent.dispose();
		fireClosed(pageComponent);
		if (activeComponent == null) {
			setActiveComponent();
		}
	}
	
	public boolean close() {
		for (Iterator iter = new HashSet(pageComponents).iterator(); iter.hasNext();) {
			PageComponent component = (PageComponent) iter.next();
			close(component);
		}
		return true;
	}

	public void showView(ViewDescriptor viewDescriptor) {
		PageComponent component = findPageComponent(viewDescriptor.getId());
		if (component == null) {
			component = createPageComponent(viewDescriptor);

			addPageComponent(component);
		}
		setActiveComponent(component);
	}

	public void openEditor(Object editorInput) {
		// todo
	}

	public boolean closeAllEditors() {
		// todo
		return true;
	}

	protected void addPageComponent(PageComponent pageComponent) {
		pageComponents.add(pageComponent);
		fireOpened(pageComponent);
	}

	/**
	 * Creates a PageComponent for the given PageComponentDescriptor.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @return the created PageComponent
	 */
	protected abstract PageComponent createPageComponent(PageComponentDescriptor descriptor);

	public void showView(String viewDescriptorId) {
		showView(getViewDescriptor(viewDescriptorId));
	}

	public Set getPageComponents() {
		return Collections.unmodifiableSet(pageComponents);
	}

	public final void setApplicationWindow(ApplicationWindow window) {
		Assert.notNull(window, "The containing window is required");
		Assert.state(this.window == null, "Page window already set: it should only be set once, during initialization");
		this.window = window;
		sharedCommandTargeter = new SharedCommandTargeter(window);
		addPageComponentListener(sharedCommandTargeter);
	}

	public final void setDescriptor(PageDescriptor descriptor) {
		Assert.notNull(descriptor, "The page's descriptor is required");
		Assert.state(this.descriptor == null,
				"Page descriptor already set: it should only be set once, during initialization");
		this.descriptor = descriptor;
	}

	protected PageDescriptor getPageDescriptor() {
		return descriptor;
	}
}