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
package org.springframework.richclient.security;

import javax.swing.JComponent;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * This class provides a simple form for capturing a username and password from the user.
 * It also generates an {@link Authentication} token from the entered values.
 *  
 * @author Larry Streepy
 * @see #getAuthentication()
 */
public class LoginForm extends AbstractForm {
    private static final String FORM_ID = "credentials";

    private LoginDetails loginDetails;

    private JComponent usernameField;

    /**
     * Constructor.
     */
    public LoginForm() {
        super( FORM_ID );

        loginDetails = createLoginDetails();
        setFormModel( FormModelHelper.createUnbufferedFormModel( loginDetails ) );
    }

    /**
     * Set the user name in the form.
     * @param userName to install
     */
    public void setUserName(String userName) {
        if( isControlCreated() ) {
            getValueModel(LoginDetails.PROPERTY_USERNAME).setValue(userName);
        } else {
            loginDetails.setUsername(userName);
        }
    }

    /**
     * Get an Authentication token that contains the current username and password.
     * @return authentication token
     */
    public Authentication getAuthentication() {
        return new UsernamePasswordAuthenticationToken( loginDetails.getUsername(), loginDetails.getPassword() );
    }

    /**
     * Create the form object to hold our login information.
     * @return constructed form object
     */
    protected LoginDetails createLoginDetails() {
        return new LoginDetails();
    }

    /**
     * Construct the form with the username and password fields.
     */
    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder( getBindingFactory() );
        usernameField = formBuilder.add( LoginDetails.PROPERTY_USERNAME )[1];
        formBuilder.row();
        formBuilder.addPasswordField( LoginDetails.PROPERTY_PASSWORD );
        return formBuilder.getForm();
    }

    public boolean requestFocusInWindow() {
        return usernameField.requestFocusInWindow();
    }

}