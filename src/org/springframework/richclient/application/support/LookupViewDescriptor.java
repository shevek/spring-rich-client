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

import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.View;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.support.ShowViewCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;

/**
 * An implementation of {@link ViewDescriptor} that instantiates new Views
 * by looking them up in a Spring {@link BeanFactory}.  You must set the bean
 * name to lookup in the <code>BeanFactory</code> via
 * {@link #setViewPrototypeBeanName(String)}, and the referenced bean
 * <b>must</b> be a declared non-singleton in the <code>BeanFactory</code>
 * (a prototype bean).
 * 
 * @author Andy DePue
 */
public class LookupViewDescriptor extends LabeledObjectSupport
    implements ViewDescriptor, BeanNameAware, BeanFactoryAware, InitializingBean
{
  private String id;
  private String viewPrototypeBeanName;
  private BeanFactory beanFactory;

  public void setId(final String id)
  {
    Assert.notNull(id);
    this.id = id;
  }

  public String getViewPrototypeBeanName()
  {
    return this.viewPrototypeBeanName;
  }

  public void setViewPrototypeBeanName(final String viewPrototypeBeanName)
  {
    this.viewPrototypeBeanName = viewPrototypeBeanName;
  }

  public BeanFactory getBeanFactory()
  {
    return this.beanFactory;
  }

  protected View createView()
  {
    final View view = (View)getBeanFactory().getBean(getViewPrototypeBeanName(), View.class);
    view.setDescriptor(this);
    return view;
  }
  
  
  //
  // METHODS FROM INTERFACE ViewDescriptor
  //
  
  public String getId()
  {
    return this.id;
  }

  public ActionCommand createShowViewCommand(final ApplicationWindow window)
  {
    return new ShowViewCommand(this, window);
  }

  public CommandButtonLabelInfo getShowViewCommandLabel()
  {
    return getLabel();
  }

  public PageComponent createPageComponent()
  {
    return createView();
  }
  


  //
  // METHODS FROM INTERFACE BeanNameAware
  //

  public void setBeanName(final String name)
  {
    setId(name);
  }
  
  
  //
  // METHODS FROM INTERFACE BeanFactoryAware
  //

  public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
  {
    this.beanFactory = beanFactory;
  }


  //
  // METHODS FROM INTERFACE InitializingBean
  //

  public final void afterPropertiesSet() throws Exception
  {
    initViewDescriptor();

    if(getViewPrototypeBeanName() == null) {
      throw new IllegalArgumentException("viewPrototypeBeanName property must be set.");
    }
    if(getBeanFactory() == null) {
      throw new IllegalArgumentException("beanFactory property must be set.");
    }
    if(!getBeanFactory().containsBean(getViewPrototypeBeanName())) {
      throw new IllegalArgumentException("There is no bean in the bean factory with the given name '" + getViewPrototypeBeanName() + "'");
    }
    if(getBeanFactory().isSingleton(getViewPrototypeBeanName())) {
      throw new IllegalArgumentException("View bean '" + getViewPrototypeBeanName() + "' must be a prototype (singleton=\"false\").");
    }
    if(!View.class.isAssignableFrom(getBeanFactory().getType(getViewPrototypeBeanName()))) {
      throw new IllegalArgumentException("Prototype View bean '" + getViewPrototypeBeanName() + "' does not implement the View interface.");
    }
  }


  /**
   * Subclasses may override this method to perform intialization after
   * properties have been set.
   * @throws Exception
   */
  protected void initViewDescriptor() throws Exception
  {
  }
}
