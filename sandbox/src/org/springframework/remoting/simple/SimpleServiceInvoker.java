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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.remoting.simple.SimpleRemotingException.Recoverable;
import org.springframework.remoting.simple.protocol.Protocol;
import org.springframework.remoting.simple.protocol.Reply;
import org.springframework.remoting.simple.protocol.Request;
import org.springframework.remoting.simple.protocol.DefaultProtocol;

/**
 * 
 * 
 * @author oliverh
 */
public class SimpleServiceInvoker {

    private Map serviceMap;

    private Protocol protocol;

    public SimpleServiceInvoker(List services) {
        protocol = new DefaultProtocol();
        populateServiceMap(services);
    }

    private void populateServiceMap(List services) {
        if (services.size() < 1) {
            throw new IllegalArgumentException(
                    "At least 1 service is required.");
        }
        serviceMap = new HashMap(services.size());
        for (Iterator i = services.iterator(); i.hasNext();) {
            Service service = (Service) i.next();
            String serviceInterfaceName = service.getServiceInterface()
                    .getName();
            if (serviceMap.containsKey(serviceInterfaceName)) {
                throw new IllegalArgumentException(
                        "More than one service implements service interface ["
                                + serviceInterfaceName + "].");
            }
            serviceMap.put(serviceInterfaceName, service);
        }
    }

    public void invoke(InputStream is, OutputStream os) throws IOException {
        Object returnedValue = null;
        
        Throwable returnedThrowable = null;
        SimpleRemotingException serverException = null;

        try {
            Request request = protocol.readRequest(is, Recoverable.YES);

            Service service = (Service) serviceMap.get(request
                    .getServiceInterfaceName());
            if (service == null) {
                throw new SimpleRemotingException(
                        "Unrecognized service interface ["
                                + request.getServiceInterfaceName() + "].");
            }
            Method serviceMethod = (Method) service.getServiceMethod(request
                    .getMethodDesc());

            returnedValue = serviceMethod.invoke(service.getServiceObject(),
                    request.getArgs());
        } catch (SimpleRemotingException e) {
            serverException = e;
        } catch (IllegalAccessException e) {
            serverException = new SimpleRemotingException(
                    "Unable to access service method", e);
        } catch (IllegalArgumentException e) {
            serverException = new SimpleRemotingException(
                    "Unable to invoke service method", e);
        } catch (InvocationTargetException e) {
            returnedThrowable = e.getTargetException();
        }

        if (serverException != null) {
            protocol.writeException(os, serverException);
        } else {
            protocol.writeReply(os, new Reply(returnedValue, returnedThrowable));
        }
    }

    public static class Service {
        private Object serviceObject;

        private Class serviceInterface;

        private Map methodMap;

        public Service(Object serviceObject, Class serviceInterface) {
            if (!serviceInterface.isInterface()) {
                throw new IllegalArgumentException("serviceInterface ["
                        + serviceInterface.getName()
                        + "] must be an interface.");
            }
            if (!serviceInterface.isInstance(serviceObject)) {
                throw new IllegalArgumentException("serviceInterface ["
                        + serviceInterface.getName()
                        + "] needs to be implemented by service ["
                        + serviceObject + "].");
            }
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
            if (method == null) {
                throw new SimpleRemotingException("Service ["
                        + serviceInterface.getName()
                        + "] does not implement method [" + methodDesc + "].");
            }
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
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Service object ["
                            + serviceObject + "] does not implement method ["
                            + interfaceMethod + "].");
                }
            }
        }
    }
}