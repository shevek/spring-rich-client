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
 *
 */

package org.springframework.richclient.form;

import javax.swing.JComponent;

import org.springframework.richclient.factory.ControlFactory;
import org.springframework.richclient.form.binding.BindingFactory;

/**
 * Allows pre-generated form UIs to easily integrate with Spring Rich's form
 * and binding framework.  Typically, these pre-generated form UIs are from
 * 3rd party form designers (Matisse, JFormDesigner, etc).
 *
 * @author Andy DePue
 * @author Peter De Bruycker
 * @author Christophe GADAIX
 */
public interface FormUIProvider extends ControlFactory {

  /**
   * Produces the pre-generated form as a single Swing component.
   */
  JComponent getControl();

  /**
   * Binds the fields and other components in this pre-generated form to a
   * Spring {@link Form form} by using the specified {@link BindingFactory}.
   * 
   * @param factory the <code>BindingFactory</code> this form provider should
   *        use to bind the provided form.
   * @param form the <code>Form</code> being bound.
   */
  void bind(BindingFactory factory, Form form);

  /**
   * Provides access to individual components of this pre-generated form.
   * Components are referenced by id.  The "id" of a component can be any
   * arbitrary String agreed upon between the designer and the developer, but
   * typically these IDs will be the same as the property names of the
   * object backing the form.
   *
   * @param componentId component id to lookup
   *
   * @return component with the specified id, or <code>null</code> if no
   *         component exists in this pre-generated form with the given id.
   */
  JComponent getComponent(String componentId);
}
