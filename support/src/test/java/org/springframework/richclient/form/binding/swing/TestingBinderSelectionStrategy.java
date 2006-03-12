/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.form.binding.swing;

import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binding;

/**
 * @author Oliver Hutchison
 */
public class TestingBinderSelectionStrategy implements BinderSelectionStrategy {

    public Binder selectBinder(FormModel formModel, String propertyName) {
        return new TestableBinder(null);
    }

    public Binder selectBinder(Class controlType, FormModel formModel, String propertyName) {
        return new TestableBinder(controlType);
    }
    
    private class TestableBinder implements Binder {
        private Class controlType; 

        public TestableBinder(Class controlType) {
            this.controlType = controlType;
        }

        public Binding bind(FormModel formModel, String formPropertyPath, Map context) {
            return new TestableBinding(controlType, null, formModel,formPropertyPath, context);
        }

        public Binding bind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
            return new TestableBinding(controlType, control, formModel,formPropertyPath, context);
        }

        public Class getRequiredSourceClass() {
            // TODO Auto-generated method stub
            return null;
        }        
    }
}