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
package org.springframework.richclient.util;

import java.util.Collection;

import org.springframework.rules.Function;
import org.springframework.rules.UnaryProcedure;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class CollectionRefresher implements UnaryProcedure {

    private Function refreshFunction;
    
    public CollectionRefresher(Function refreshFunction) {
        Assert.notNull(refreshFunction);
        this.refreshFunction = refreshFunction;
    }
    
    /**
     * @see org.springframework.rules.UnaryProcedure#run(java.lang.Object)
     */
    public void run(Object argument) {
       Collection c = (Collection)argument;
       c.clear();
       c.addAll((Collection)refreshFunction.evaluate());
    }

}
