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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Web controller that exports the specified service bean as a SimpleRemoting
 * service endpoint, accessible via a SimpleRemoting proxy.
 * 
 * @author Oliver Hutchison
 * @see SimpleProxyFactoryBean
 */
public class SimpleServiceExporter extends RemoteExporter implements Controller {

    private SimpleServiceInvoker serviceInvoker;

    private List services;

    public void setServices(Map serviceMap) {
        ClassEditor ce = new ClassEditor();
        services = new ArrayList(serviceMap.size());
        for (Iterator i = serviceMap.entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();
            Object serviceObject = (Object) entry.getKey();
            if (serviceObject == null) { throw new IllegalArgumentException(
                    "Service object is null."); }
            String serviceInterfaceName = (String) entry.getValue();
            ce.setAsText(serviceInterfaceName);
            Class serviceInterface = (Class) ce.getValue();
            services.add(new SimpleService(serviceObject, serviceInterface));
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (services == null) {
            //super.afterPropertiesSet();
            services = new ArrayList(1);
        }
        if (getService() != null || getServiceInterface() != null) {
            services
                    .add(new SimpleService(getService(), getServiceInterface()));
        }
        serviceInvoker = new SimpleServiceInvoker(services);
    }

    /**
     * Process the incoming SimpleRemoting request and create a SimpleRemoting
     * response.
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            serviceInvoker.invoke(request.getInputStream(), response
                    .getOutputStream());
        }
        catch (Exception ex) {
            throw ex;
        }
        catch (Error ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new ServletException(ex);
        }
        return null;
    }

    public Object getService() {
        return super.getService();
    }
}