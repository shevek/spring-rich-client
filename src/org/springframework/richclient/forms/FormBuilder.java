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
package org.springframework.richclient.forms;

import javax.swing.JComponent;

import org.springframework.richclient.factory.ControlFactory;

/**
 * Common interface to assist with building forms.
 * 
 * @author Keith Donald
 */
public interface FormBuilder {
    public static final int LABEL_INDEX = 0;

    public static final int LABELED_INDEX = 1;

    public JComponent getForm();

    public JComponent[] add(String labelKey, JComponent labeledComponent);

    public JComponent[] add(String labelKey, String labelConstraints, JComponent labeledComponent);

    public void addSeparator();

    public void addGapRow();

    public void addRow(ControlFactory controlFactory);
}