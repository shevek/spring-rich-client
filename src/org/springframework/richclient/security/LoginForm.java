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

import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.AuthenticationManager;

import org.springframework.richclient.forms.AbstractForm;
import org.springframework.richclient.forms.BeanFormBuilder;
import org.springframework.richclient.forms.JGoodiesBeanFormBuilder;
import org.springframework.richclient.forms.SwingFormModel;

import com.jgoodies.forms.layout.FormLayout;

public class LoginForm extends AbstractForm {
    private SessionDetails sessionDetails;

    private JComponent usernameField;

    public LoginForm(AuthenticationManager manager) {
        super("general");
        this.sessionDetails = createSessionDetails();
        setFormModel(SwingFormModel.createFormModel(this.sessionDetails));
        setAuthenticationManager(manager);
    }

    protected SessionDetails createSessionDetails() {
        return new SessionDetails();
    }

    public void setAuthenticationManager(AuthenticationManager manager) {
        sessionDetails.setAuthenticationManager(manager);
    }

    protected JComponent createFormControl() {
        FormLayout layout = new FormLayout("left:pref, 5dlu, pref:grow");
        BeanFormBuilder formBuilder = new JGoodiesBeanFormBuilder(getFormModel(), layout);
        this.usernameField = formBuilder.add(SessionDetails.PROPERTY_USERNAME)[1];
        formBuilder.addPasswordField(SessionDetails.PROPERTY_PASSWORD);
        return formBuilder.getForm();
    }

    public void commit() throws AuthenticationException {
        super.commit();
        sessionDetails.login();
    }

    public boolean requestFocusInWindow() {
        return usernameField.requestFocusInWindow();
    }

}