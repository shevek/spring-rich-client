/*
 * Copyright 2005 the original author or authors.
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
package com.jidesoft.spring.richclient.googledemo.command;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Form that obtains a query string
 * 
 * @author Jonny Wray
 *
 */
public class SearchForm extends AbstractForm{

	private static final String ID = "searchForm";
    private JComponent textArea;

	public SearchForm(FormModel formModel) {
        super(formModel, ID);
    }
	
	protected JComponent createFormControl(){

        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        textArea = formBuilder.add("queryString")[0];
        return formBuilder.getForm();
	}
	
    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();
    }
}
