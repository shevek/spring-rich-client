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

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.support.ShowViewCommand;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.Assert;

/**
 * Provides a standard implementation of {@link ViewDescriptor}.
 * 
 * @author Keith Donald
 */
public class DefaultViewDescriptor extends LabeledObjectSupport implements InitializingBean, ViewDescriptor,
		BeanNameAware {
	private String id;

	private Class viewClass;

	private Map viewProperties;

	public void setBeanName(String beanName) {
		setId(beanName);
	}

	public void setId(String id) {
		Assert.notNull("id is required");
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setViewClass(Class viewClass) {
		this.viewClass = viewClass;
	}

	public void setViewProperties(Map viewProperties) {
		this.viewProperties = viewProperties;
	}

	public ApplicationEventMulticaster getApplicationEventMulticaster() {
		if (getApplicationContext() != null) {
			final String beanName = AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;
			if (getApplicationContext().containsBean(beanName)) {
				return (ApplicationEventMulticaster)getApplicationContext().getBean(beanName);
			}
		}
		return null;
	}

	public void afterPropertiesSet() {
		Assert.notNull(viewClass, "The viewClass property must be specified");
	}

	public View createView() {
		Object o = BeanUtils.instantiateClass(viewClass);
		Assert.isTrue((o instanceof View), "View class '" + viewClass + "' was instantiated, but instance is not a View!");
		View view = (View)o;
		view.setDescriptor(this);
		if (view instanceof ApplicationListener && getApplicationEventMulticaster() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Registering new view instance '" + getDisplayName() + "' as an application event listener...");
			}
			getApplicationEventMulticaster().addApplicationListener((ApplicationListener)view);
		}
		if (viewProperties != null) {
			BeanWrapper wrapper = new BeanWrapperImpl(view);
			wrapper.setPropertyValues(viewProperties);
		}

		if (view instanceof InitializingBean) {
			try {
				((InitializingBean)view).afterPropertiesSet();
			}
			catch (Exception e) {
				throw new BeanInitializationException("Problem running on " + view, e);
			}
		}

		return view;
	}

	public CommandButtonLabelInfo getShowViewCommandLabel() {
		return getLabel();
	}

	public ActionCommand createShowViewCommand(ApplicationWindow window) {
		return new ShowViewCommand(this, window);
	}

}