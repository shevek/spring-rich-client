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
package org.springframework.binding;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

/**
 * A strategy for accessing a domain object's properties. Different
 * implementations could access different backing objects such as a javabean
 * <code>(BeanPropertyAccessStrategy)</code>, hashmap, rowset, or other data
 * structure.
 * 
 * @author Keith Donald
 */
public interface PropertyAccessStrategy {

	/**
	 * Get the value of a property.
	 * 
	 * @param propertyPath name of the property to get the value of
	 * @return the value of the property
	 * @throws FatalBeanException if there is no such property, if the property
	 * isn't readable, or if the property getter throws an exception.
	 */
	Object getPropertyValue(String propertyPath) throws BeansException;

	/**
	 * Get a metadata accessor, which can return meta information about
	 * particular properties of the backed domain object.
	 * 
	 * @return The meta accessor.
	 */
	PropertyMetadataAccessStrategy getMetadataAccessStrategy();

	/**
	 * Return the target, backing domain object for which property access
	 * requests are targeted against.
	 * 
	 * @return The backing target object.
	 */
	Object getDomainObject();

}