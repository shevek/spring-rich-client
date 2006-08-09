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
package org.springframework.binding.support;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.form.support.UserMetadata;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author Mathias Broekelmann
 * 
 */
public class ProtectedFieldTests extends SpringRichTestCase {

    public static class AnnotatedTestBean {
        private String password;

        @ProtectedField
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    public void testAnnotation() throws Exception {
        FormModel model = new DefaultFormModel(new AnnotationAwareBeanPropertyAccessStrategy(new AnnotatedTestBean()));
        assertEquals(true, model.getFieldMetadata("password").getUserMetadata(UserMetadata.PROTECTED_FIELD));
    }
}
