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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.core.Guarded;
import org.springframework.rules.values.FormModel;
import org.springframework.rules.values.ValidationEvent;
import org.springframework.rules.values.ValidationListener;

/**
 * @author Keith Donald
 */
public class FormGuard implements ValidationListener {

    private static final Log logger = LogFactory
            .getLog(FormGuard.class);

    private Guarded guarded;

    public FormGuard(FormModel formModel, Guarded guarded) {
        this.guarded = guarded;
        update(formModel);
    }

    public void constraintSatisfied(ValidationEvent event) {
        update(event.getFormModel());
    }

    public void constraintViolated(ValidationEvent event) {
        update(event.getFormModel());
    }

    protected void update(FormModel formModel) {
        if (formModel.hasErrors()) {
            guarded.setEnabled(false);
        }
        else {
            guarded.setEnabled(true);
        }
    }

}