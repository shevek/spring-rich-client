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

package org.springframework.richclient.form;

import java.util.List;

import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.factory.ControlFactory;

/**
 * @author Keith Donald
 */
public interface Form extends ControlFactory {
	public String getId();

	public ValidatingFormModel getFormModel();

	public Object getFormObject();

	public void setFormObject(Object formObject);

	public Object getValue(String formProperty);

	public ValueModel getValueModel(String formProperty);

	public void addValidationListener(ValidationListener listener);

	public void removeValidationListener(ValidationListener listener);

	/**
	 * Create a {@link SimpleValidationResultsReporter} for this form, sending
	 * input to the given {@link Messagable}.
	 * 
	 * @param messageAreaPane the message receiver used by the created
	 * resultsReporter.
	 * @return a new ResultsReporter.
	 */
	public ValidationResultsReporter newSingleLineResultsReporter(Messagable messageAreaPane);

	public void addGuarded(Guarded guarded);
	
	public void addGuarded(Guarded guarded, int mask);
	
	public void removeGuarded(Guarded guarded);
	
	/**
	 * @return The list of ValidationResultsReporters of this Form.
	 */
	public List getValidationResultsReporters();

	/**
	 * Add a ValidationResultsReporter to this Form.
	 * 
	 * @param validationResultsReporter
	 */
	public void addValidationResultsReporter(ValidationResultsReporter validationResultsReporter);

	/**
	 * Remove a ValidationResultsReporter from this Form.
	 * 
	 * @param validationResultsReporter
	 */
	public void removeValidationResultsReporter(ValidationResultsReporter validationResultsReporter);

	/**
	 * Add a child to this Form. Models and available ResultsReporters will be
	 * coupled as well.
	 * 
	 * @param form The childForm to add.
	 */
	public void addChildForm(Form form);

	/**
	 * Remove a child from this Form. Models and available ResultsReporters will
	 * be decoupled as well.
	 * 
	 * @param form The childForm to remove.
	 */
	public void removeChildForm(Form form);

	public boolean hasErrors();

	public void commit();

	public void revert();
	
	public void reset();
}