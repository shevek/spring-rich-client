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
package org.springframework.richclient.command.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 * An implementation of <code>ActionCommandExecutor</code> that delegates
 * job execution to a specified (static or non-static) method for command 
 * execution.
 *   
 * @author Oliver Hutchison
 * @see org.springframework.beans.support.MethodInvoker
 */
public class MethodInvokingActionCommandExecutor extends ArgumentConvertingMethodInvoker implements
        ActionCommandExecutor, InitializingBean {

    protected static final Log logger = LogFactory.getLog(MethodInvokingActionCommandExecutor.class);

    public void afterPropertiesSet() throws Exception {
        prepare();
    }

    public void execute() {
        try {
            invoke();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            logger.warn("Could not invoke method '" + getTargetMethod() + "' on target object [" + getTargetObject()
                    + "]", e);
        }
    }

}