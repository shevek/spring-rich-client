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
package org.springframework.richclient.samples.simple.ui;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Form to handle the properties of a Contact object. It uses a {@link TableFormBuilder}
 * to construct the layout of the form. Contact object properties are easily bound to UI
 * controls using the form builder's {@link TableFormBuilder#add(String)} method. The
 * platform takes care of determining which kind of control to create based on the type of
 * the property in question.
 * 
 * @author Larry Streepy
 * 
 */
public class ContactForm extends AbstractForm {

    public static final String FORM_NAME = "contact";

    private JComponent firstNameField;

    /**
     * @param pageFormModel
     */
    public ContactForm( FormModel formModel ) {
        super(formModel, FORM_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.form.AbstractForm#createFormControl()
     */
    protected JComponent createFormControl() {
        final SwingBindingFactory bf = (SwingBindingFactory) getBindingFactory();
        TableFormBuilder formBuilder = new TableFormBuilder(bf);
        formBuilder.setLabelAttributes("colGrId=label colSpec=right:pref");

        formBuilder.addSeparator("General");
        formBuilder.row();
        firstNameField = formBuilder.add("firstName")[1];
        formBuilder.add("lastName");
        formBuilder.row();
        formBuilder.add("dateOfBirth", "colSpan=1");
        formBuilder.row();
        formBuilder.add("homePhone");
        formBuilder.add("workPhone");
        formBuilder.row();
        formBuilder.add("emailAddress");
        formBuilder.row();
        formBuilder.row();
        formBuilder.add("contactType", "colSpan=1 align=left");
        formBuilder.row();
        formBuilder.addSeparator("Address");
        formBuilder.row();
        formBuilder.add("address.address1");
        formBuilder.row();
        formBuilder.add("address.address2");
        formBuilder.row();
        formBuilder.add("address.address3");
        formBuilder.row();
        formBuilder.add("address.city", "colSpan=1 align=left");
        formBuilder.row();
        // formBuilder.add( bf.createBoundComboBox( "address.state",
        // MasterLists.STATE_CODE ), "colSpan=1 align=left" );
        formBuilder.add("address.state", "colSpan=1 align=left");
        formBuilder.row();

        // We want to make the zip code UI field smaller than the default. The add method
        // returns an array of two components, the field label and the component bound to
        // the property.
        JComponent zipField = formBuilder.add("address.zip", "colSpan=1 align=left")[1];
        ((JTextField) zipField).setColumns(8);
        formBuilder.row();

        return formBuilder.getForm();
    }

    /**
     * Try to place the focus in the firstNameField whenever the initial focus is being
     * set.
     * 
     * @return
     */
    public boolean requestFocusInWindow() {
        return firstNameField.requestFocusInWindow();
    }
}
