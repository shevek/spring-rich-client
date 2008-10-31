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
package org.springframework.rules.closure;

import org.springframework.rules.closure.support.AbstractClosure;
import org.springframework.util.Assert;

/**
 * Conveneince support class for binary closures. Overrides call(argument) to
 * treat the passed argument as an array with two elements.
 * 
 * @author Keith Donald
 */
public abstract class AbstractBinaryClosure extends AbstractClosure implements
        BinaryClosure {
    public Object call(Object argument) {
        if (argument == null) {
            argument = new Object[0];
        }
        Assert.isTrue(argument.getClass().isArray(),
                "Binary argument must be an array");
        Object[] arguments = (Object[])argument;
        Assert.isTrue(arguments.length == 2,
                "Binary argument must contain 2 elements");
        return call(arguments[0], arguments[1]);
    }

}