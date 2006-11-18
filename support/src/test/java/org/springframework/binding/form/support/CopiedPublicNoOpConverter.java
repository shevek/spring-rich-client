/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.binding.form.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.support.AbstractConverter;

/**
 * HACK: Copy from the package private converter
 * org.springframework.binding.convert.support.NoOpConverter
 * of the spring-binding dependency.
 *
 * Converter that is a "no op".
 * 
 * @author Keith Donald
 */
public class CopiedPublicNoOpConverter extends AbstractConverter {

	private Class sourceClass;

	private Class targetClass;

	public CopiedPublicNoOpConverter(Class sourceClass, Class targetClass) {
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
	}

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		return source;
	}

	public Class[] getSourceClasses() {
		return new Class[] { sourceClass };
	}

	public Class[] getTargetClasses() {
		return new Class[] { targetClass };
	}
}
