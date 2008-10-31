/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.rules.closure.support;

import org.springframework.core.enums.ShortCodedLabeledEnum;

/**
 * Enumeration of possible process statuses.
 *
 * @author Keith Donald
 */
public class ProcessStatus extends ShortCodedLabeledEnum {

    private static final long serialVersionUID = 1L;

    /** Process created. */
	public static final ProcessStatus CREATED = new ProcessStatus(0, "Created");

	/** Process is running. */
	public static final ProcessStatus RUNNING = new ProcessStatus(1, "Running");

	/** Process has stopped. */
	public static final ProcessStatus STOPPED = new ProcessStatus(2, "Stopped");

	/** Process has completed. */
	public static final ProcessStatus COMPLETED = new ProcessStatus(3, "Completed");

	/** Process has been reset. */
	public static final ProcessStatus RESET = new ProcessStatus(4, "Reset");

	/**
	 * Private constructor because this is a typesafe enum!
	 */
	private ProcessStatus(int code, String label) {
		super(code, label);
	}
}