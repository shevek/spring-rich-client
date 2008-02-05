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

/**
 * A service locator that can retrieve instances of a service by class type.
 *
 * @author Larry Streepy
 */
public interface ApplicationServices {

	/**
	 * Returns the application service of the requested type (class).
	 *
	 * @param serviceType Type of service to locate
	 * @return A service implementation of the requested type. Never null.
	 * @throws IllegalArgumentException if {@code serviceType} is null.
	 * @throws ServiceNotFoundException if there is no service known for the
	 * given serviceType.
	 */
	Object getService(Class serviceType);

	/**
	 * Determine if a service of the requested type is available.
	 *
	 * @param serviceType Type of service to locate
	 * @return true if service is available, false if not
	 * @throws IllegalArgumentException if {@code serviceType} is null.
	 */
	boolean containsService(Class serviceType);

}