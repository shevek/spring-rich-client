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
package org.springframework.richclient.dialog;

import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.springframework.richclient.forms.FormBuilder;
import org.springframework.richclient.forms.JGoodiesFormBuilder;
import org.springframework.richclient.forms.SimpleValidationResultsReporter;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.rules.UnaryPredicate;
import org.springframework.rules.UnaryProcedure;
import org.springframework.util.Assert;

import com.jgoodies.forms.layout.FormLayout;

/**
 * Simple input application dialog consisting of a label and a text field for
 * accepting input.
 * 
 * @author Keith Donald
 */
public class InputApplicationDialog extends ApplicationDialog {

    private String inputLabelMessage = "dialog.input";

    private JComponent inputField;

    private UnaryPredicate inputConstraint;

    private UnaryProcedure finishAction;

    private SimpleMessageAreaPane reporter;

    private SwingFormModel formModel;

    public InputApplicationDialog(Object bean, String propertyName) {
        this(bean, propertyName, true);
    }

    public InputApplicationDialog(Object bean, String propertyName,
            boolean bufferChanges) {
        this();
        this.formModel = SwingFormModel.createFormModel(bean, bufferChanges);
        setInputField(formModel.createBoundControl(propertyName));
    }

    public InputApplicationDialog() {
        this(null, null, CloseAction.DISPOSE);
    }

    public InputApplicationDialog(String title, Window parent) {
        this(title, parent, CloseAction.DISPOSE);
    }

    public InputApplicationDialog(String title, Window parent,
            CloseAction closeAction) {
        super(title, parent, closeAction);
        setResizable(true);
    }

    public void setInputField(JComponent field) {
        Assert.notNull(field);
        this.inputField = field;
    }

    public void setInputLabelMessage(String inputLabel) {
        Assert.hasText(inputLabel);
        this.inputLabelMessage = inputLabel;
    }

    public void setInputConstraint(UnaryPredicate constraint) {
        this.inputConstraint = constraint;
    }

    public void setFinishAction(UnaryProcedure procedure) {
        this.finishAction = procedure;
    }

    private SimpleMessageAreaPane getValidationReporter() {
        if (reporter == null) {
            this.reporter = new SimpleMessageAreaPane();
            if (this.formModel != null) {
                new SimpleValidationResultsReporter(formModel, this,
                        this.reporter);
            }
        }
        return reporter;
    }

    protected JComponent createDialogContentPane() {
        FormLayout layout = new FormLayout("left:pref, 6dlu, pref:grow");
        FormBuilder formBuilder = new JGoodiesFormBuilder(layout);

        if (this.inputField == null) {
            this.inputField = new JTextField(25);
        }
        // workaround for bug in jformatted text field for selectAll
        if (inputField instanceof JFormattedTextField) {
            inputField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ((JFormattedTextField)inputField).selectAll();
                        }
                    });
                }
            });
        }
        formBuilder.add(getInputLabelMessage(), inputField);
        formBuilder.addGapRow();
        formBuilder.addRow(getValidationReporter());
        formBuilder.addSeparator();
        return formBuilder.getForm();
    }

    private String getInputLabelMessage() {
        return inputLabelMessage;
    }

    protected boolean onFinish() {
        if (checkInputConstraint()) {
            onFinish(getInputValue());
            return true;
        }
        return false;
    }

    private boolean checkInputConstraint() {
        if (inputConstraint != null) {
            return inputConstraint.test(getInputValue());
        }
        else {
            return true;
        }
    }

    private Object getInputValue() {
        if (inputField instanceof JFormattedTextField) {
            return ((JFormattedTextField)inputField).getValue();
        }
        else if (inputField instanceof JTextComponent) {
            return ((JTextComponent)inputField).getText();
        }
        else {
            throw new IllegalStateException("Input field type not supported");
        }
    }

    protected void onFinish(Object inputValue) {
        if (formModel != null) {
            formModel.commit();
        }
        if (finishAction != null) {
            finishAction.run(inputValue);
        }
    }

}