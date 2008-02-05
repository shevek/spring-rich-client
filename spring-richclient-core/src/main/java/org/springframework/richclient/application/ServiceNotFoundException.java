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
 */
package org.springframework.richclient.application;

/**
 * Indicates that an application service of a given type could not be found.
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public class ServiceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4732373005271272964L;

	private final Class serviceClass;

	private static String createDefaultMessage(Class serviceClass) {

		if (serviceClass != null) {
			return "Unable to locate an application service of type [" + serviceClass.getName() + "]";
		}
		else {
			return "Unable to locate an application service. The type of the service is either "
					+ "unknown or was not provided when this exception was created.";
		}

	}

	/**
	 * Creates a new {@code ServiceNotFoundException}. A default message
	 * containing the name of the given class will be used.
	 *
	 * @param serviceClass The class of the application service that could not
	 * be located.
	 */
	public ServiceNotFoundException(Class serviceClass) {
		super(createDefaultMessage(serviceClass));
		this.serviceClass = serviceClass;
	}

	/**
	 * Creates a new {@code ServiceNotFoundException} with the given detail
	 * message and nested exception.
	 *
	 * @param message The detail message.
	 * @param serviceClass The class of the application service that could not
	 * be located.
	 * @param cause An optional nested exception that occurred attempting to
	 * locate the service.
	 */
	public ServiceNotFoundException(String message, Class serviceClass) {
		super(message);
		this.serviceClass = serviceClass;
	}

	/**
	 * Creates a new {@code ServiceNotFoundException} with the given detail
	 * message and nested exception.
	 *
	 * @param message The detail message.
	 * @param serviceClass The class of the application service that could not
	 * be located.
	 * @param cause An optional nested exception that occurred attempting to
	 * locate the service.
	 */
	public ServiceNotFoundException(String message, Class serviceClass, Throwable cause) {
		super(message, cause);
		this.serviceClass = serviceClass;
	}

	/**
	 * Returns class of the application service that could not be located.
	 * @return Returns the service class.
	 */
	public Class getServiceClass() {
		return this.serviceClass;
	}

}