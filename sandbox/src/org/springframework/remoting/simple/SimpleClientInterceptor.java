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

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.remoting.simple.transport.Transport;

/**
 * @author oliverh
 */
public class SimpleClientInterceptor implements MethodInterceptor {

    private Class serviceInterface;

    private Transport transport;

    public SimpleClientInterceptor(Class serviceInterface, Transport transport) {
        this.serviceInterface = serviceInterface;
        this.transport = transport;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        Object[] args = invocation.getArguments();

        if (!serviceImplementsMethod(method)) {
            if ("equals".equals(methodName) && paramTypes.length == 1 && paramTypes[0].equals(Object.class)) {
                return Boolean.valueOf(checkEqual(invocation.getThis()));
            }
            else if ("hashCode".equals(methodName) && paramTypes.length == 0) {
                return new Integer(serviceInterface.hashCode() ^ transport.hashCode());
            }
            throw new SimpleRemotingException("Method [" + method.toString()
                    + "] is not provided by serviceInterface [" + serviceInterface + "].");
        }

        return transport.invokeRemoteMethod(serviceInterface, method, args);
    }

    protected Class getServiceInterface() {
        return serviceInterface;
    }

    protected Transport getTransport() {
        return transport;
    }

    private boolean serviceImplementsMethod(Method method) {
        Method[] methods = serviceInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEqual(Object o2) {
        if (o2 instanceof Advised) {
            Advisor[] advisors = ((Advised)o2).getAdvisors();
            for (int i = 0; i < advisors.length; i++) {
                Advice advice = advisors[i].getAdvice();
                if (advice instanceof SimpleClientInterceptor) {
                    SimpleClientInterceptor interceptor2 = (SimpleClientInterceptor)advice;
                    return serviceInterface.equals(interceptor2.getServiceInterface())
                            && transport.equals(interceptor2.getTransport());
                }
            }
        }
        return false;
    }
}