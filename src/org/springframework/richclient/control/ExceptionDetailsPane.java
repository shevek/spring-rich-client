/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.richclient.control;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.richclient.factory.AbstractControlFactory;

/**
 * A simple pane which can display an exception stack trace.
 * 
 * @author Keith Donald
 */
public class ExceptionDetailsPane extends AbstractControlFactory {
	private JTextArea exceptionDetails;

	public void setException(Exception e) {
		createControlIfNecessary();
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		exceptionDetails.setText(writer.toString());
	}

	/**
	 * @see org.springframework.richclient.factory.AbstractControlFactory#createControl()
	 */
	protected JComponent createControl() {
		exceptionDetails = new JTextArea();
		exceptionDetails.setEditable(false);
		exceptionDetails.setRows(10);
		exceptionDetails.setColumns(80);
		JScrollPane sp = new JScrollPane(exceptionDetails);
		return sp;
	}

}