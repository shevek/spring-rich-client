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
package org.springframework.remoting.simple;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oliverh
 */
public class SimpleService {
    private Object serviceObject;

    private Class serviceInterface;

    private Map methodMap;

    public SimpleService(Object serviceObject, Class serviceInterface) {
        if (!serviceInterface.isInterface()) { throw new IllegalArgumentException(
                "serviceInterface [" + serviceInterface.getName()
                        + "] must be an interface."); }
        if (!serviceInterface.isInstance(serviceObject)) { throw new IllegalArgumentException(
                "serviceInterface [" + serviceInterface.getName()
                        + "] needs to be implemented by service ["
                        + serviceObject + "]."); }
        this.serviceObject = serviceObject;
        this.serviceInterface = serviceInterface;
        populateMethodMap();
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public Class getServiceInterface() {
        return serviceInterface;
    }

    public Method getServiceMethod(String methodDesc) {
        Method method = (Method) methodMap.get(methodDesc);
        if (method == null) { throw new SimpleRemotingException("Service ["
                + serviceInterface.getName() + "] does not implement method ["
                + methodDesc + "]."); }
        return method;
    }

    private void populateMethodMap() {
        Method[] interfaceMethods = serviceInterface.getMethods();
        methodMap = new HashMap(interfaceMethods.length);
        Class serviceObjectClass = serviceObject.getClass();
        for (int i = 0; i < interfaceMethods.length; i++) {
            Method interfaceMethod = interfaceMethods[i];
            try {
                Method serviceMethod = serviceObjectClass.getMethod(
                        interfaceMethod.getName(), interfaceMethod
                                .getParameterTypes());
                String methodDesc = interfaceMethod.toString();
                methodMap.put(methodDesc, serviceMethod);
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Service object ["
                        + serviceObject + "] does not implement method ["
                        + interfaceMethod + "].");
            }
        }
    }
}

