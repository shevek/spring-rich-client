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

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.simple.protocol.Request;
import org.springframework.remoting.simple.transport.Authentication;
import org.springframework.remoting.simple.transport.AuthenticationCallback;
import org.springframework.remoting.simple.transport.HttpTransport;
import org.springframework.remoting.simple.transport.Transport;
import org.springframework.remoting.simple.transport.UsernamePasswordAuthentication;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

/**
 * @author oliverh
 */
public class SimpleProxyFactoryBean extends UrlBasedRemoteAccessor implements InitializingBean, FactoryBean {

    private Object serviceProxy;

    private URL serviceUrl;

    private String userName;

    private String password;

    private Transport transport;

    /**
     * Sets the user name to be used for HTTP BASIC authentication.
     * 
     * @param username
     *            the user name
     */
    public void setUsername(String username) {
        this.userName = username;
    }

    /**
     * Sets the password to be used for HTTP BASIC authentication.
     * 
     * @param user
     *            the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the transport for this interceptor.
     * 
     * @param transport
     *            the transport
     */
    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    /**
     * @return the transport for this interceptor
     */
    public Transport getTransport() {
        return transport;
    }

    public void afterPropertiesSet() throws MalformedURLException {
        if (getServiceInterface() == null) {
            throw new IllegalArgumentException("serviceInterface is required.");
        }
        if (getServiceUrl() == null) {
            throw new IllegalArgumentException("serviceUrl is required.");
        }
        serviceUrl = new URL(getServiceUrl());

        if (transport == null) {
            transport = new HttpTransport(serviceUrl);
        }
        transport.setAuthenticationCallback(new AuthenticationCallback() {
            public Authentication authenticate(Request request) {
                if (userName != null && password != null) {
                    return new UsernamePasswordAuthentication(userName, password);
                }
                return null;
            }
        });

        this.serviceProxy = ProxyFactory.getProxy(getServiceInterface(), new SimpleClientInterceptor(
                getServiceInterface(), transport));
    }

    public Object getObject() {
        return this.serviceProxy;
    }

    public Class getObjectType() {
        return (this.serviceProxy != null) ? this.serviceProxy.getClass() : getServiceInterface();
    }

    public boolean isSingleton() {
        return true;
    }
}