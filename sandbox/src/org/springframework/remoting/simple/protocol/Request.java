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
package org.springframework.remoting.simple.protocol;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
    private String serviceInterfaceName;
    private String methodDesc;
    private Object[] args; 
    private Map metaData;
    
    public Request(Class serviceInterface, Method method, Object[] args) {
        this.serviceInterfaceName = serviceInterface.getName();
        this.methodDesc = method.toString();
        this.args = args;
    }

    public Object getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void putMetaData(Object key, Object value) {
        getMetaData().put(key, value);
    }
    
    public Object getMetaData(Object key) {
        return getMetaData().get(key);
    }
    
    private Map getMetaData() {
        if (metaData == null) {
            metaData = new HashMap();
        }
        return metaData;
    }
}