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
package org.springframework.richclient.application;

import java.beans.PropertyEditor;

/**
 * This provides a central place to register PropertyEditors.
 * 
 * @author Jim Moore
 */
public interface PropertyEditorRegistry {

	/**
	 * Set the class to use for the {@link PropertyEditor}of classes of a given
	 * type.
	 * 
	 * @param typeClass
	 *            the type of class to use the property editor for
	 * @param propertyEditorClass
	 *            the class to create a PropertyEditor from
	 */
	public void setPropertyEditor(Class typeClass, Class propertyEditorClass);

	/**
	 * Set the class to use for the {@link PropertyEditor}for the given class
	 * property.
	 * 
	 * @param objectType
	 *            the class that the property is for
	 * @param propertyName
	 *            the name of the property
	 * @param propertyEditorClass
	 *            the class to create a PropertyEditor from
	 */
	public void setPropertyEditor(Class objectType, String propertyName, Class propertyEditorClass);

	/**
	 * Get the {@link PropertyEditor}to use for the given class.
	 * 
	 * @param typeClass
	 *            the type of class to use the property editor for
	 * 
	 * @return the PropertyEditor to use; otherwise null
	 */
	public PropertyEditor getPropertyEditor(Class typeClass);

	/**
	 * Get the {@link PropertyEditor}to use for the given class property.
	 * 
	 * @param objectType
	 *            the class that the property is for
	 * @param propertyName
	 *            the name of the property
	 * 
	 * @return the PropertyEditor to use; otherwise null
	 */
	public PropertyEditor getPropertyEditor(Class objectType, String propertyName);

}