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
package org.springframework.richclient.factory;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.support.ApplicationServicesAccessor;

/**
 * A control factory that only creates it's control when requested.
 * 
 * @author Keith Donald
 */
public abstract class AbstractControlFactory extends ApplicationServicesAccessor implements ControlFactory {

    protected Log logger = LogFactory.getLog(getClass());

    private boolean singleton = true;

    private JComponent control;

    protected final boolean isSingleton() {
        return singleton;
    }

    protected final void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public final JComponent getControl() {
        if (isSingleton()) {
            if (control == null) {
                this.control = createControl();
            }
            return control;
        }
        else {
            return createControl();
        }
    }

    public final boolean isControlCreated() {
        if (isSingleton()) {
            return control != null;
        }
        else {
            return false;
        }
    }

    protected void createControlIfNecessary() {
        if (isSingleton() && control == null) {
            getControl();
        }
    }

    protected abstract JComponent createControl();
}