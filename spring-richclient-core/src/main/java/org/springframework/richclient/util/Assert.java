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
package org.springframework.richclient.util;

/**
 *
 * A convenience extension to Spring's {@link org.springframework.util.Assert}
 * class. This is mainly for use within the framework.
 *
 * @author Oliver Hutchinson
 * @author Kevin Stembridge
 *
 */
public class Assert extends org.springframework.util.Assert {

	/**
	 * Assert that an object required; that is, it is not null.
	 *
	 * <pre>
	 * required(clazz, &quot;class&quot;);
	 * </pre>
	 *
	 * @param object the object to check
	 * @param name the name of the object being checked
	 * @throws IllegalArgumentException if the object is <code>null</code>
	 */
	public static void required(Object object, String name) {
		if (object == null) {
			throw new IllegalArgumentException(name + " is required; it cannot be null");
		}
	}

	/**
	 * Confirms that the given array is not null and that all of its elements
	 * are not null also.
	 *
	 * @param array The array whose elements, if any, must all be non-null.
	 * @param arrayName The property name of the array, only used for display
	 * purposes. May be null.
	 *
	 * @throws IllegalArgumentException if the given array is null or if any of
	 * its elements are null.
	 */
	public static void noElementsNull(Object[] array, String arrayName) {

		if (arrayName == null) {
			arrayName = "array";
		}

		required(array, arrayName);

		for (int i = 0; i < array.length; i++) {
			required(array[i], arrayName + "[" + i + "]");
		}

	}

}